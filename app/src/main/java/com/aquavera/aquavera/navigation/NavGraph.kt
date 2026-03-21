package com.aquavera.aquavera.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aquavera.aquavera.RequestWaterActivity
import com.aquavera.aquavera.screens.*
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object LanguageSelection : Screen("language_selection")
    data object Login : Screen("login?email={email}") {
        fun createRoute(email: String = "") = "login?email=$email"
    }
    data object ForgotPassword : Screen("forgot_password")
    data object Otp : Screen("otp/{email}/{type}") {
        fun createRoute(email: String, type: String) = "otp/$email/$type"
    }
    data object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }
    data object SignUp : Screen("signup")
    data object ProfileSetup : Screen("profile_setup")
    data object Dashboard : Screen("dashboard")
    data object UserManagement : Screen("user_management")
    data object MyRequests : Screen("my_requests")
    data object LandSummary : Screen("land_summary")
    data object ProfileSummary : Screen("profile_summary")
    data object BillSummary : Screen("bill_summary")
    data object Notifications : Screen("notifications")
}

@Composable
fun AquaNavGraph(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = viewModel(),
    langViewModel: LangViewModel = viewModel()
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.LanguageSelection.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                langViewModel = langViewModel,
                onLanguageSelected = {
                    navController.navigate(Screen.Login.createRoute())
                }
            )
        }
        
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("email") { defaultValue = ""; type = NavType.StringType })
        ) { backStackEntry ->
            val initialEmail = backStackEntry.arguments?.getString("email") ?: ""
            LoginScreen(
                langViewModel = langViewModel,
                initialEmail = initialEmail,
                onLoginClick = { email, pass ->
                    appViewModel.login(email, pass) { success, _ ->
                        if (success) {
                            if (appViewModel.isProfileComplete()) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.ProfileSetup.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                langViewModel = langViewModel,
                onSendCode = { email ->
                    appViewModel.generateAndSendOtp(email) { success ->
                        if (success) {
                            navController.navigate(Screen.Otp.createRoute(email, "reset"))
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Otp.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            OtpScreen(
                email = email,
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onVerifySuccess = {
                    when (type) {
                        "reset" -> navController.navigate(Screen.ResetPassword.createRoute(email))
                        "signup" -> {
                            appViewModel.finalizeSignUp { success, _ ->
                                if (success) {
                                    navController.navigate(Screen.Login.createRoute(email)) {
                                        popUpTo(Screen.SignUp.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                        else -> {
                            if (appViewModel.isProfileComplete()) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.ProfileSetup.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onSaveSuccess = {
                    navController.navigate(Screen.Login.createRoute(email)) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onSignUpSuccess = {
                    navController.navigate(Screen.Otp.createRoute(it, "signup"))
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onNavigateToMyRequests = { navController.navigate(Screen.MyRequests.route) },
                onNavigateToLandSummary = { navController.navigate(Screen.LandSummary.route) },
                onNavigateToProfileSummary = { navController.navigate(Screen.ProfileSummary.route) },
                onNavigateToBillSummary = { navController.navigate(Screen.BillSummary.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToRequestWater = {
                    context.startActivity(Intent(context, RequestWaterActivity::class.java))
                }
            )
        }
        
        composable(Screen.MyRequests.route) {
            MyRequestsScreen(appViewModel, langViewModel, onBack = { navController.popBackStack() })
        }
        
        composable(Screen.LandSummary.route) {
            LandSummaryScreen(
                appViewModel = appViewModel,
                langViewModel = langViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.ProfileSetup.route) }
            )
        }
        
        composable(Screen.ProfileSummary.route) {
            ProfileSummaryScreen(appViewModel, langViewModel, onBack = { navController.popBackStack() })
        }
        
        composable(Screen.BillSummary.route) {
            BillSummaryScreen(appViewModel, langViewModel, onBack = { navController.popBackStack() })
        }
        
        composable(Screen.Notifications.route) {
            NotificationsScreen(langViewModel, onBack = { navController.popBackStack() })
        }
        
        composable(Screen.UserManagement.route) {
            UserManagementScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}
