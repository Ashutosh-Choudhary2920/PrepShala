package com.study.prepshala

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper


class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME : Long = 2100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        toDashboard()
    }

    fun toDashboard() {
        Handler(Looper.getMainLooper()).postDelayed( {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }, SPLASH_TIME)
    }
}