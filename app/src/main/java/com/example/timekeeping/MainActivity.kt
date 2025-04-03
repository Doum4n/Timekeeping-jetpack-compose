package com.example.timekeeping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.timekeeping.ui.themes.TimekeepingTheme

// MainActivity.kt (nếu dùng pure Compose)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimekeepingTheme {
                MainScreen()
            }
        }
    }
}