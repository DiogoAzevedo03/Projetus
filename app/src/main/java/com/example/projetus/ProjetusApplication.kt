package com.example.projetus

import android.app.Application

class ProjetusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleManager.applySavedLocale(this)
    }
}