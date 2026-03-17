package com.app.weatherapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.weatherapp.data.local.AppDatabase
import com.app.weatherapp.data.local.NotificationDao
import com.app.weatherapp.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationViewModel(
    private val notificationDao: NotificationDao
) : ViewModel() {
    val notifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotification()

    companion object {
        fun Factory(appContext: Context): ViewModelProvider.Factory =
            object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                        val db = AppDatabase.getDatabase(appContext)
                        val dao = db.notificationDao()
                        return NotificationViewModel(dao) as T
                    } else {
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            }
    }
}