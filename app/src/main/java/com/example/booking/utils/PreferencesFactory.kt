package com.example.booking.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.booking.config.ApiSettings

object PreferencesFactory {
    fun getPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences(ApiSettings.PREFERENCES_SETTINGS, Context.MODE_PRIVATE)
    }
}