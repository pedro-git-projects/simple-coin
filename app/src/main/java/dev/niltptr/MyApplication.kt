package dev.niltptr

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize()
    }
}