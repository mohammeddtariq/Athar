package com.athar.app.updater

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.athar.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Background worker that periodically checks GitHub for newly released Athar versions.
 * Dispatches a serene system notification when an update is available.
 */
class UpdateCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sp = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply()

            val result = AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
            if (result is UpdateCheckResult.UpdateAvailable) {
                AppUpdateManager.showUpdateNotification(applicationContext, result.releaseInfo)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "athar_periodic_update_check"
        private const val ONETIME_WORK_NAME = "athar_onetime_update_check"
        private const val PREFS_NAME = "athar_update_check_meta"
        private const val KEY_LAST_CHECK = "last_update_check_time"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun checkNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<UpdateCheckWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                ONETIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        suspend fun checkIfDue(context: Context, minIntervalMinutes: Long = 90) {
            val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastCheck = sp.getLong(KEY_LAST_CHECK, 0L)
            val now = System.currentTimeMillis()
            if (now - lastCheck < minIntervalMinutes * 60 * 1000L) {
                return
            }
            sp.edit().putLong(KEY_LAST_CHECK, now).apply()

            withContext(Dispatchers.IO) {
                try {
                    val result = AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
                    if (result is UpdateCheckResult.UpdateAvailable) {
                        AppUpdateManager.showUpdateNotification(context, result.releaseInfo)
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
