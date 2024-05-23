package com.example.baseproject.utils

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

}