package com.aquavera.aquavera.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquavera.aquavera.utils.Billing
import com.aquavera.aquavera.utils.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class Farmer(
    val id: String? = null,
    val name: String = "",
    val survey_no: String = "",
    val land_area: Double = 0.0,
    val area_unit: String = "Acres",
    val state: String = "",
    val district: String = "",
    val taluka: String = "",
    val village: String = "",
    val plot_no: String = "",
    val is_setup: Boolean = false,
    val role: String = "User"
)

@Serializable
data class WaterRequest(
    val id: String = UUID.randomUUID().toString(),
    val farmer_id: String? = null,
    val crop_type: String,
    val season: String,
    val duration: Int = 0,
    val status: String = "Pending",
    val bill_amount: Double,
    val paid: Boolean = false,
    val created_at: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
)

class AppViewModel : ViewModel() {
    var profile by mutableStateOf(Farmer())
        private set

    val requests = mutableStateListOf<WaterRequest>()
    val allFarmers = mutableStateListOf<Farmer>()
    
    var generatedOtp by mutableStateOf("123456")
        private set

    private var tempEmail = ""
    private var tempPassword = ""
    private var tempFullName = ""

    fun signUp(emailStr: String, pass: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Mock OTP logic for now, can be replaced with real Supabase Auth OTP
                generatedOtp = "123456"
                tempEmail = emailStr
                tempPassword = pass
                tempFullName = fullName
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun finalizeSignUp(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.auth.signUpWith(Email) {
                    email = tempEmail
                    password = tempPassword
                }
                
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val newFarmer = Farmer(id = userId, name = tempFullName)
                    SupabaseConfig.client.postgrest["farmers"].insert(newFarmer)
                    profile = newFarmer
                }
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun generateAndSendOtp(emailStr: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                generatedOtp = "123456"
                tempEmail = emailStr
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

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
                onResult(false, "Invalid credentials or connection error")
            }
        }
    }

    fun resetPassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseConfig.client.auth.updateUser {
                    password = newPassword
                }
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to reset password")
            }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return@launch
                
                val farmerData = SupabaseConfig.client.postgrest["farmers"]
                    .select { filter { eq("id", userId) } }.decodeSingle<Farmer>()
                profile = farmerData

                val userRequests = SupabaseConfig.client.postgrest["water_requests"]
                    .select { filter { eq("farmer_id", userId) } }.decodeList<WaterRequest>()
                requests.clear()
                requests.addAll(userRequests.sortedByDescending { it.created_at })

                if (profile.role == "Admin") fetchAllFarmers()
            } catch (e: Exception) {
                Log.e("AppViewModel", "Sync Error", e)
            }
        }
    }

    private fun fetchAllFarmers() {
        viewModelScope.launch {
            try {
                val farmers = SupabaseConfig.client.postgrest["farmers"].select().decodeList<Farmer>()
                allFarmers.clear()
                allFarmers.addAll(farmers)
            } catch (e: Exception) {}
        }
    }

    fun updateProfile(
        name: String, 
        surveyNo: String, 
        landArea: Double, 
        areaUnit: String, 
        state: String, 
        district: String, 
        taluka: String, 
        village: String, 
        plotNo: String
    ) {
        viewModelScope.launch {
            try {
                val userId = SupabaseConfig.client.auth.currentUserOrNull()?.id ?: return@launch
                val updatedFarmer = profile.copy(
                    name = name,
                    survey_no = surveyNo,
                    land_area = landArea,
                    area_unit = areaUnit,
                    state = state,
                    district = district,
                    taluka = taluka,
                    village = village,
                    plot_no = plotNo,
                    is_setup = true
                )
                
                SupabaseConfig.client.postgrest["farmers"].update(updatedFarmer) {
                    filter { eq("id", userId) }
                }
                profile = updatedFarmer
            } catch (e: Exception) {
                Log.e("AppViewModel", "Update Error", e)
            }
        }
    }

    fun addRequest(cropType: String, season: String, duration: Int, onComplete: (WaterRequest) -> Unit) {
        viewModelScope.launch {
            try {
                val bill = Billing.calculateBill(season, duration, profile.land_area)
                val newRequest = WaterRequest(
                    farmer_id = profile.id,
                    crop_type = cropType,
                    season = season,
                    duration = duration,
                    status = "Pending",
                    bill_amount = bill
                )
                
                SupabaseConfig.client.postgrest["water_requests"].insert(newRequest)
                requests.add(0, newRequest)
                onComplete(newRequest)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Insert Error", e)
            }
        }
    }

    fun verifyOtp(inputOtp: String) = inputOtp == generatedOtp
    fun isProfileComplete() = profile.is_setup
    val totalBill: Double get() = requests.sumOf { it.bill_amount }
}
