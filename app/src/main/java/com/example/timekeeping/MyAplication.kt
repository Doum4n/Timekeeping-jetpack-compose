package com.example.timekeeping

// MyApplication.kt
import android.app.Application
import com.example.timekeeping.utils.SessionManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        SessionManager.init(this)
    }
}