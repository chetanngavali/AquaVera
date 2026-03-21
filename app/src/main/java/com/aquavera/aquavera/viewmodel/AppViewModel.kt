package com.aquavera.aquavera.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquavera.aquavera.utils.Billing
import com.aquavera.aquavera.utils.EmailService
import com.aquavera.aquavera.utils.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class Profile(
    val id: String? = null,
    val name: String = "",
    val aadhaar_last4: String = "",
    val land_id: String = "",
    val land_area: Double = 0.0,
    val area_unit: String = "Acres",
    val is_setup: Boolean = false,
    val role: String = "User"
)

@Serializable
data class WaterRequest(
    val id: String = UUID.randomUUID().toString(),
    val user_id: String? = null,
    val crop_type: String,
    val season: String,
    val duration: Int,
    val status: String,
    val bill_amount: Double,
    val paid: Boolean = false,
    val created_at: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
)

class AppViewModel : ViewModel() {
    var profile by mutableStateOf(Profile())
        private set

    val requests = mutableStateListOf<WaterRequest>()
    val allUsers = mutableStateListOf<Profile>()
    
    var generatedOtp by mutableStateOf("")
        private set

    private var tempEmail = ""
    private var tempPassword = ""
    private var tempFullName = ""

    // 1. Signup sends OTP via EmailJS
    fun signUp(emailStr: String, pass: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val otp = (100000..999999).random().toString()
                generatedOtp = otp
                val emailSent = EmailService.sendOTP(emailStr, otp)
                
                if (emailSent) {
                    tempEmail = emailStr
                    tempPassword = pass
                    tempFullName = fullName
                    onResult(true, null)
                } else {
                    onResult(false, "Connection error. Failed to send OTP.")
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    // 2. Verified OTP creates Auth User + Custom Profile Table entry
    fun finalizeSignUp(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.auth.signUpWith(Email) {
                    email = tempEmail
                    password = tempPassword
                    data = buildJsonObject { put("full_name", tempFullName) }
                }
                
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val newProfile = Profile(id = userId, name = tempFullName)
                    SupabaseConfig.client.postgrest["profiles"].insert(newProfile)
                    profile = newProfile
                }
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    // 3. Login uses standard Auth table
    fun login(emailStr: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.auth.signInWith(Email) {
                    email = emailStr
                    password = pass
                }
                fetchUserData()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, "Invalid credentials")
            }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return@launch
                
                // Fetch from custom profiles table
                val userProfile = SupabaseConfig.client.postgrest["profiles"]
                    .select { filter { eq("id", userId) } }.decodeSingle<Profile>()
                profile = userProfile

                // Fetch from custom water_requests table
                val userRequests = SupabaseConfig.client.postgrest["water_requests"]
                    .select { filter { eq("user_id", userId) } }.decodeList<WaterRequest>()
                requests.clear()
                requests.addAll(userRequests)

                if (profile.role == "Admin") fetchAllUsers()
            } catch (e: Exception) {
                Log.e("AppViewModel", "Sync Error", e)
            }
        }
    }

    private fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                val users = SupabaseConfig.client.postgrest["profiles"].select().decodeList<Profile>()
                allUsers.clear()
                allUsers.addAll(users)
            } catch (e: Exception) {}
        }
    }

    fun updateProfile(name: String, aadhaar: String, landId: String, area: Double, unit: String) {
        viewModelScope.launch {
            try {
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return@launch
                val updatedProfile = profile.copy(
                    name = name, aadhaar_last4 = aadhaar, land_id = landId, 
                    land_area = area, area_unit = unit, is_setup = true
                )
                SupabaseConfig.client.postgrest["profiles"].update(updatedProfile) { filter { eq("id", userId) } }
                profile = updatedProfile
            } catch (e: Exception) {}
        }
    }

    fun addRequest(cropType: String, season: String, duration: Int, onComplete: (WaterRequest) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return@launch
                val bill = Billing.calculateBill(season, duration, profile.land_area)
                val newRequest = WaterRequest(
                    user_id = userId, crop_type = cropType, season = season,
                    duration = duration, status = "Pending", bill_amount = bill
                )
                SupabaseConfig.client.postgrest["water_requests"].insert(newRequest)
                requests.add(0, newRequest)
                onComplete(newRequest)
            } catch (e: Exception) {}
        }
    }

    fun verifyOtp(inputOtp: String) = inputOtp == generatedOtp
    fun isProfileComplete() = profile.is_setup
    val totalBill: Double get() = requests.sumOf { it.bill_amount }
}
