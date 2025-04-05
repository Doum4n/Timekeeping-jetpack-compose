package com.example.timekeeping.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object FormatDate {
        fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return formatter.format(date)
        }
    }
}