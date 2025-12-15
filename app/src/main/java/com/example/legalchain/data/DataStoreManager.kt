package com.example.legalchain.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates DataStore automatically
private val Context.dataStore by preferencesDataStore(name = "legalchain_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("pref_dark_mode")
        val KEY_LANGUAGE = stringPreferencesKey("pref_language")
    }

    // READ DARK MODE STATE
    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[KEY_DARK_MODE] ?: false   // default: false
        }

    // READ LANGUAGE STATE
    val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs ->
            prefs[KEY_LANGUAGE] ?: "en"     // default: English
        }

    // SAVE DARK MODE VALUE
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = isDark
        }
    }

    // SAVE LANGUAGE VALUE
    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = lang
        }
    }
}
