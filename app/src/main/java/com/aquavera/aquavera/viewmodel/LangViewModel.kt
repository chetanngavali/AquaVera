package com.aquavera.aquavera.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LangViewModel : ViewModel() {
    var currentLanguage by mutableStateOf("en")
        private set

    private val translations = mapOf(
        "en" to mapOf(
            "app_name" to "AquaVera",
            "farmer_portal" to "Farmer portal",
            "farmer_registration" to "Farmer registration",
            "login" to "Log in",
            "signup" to "Sign up",
            "email" to "Email address",
            "phone" to "Phone number",
            "password" to "Password",
            "forgot_password" to "Forgot password?",
            "no_account" to "Don't have an account? ",
            "already_account" to "Already have an account? ",
            "full_name" to "Full name",
            "email_address" to "Email address",
            "phone_number" to "Phone number",
            "confirm_password" to "Confirm password",
            "create_account" to "Create account",
            "choose_language" to "Choose your language",
            "continue" to "Continue",
            "dashboard" to "Dashboard",
            "requests" to "Requests",
            "users" to "Users",
            "operational_dashboard" to "Operational dashboard",
            "quick_access" to "Quick access",
            "overview" to "Overview",
            "request_water" to "Request water",
            "land_summary" to "Land summary",
            "bill_summary" to "Bill summary",
            "profile_summary" to "Profile summary",
            "total_requests" to "Total requests",
            "pending_approvals" to "Pending approvals",
            "total_bill" to "Total bill",
            "land_area" to "Land area",
            "recent_requests" to "Recent requests",
            "welcome_back" to "Welcome back",
            "no_requests" to "No requests yet",
            "notifications" to "Notifications",
            "profile" to "Profile",
            "reset_password" to "Reset password",
            "enter_email_reset" to "Enter your email to receive a password reset code",
            "send_code" to "Send code",
            "new_password" to "New password",
            "save_password" to "Save password",
            "otp_verification" to "OTP verification",
            "enter_otp" to "Enter the 6-digit code sent to your email",
            "verify" to "Verify",
            "resend_otp" to "Resend OTP"
        ),
        "mr" to mapOf(
            "app_name" to "AquaVera",
            "farmer_portal" to "शेतकरी पोर्टल",
            "farmer_registration" to "शेतकरी नोंदणी",
            "login" to "लॉगिन करा",
            "signup" to "साइन अप करा",
            "email" to "ईमेल पत्ता",
            "phone" to "फोन नंबर",
            "password" to "पासवर्ड",
            "forgot_password" to "पासवर्ड विसरलात?",
            "no_account" to "तुमचे खाते नाही का? ",
            "already_account" to "आधीच खाते आहे का? ",
            "full_name" to "पूर्ण नाव",
            "email_address" to "ईमेल पत्ता",
            "phone_number" to "फोन नंबर",
            "confirm_password" to "पासवर्डची पुष्टी करा",
            "create_account" to "खाते तयार करा",
            "choose_language" to "तुमची भाषा निवडा",
            "continue" to "पुढे जा",
            "dashboard" to "डॅशबोर्ड",
            "requests" to "विनंत्या",
            "users" to "वापरकर्ते",
            "operational_dashboard" to "कार्यक्षम डॅशबोर्ड",
            "quick_access" to "त्वरित प्रवेश",
            "overview" to "विहंगावलोकन",
            "request_water" to "पाण्याची विनंती करा",
            "land_summary" to "जमिनीचा सारांश",
            "bill_summary" to "बिलाचा सारांश",
            "profile_summary" to "प्रोफाइल सारांश",
            "total_requests" to "एकूण विनंत्या",
            "pending_approvals" to "प्रलंबित मंजूरी",
            "total_bill" to "एकूण बिल",
            "land_area" to "जमिनीचे क्षेत्रफळ",
            "recent_requests" to "अलीकडील विनंत्या",
            "welcome_back" to "स्वागत आहे",
            "no_requests" to "अद्याप विनंत्या नाहीत",
            "notifications" to "सूचना",
            "profile" to "प्रोफाइल",
            "reset_password" to "पासवर्ड रिसेट करा",
            "enter_email_reset" to "पासवर्ड रिसेट कोड मिळवण्यासाठी तुमचा ईमेल प्रविष्ट करा",
            "send_code" to "कोड पाठवा",
            "new_password" to "नवीन पासवर्ड",
            "save_password" to "पासवर्ड जतन करा",
            "otp_verification" to "OTP पडताळणी",
            "enter_otp" to "तुमच्या ईमेलवर पाठवलेला ६-अंकी कोड प्रविष्ट करा",
            "verify" to "पडताळणी करा",
            "resend_otp" to "OTP पुन्हा पाठवा"
        ),
        "hi" to mapOf(
            "app_name" to "AquaVera",
            "farmer_portal" to "किसान पोर्टल",
            "farmer_registration" to "किसान पंजीकरण",
            "login" to "लॉग इन करें",
            "signup" to "साइन अप करें",
            "email" to "ईमेल पता",
            "phone" to "फोन नंबर",
            "password" to "पासवर्ड",
            "forgot_password" to "पासवर्ड भूल गए?",
            "no_account" to "क्या आपका खाता नहीं है? ",
            "already_account" to "पहले से ही खाता है? ",
            "full_name" to "पूरा नाम",
            "email_address" to "ईमेल पता",
            "phone_number" to "फोन नंबर",
            "confirm_password" to "पासवर्ड की पुष्टि करें",
            "create_account" to "खाता बनाएं",
            "choose_language" to "अपनी भाषा चुनें",
            "continue" to "जारी रखें",
            "dashboard" to "डैशबोर्ड",
            "requests" to "अनुरोध",
            "users" to "उपयोगकर्ता",
            "operational_dashboard" to "परिचालन डैशबोर्ड",
            "quick_access" to "त्वरित पहुँच",
            "overview" to "विहंगावलोकन",
            "request_water" to "पानी का अनुरोध करें",
            "land_summary" to "भूमि सारांश",
            "bill_summary" to "बिल सारांश",
            "profile_summary" to "प्रोफ़ाइल सारांश",
            "total_requests" to "कुल अनुरोध",
            "pending_approvals" to "लंबित अनुमोदन",
            "total_bill" to "कुल बिल",
            "land_area" to "भूमि क्षेत्र",
            "recent_requests" to "हाल के अनुरोध",
            "welcome_back" to "स्वागत है",
            "no_requests" to "अभी तक कोई अनुरोध नहीं",
            "notifications" to "सूचनाएं",
            "profile" to "प्रोफ़ाइल",
            "reset_password" to "पासवर्ड रिसेट करें",
            "enter_email_reset" to "पासवर्ड रिसेट कोड प्राप्त करने के लिए अपना ईमेल दर्ज करें",
            "send_code" to "कोड भेजें",
            "new_password" to "नया पासवर्ड",
            "save_password" to "पासवर्ड सहेजें",
            "otp_verification" to "OTP सत्यापन",
            "enter_otp" to "अपने ईमेल पर भेजा गया 6-अंकीय कोड दर्ज करें",
            "verify" to "सत्यापित करें",
            "resend_otp" to "OTP पुनः भेजें"
        )
    )

    fun setLanguage(lang: String) {
        currentLanguage = lang
    }

    fun toggleLanguage() {
        currentLanguage = when (currentLanguage) {
            "en" -> "mr"
            "mr" -> "hi"
            else -> "en"
        }
    }

    fun t(key: String): String {
        return translations[currentLanguage]?.get(key) ?: key
    }
}
