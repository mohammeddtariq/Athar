package com.athar.app.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * One-tap location: fresh high-accuracy fix, provider checks, and the
 * system dialog that turns location on immediately (no manual trip to
 * settings). Backed by Google Play Services Location (free SDK).
 */
object LocationHelper {

    fun hasPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isProvidersOn(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Best-effort fresh location: high-accuracy single fix first,
     * Fused last location second, LocationManager last-known third.
     */
    suspend fun freshFix(context: Context): Pair<Double, Double>? =
        withContext(Dispatchers.IO) {
            if (!hasPermission(context)) return@withContext null
            // 1. Fresh high-accuracy fix (also warms up the chip for Qibla).
            withTimeoutOrNull(12_000) { currentFix(context) }?.let { return@withContext it }
            // 2. Fused last location.
            withTimeoutOrNull(4_000) { fusedLast(context) }?.let { return@withContext it }
            // 3. Platform last-known.
            platformLastKnown(context)
        }

    private suspend fun currentFix(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val tokenSource = com.google.android.gms.tasks.CancellationTokenSource()
                cont.invokeOnCancellation { tokenSource.cancel() }
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                    .addOnSuccessListener { loc ->
                        val value = if (loc != null) loc.latitude to loc.longitude else null
                        if (cont.isActive) cont.resumeWith(Result.success(value))
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resumeWith(Result.success(null))
                    }
            } catch (e: SecurityException) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            }
        }

    private suspend fun fusedLast(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            try {
                LocationServices.getFusedLocationProviderClient(context).lastLocation
                    .addOnSuccessListener { loc ->
                        val value = if (loc != null) loc.latitude to loc.longitude else null
                        if (cont.isActive) cont.resumeWith(Result.success(value))
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resumeWith(Result.success(null))
                    }
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            }
        }

    @Suppress("MissingPermission")
    private fun platformLastKnown(context: Context): Pair<Double, Double>? {
        return try {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
                try {
                    val loc = manager.getLastKnownLocation(provider)
                    if (loc != null) return loc.latitude to loc.longitude
                } catch (_: Exception) { /* next provider */ }
            }
            null
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Remembers a `requestEnableLocation()` launcher that pops the system
 * dialog turning location on with one tap. Calls [onEnabled] when the
 * providers are confirmed on.
 */
@Composable
fun rememberLocationEnabler(onEnabled: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) onEnabled()
    }
    return remember(context) {
        {
            try {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()
                val settings = LocationSettingsRequest.Builder()
                    .addLocationRequest(request)
                    .setAlwaysShow(true)
                    .build()
                LocationServices.getSettingsClient(context)
                    .checkLocationSettings(settings)
                    .addOnSuccessListener { onEnabled() }
                    .addOnFailureListener { e ->
                        val resolvable = e as? ResolvableApiException
                        if (resolvable != null) {
                            try {
                                launcher.launch(
                                    IntentSenderRequest.Builder(resolvable.resolution).build()
                                )
                            } catch (_: Exception) { /* dialog unavailable */ }
                        }
                    }
            } catch (_: Exception) { /* play services unavailable */ }
        }
    }
}
