package com.aquavera.aquavera.utils

import kotlin.math.round

object Billing {
    const val KHARIF_RATE = 5.5
    const val RABI_RATE = 11.0
    const val SUMMER_RATE = 16.5

    fun calculateBill(season: String, duration: Int, landArea: Double): Double {
        val rate = when (season.lowercase()) {
            "kharif" -> KHARIF_RATE
            "rabi" -> RABI_RATE
            "summer" -> SUMMER_RATE
            else -> 0.0
        }
        val bill = rate * duration * landArea
        return round(bill * 100) / 100.0
    }
}
