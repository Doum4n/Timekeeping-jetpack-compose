package com.example.timekeeping.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService

fun setUp(context: Context) {
    val channelId = "default_channel_id"
    val channelName = "Default Channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "This is the default notification channel."
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun sendNotification(context: Context) {
    setUp(context)  // Đảm bảo kênh thông báo đã được tạo

    val channelId = "default_channel_id"  // Đảm bảo khai báo channelId tại đây
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Thông báo tiêu đề")
        .setContentText("Đây là nội dung của thông báo.")
        .setSmallIcon(android.R.drawable.ic_notification_overlay)  // Đặt icon cho thông báo
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    notificationManager.notify(1, notification)
}

@Composable
fun NotificationButton() {
    val context = LocalContext.current
    Button(onClick = { sendNotification(context) }) {
        Text("Gửi thông báo")
    }
}

@Preview
@Composable
fun PreviewNotificationButton() {
    NotificationButton()
}
