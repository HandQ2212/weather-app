package com.app.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferenceStore(private val context: Context) {

    companion object {
        private val LOCATION_QUERY: Preferences.Key<String> = stringPreferencesKey("location_query")
    }

    val locationQueryFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LOCATION_QUERY] ?: ""
    }

    suspend fun saveLocationQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentQuery = preferences[LOCATION_QUERY] ?: ""
            if (currentQuery == query) return@edit
            preferences[LOCATION_QUERY] = query
        }
    }
}
