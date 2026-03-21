package com.aquavera.aquavera.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aquavera.aquavera.screens.*
import com.aquavera.aquavera.ui.theme.DrawerBackground
import com.aquavera.aquavera.ui.theme.PrimaryGreen
import com.aquavera.aquavera.viewmodel.AppViewModel
import com.aquavera.aquavera.viewmodel.LangViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object LanguageSelection : Screen("language_selection")
    data object Login : Screen("login")
    data object ForgotPassword : Screen("forgot_password")
    data object Otp : Screen("otp/{email}/{type}") {
        fun createRoute(email: String, type: String) = "otp/$email/$type"
    }
    data object ResetPassword : Screen("reset_password")
    data object SignUp : Screen("signup")
    data object ProfileSetup : Screen("profile_setup")
    data object Dashboard : Screen("dashboard")
    data object UserManagement : Screen("user_management")
}

@Composable
fun AquaNavGraph(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = viewModel(),
    langViewModel: LangViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isDrawerEnabled = currentRoute == Screen.Dashboard.route || currentRoute == Screen.UserManagement.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isDrawerEnabled,
        drawerContent = {
            if (isDrawerEnabled) {
                ModalDrawerSheet(
                    drawerContainerColor = DrawerBackground,
                    drawerContentColor = Color.White
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(langViewModel.t("app_name"), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(langViewModel.t("logged_in_as"), fontSize = 12.sp, color = Color.LightGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    NavigationDrawerItem(
                        label = { Text(langViewModel.t("dashboard")) },
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.Dashboard.route) {
                                navController.navigate(Screen.Dashboard.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White, 
                            unselectedIconColor = Color.White,
                            selectedContainerColor = Color(0xFF334155),
                            selectedTextColor = Color.White,
                            selectedIconColor = Color.White
                        )
                    )
                    NavigationDrawerItem(
                        label = { Text(langViewModel.t("user_management")) },
                        selected = currentRoute == Screen.UserManagement.route,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.UserManagement.route) {
                                navController.navigate(Screen.UserManagement.route)
                            }
                        },
                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White, 
                            unselectedIconColor = Color.White,
                            selectedContainerColor = Color(0xFF334155),
                            selectedTextColor = Color.White,
                            selectedIconColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = { Text(langViewModel.t("secure_logout")) },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.LanguageSelection.route) { popUpTo(0) } 
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = Color.White, 
                            unselectedIconColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) {
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
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    langViewModel = langViewModel,
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
                            "reset" -> navController.navigate(Screen.ResetPassword.route)
                            "signup" -> {
                                appViewModel.finalizeSignUp { success, _ ->
                                    if (success) {
                                        navController.navigate(Screen.Login.route) {
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

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    appViewModel = appViewModel,
                    langViewModel = langViewModel,
                    onSaveSuccess = {
                        navController.navigate(Screen.Login.route) {
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
                    langViewModel = langViewModel,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            
            composable(Screen.UserManagement.route) {
                UserManagementScreen(onBack = {
                    navController.popBackStack()
                })
            }
        }
    }
}
