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
 *
 * This is the fallback path: the primary trigger is [UpdateAlarmScheduler], which keeps
 * polling in Doze and is not deferred the way WorkManager jobs are.
 */
class UpdateCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            runCheck(applicationContext)
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

            val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(1, TimeUnit.HOURS)
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

        /** Checks GitHub and posts the (deduplicated) update notification if needed. */
        suspend fun runCheck(context: Context): UpdateCheckResult {
            val appContext = context.applicationContext
            val sp = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply()

            val result = try {
                AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
            } catch (e: Exception) {
                return UpdateCheckResult.Error(e.localizedMessage ?: "Network error")
            }

            if (result is UpdateCheckResult.UpdateAvailable) {
                AppUpdateManager.showUpdateNotification(appContext, result.releaseInfo)
            }
            return result
        }

        suspend fun checkIfDue(context: Context, minIntervalMinutes: Long = 90) {
            val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastCheck = sp.getLong(KEY_LAST_CHECK, 0L)
            val now = System.currentTimeMillis()
            if (now - lastCheck < minIntervalMinutes * 60 * 1000L) {
                return
            }
            withContext(Dispatchers.IO) {
                runCatching { runCheck(context) }
            }
        }
    }
}
