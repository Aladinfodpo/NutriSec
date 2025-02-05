package com.example.android.architecture.blueprints.nutrisecapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Extension property pour DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsDataStore {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val TODAY_ID_KEY = longPreferencesKey("today_id")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    private val USER_AGE_KEY = intPreferencesKey("user_age")

    suspend fun saveTodayId(dayId: Long) {
        appContext.dataStore.edit { preferences ->
            preferences[TODAY_ID_KEY] = dayId
        }
    }

    fun getTodayId(): Flow<Long> {
        return appContext.dataStore.data.map { preferences ->
            preferences[TODAY_ID_KEY] ?: 0}
    }

}
