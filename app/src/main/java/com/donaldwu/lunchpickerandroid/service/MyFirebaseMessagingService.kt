package com.donaldwu.lunchpickerandroid.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.donaldwu.lunchpickerandroid.server.Server

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val refreshedToken = token
        Log.i("logger", "refreshedToken = $refreshedToken")

        val currentToken = getCurrentTokenFromSharedPreferences()

        val response = Server.addTokenToServer(currentToken!!, refreshedToken)
        Log.i("logger", "response = ${response}")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.i("logger", "remoteMessage from = ${remoteMessage.from}")
        Log.i("logger", "remoteMessage notification title = ${remoteMessage.notification?.title}")
        Log.i("logger", "remoteMessage notification body = ${remoteMessage.notification?.body}")
    }

    private fun getCurrentTokenFromSharedPreferences(): String? {
        val prefs: SharedPreferences = applicationContext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        return prefs.getString("token", "")
    }
}
