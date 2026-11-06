package com.solplus.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "solplus_preferences")

class AppPreferences(private val context: Context) {
    companion object {
        val FONT_SCALE_KEY = floatPreferencesKey("font_scale")
        val HIGH_CONTRAST_KEY = booleanPreferencesKey("high_contrast")
        val POSTPONE_MINUTES_KEY = intPreferencesKey("postpone_minutes")
        val LAST_BACKUP_TIMESTAMP_KEY = longPreferencesKey("last_backup_timestamp")
    }

    val fontScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[FONT_SCALE_KEY] ?: 1.2f // Padrão com acessibilidade reforçada
    }

    val highContrast: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HIGH_CONTRAST_KEY] ?: true
    }

    val postponeMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[POSTPONE_MINUTES_KEY] ?: 15
    }

    val lastBackupTimestamp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_BACKUP_TIMESTAMP_KEY] ?: 0L
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[FONT_SCALE_KEY] = scale }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { it[HIGH_CONTRAST_KEY] = enabled }
    }

    suspend fun setPostponeMinutes(minutes: Int) {
        context.dataStore.edit { it[POSTPONE_MINUTES_KEY] = minutes }
    }

    suspend fun setLastBackupTimestamp(timestamp: Long) {
        context.dataStore.edit { it[LAST_BACKUP_TIMESTAMP_KEY] = timestamp }
    }
}
