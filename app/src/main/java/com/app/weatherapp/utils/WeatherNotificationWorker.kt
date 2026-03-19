package com.app.weatherapp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.weatherapp.BuildConfig
import com.app.weatherapp.R
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.UserPreferenceStore
import com.app.weatherapp.data.model.NotificationEntity
import com.app.weatherapp.data.repository.WeatherRepository
import com.app.weatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.first

class WeatherNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val preferenceStore = UserPreferenceStore(context)
        val locationQuery = preferenceStore.locationQueryFlow.first().ifBlank { "Hanoi" }

        val repository = WeatherRepository(RetrofitInstance.api)
        val weatherResult = repository.getWeatherForecast(BuildConfig.WEATHER_API_KEY, locationQuery)

        return when (weatherResult) {
            is Resource.Success -> {
                val weather = weatherResult.data ?: return Result.retry()
                val summary = "${weather.location.name}: ${weather.current.temp_c.toInt()}°, ${weather.current.condition.text}"

                val notificationDao = AppDatabase.getDatabase(context).notificationDao()
                notificationDao.insertNotification(
                    NotificationEntity(
                        title = "Tóm tắt thời tiết hôm nay",
                        description = summary,
                        timeAgo = "Hôm nay",
                        iconType = "weather"
                    )
                )

                createChannelIfNeeded(context)
                showSystemNotification(context, summary)
                Result.success()
            }

            is Resource.Error -> Result.retry()
            is Resource.Loading -> Result.retry()
        }
    }

    private fun createChannelIfNeeded(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Weather Daily",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun showSystemNotification(context: Context, message: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Weather App")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "daily_weather_channel"
        private const val NOTIFICATION_ID = 1101
    }
}
