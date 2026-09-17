package com.athar.app.data

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Pure-math Qibla helpers (no external dependency). */
object QiblaCalculator {
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LNG = 39.8262

    /** Bearing in degrees clockwise from true north (0..360). */
    fun bearing(lat: Double, lng: Double): Double {
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val dLng = Math.toRadians(KAABA_LNG - lng)
        val y = sin(dLng)
        val x = cos(lat1) * kotlin.math.tan(lat2) - sin(lat1) * cos(dLng)
        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360
        return bearing
    }

    /** Great-circle distance in km (haversine). */
    fun distanceKm(lat: Double, lng: Double): Double {
        val r = 6371.0
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val dLat = lat2 - lat1
        val dLng = Math.toRadians(KAABA_LNG - lng)
        val h = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(kotlin.math.sqrt(h), kotlin.math.sqrt((1 - h).coerceAtLeast(0.0)))
        return r * c
    }

    fun degreesToCompass16(degrees: Double): String {
        val dirs = listOf(
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
        )
        val idx = ((degrees + 11.25) / 22.5).toInt() % 16
        return dirs[idx]
    }
}
