package com.aquavera.aquavera.utils

import android.util.Log

object EmailService {
    /**
     * Mock OTP service. Always returns true without sending an actual email.
     */
    suspend fun sendOTP(email: String, otp: String): Boolean {
        Log.d("EmailService", "MOCK: Sending OTP $otp to $email")
        return true
    }
}
