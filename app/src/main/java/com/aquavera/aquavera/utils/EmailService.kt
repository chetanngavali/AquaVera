package com.aquavera.aquavera.utils

import android.util.Log
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class EmailJSRequest(
    val service_id: String,
    val template_id: String,
    val user_id: String,
    val accessToken: String?,
    val template_params: Map<String, String>
)

object EmailService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("EmailService", message)
                }
            }
        }
    }

    private const val SERVICE_ID = "service_mc7z4in"
    private const val TEMPLATE_ID = "template_haux4wp"
    private const val PUBLIC_KEY = "wBI_PWYKcbtVD2deR"
    private const val PRIVATE_KEY = "nYr9nVtd-CuxrJUhmkBZR"

    suspend fun sendOTP(email: String, otp: String): Boolean {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 15)
            val expiryTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)

            Log.d("EmailService", "Sending OTP to: $email")
            
            val response: HttpResponse = client.post("https://api.emailjs.com/api/v1.0/email/send") {
                contentType(ContentType.Application.Json)
                setBody(
                    EmailJSRequest(
                        service_id = SERVICE_ID,
                        template_id = TEMPLATE_ID,
                        user_id = PUBLIC_KEY,
                        accessToken = PRIVATE_KEY,
                        template_params = mapOf(
                            "to_email" to email,
                            "passcode" to otp, // Matches {{passcode}} in your HTML
                            "time" to expiryTime, // Matches {{time}} in your HTML
                            "company_name" to "AquaVera" // You can use {{company_name}} now
                        )
                    )
                )
            }
            
            val responseBody = response.bodyAsText()
            Log.d("EmailService", "Status: ${response.status}, Body: $responseBody")
            
            response.status == HttpStatusCode.OK || responseBody.contains("OK")
        } catch (e: Exception) {
            Log.e("EmailService", "Network Error: ${e.message}", e)
            false
        }
    }
}
