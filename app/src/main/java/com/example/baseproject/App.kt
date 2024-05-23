package com.example.baseproject

import android.app.Application
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    private var instance: Application = this

    fun getContext() = instance

    companion object{
        val gson = Gson()
    }
}