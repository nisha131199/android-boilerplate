package com.example.baseproject.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.baseproject.BuildConfig
import com.example.baseproject.storage.PreferenceHandler
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object Util {

    fun PreferenceHandler.getFCMToken() {
        //TODO(add firebase google.json file)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                writeString(this.fcmToken, task.result)
            })
    }

    fun openSetting(context: Context) {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
        )
    }

}