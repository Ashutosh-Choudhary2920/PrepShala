package com.study.prepshala.chat

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.fullscreenImageDisplay.FullscreenDisplay
import kotlinx.android.synthetic.main.activity_display_individual_contacts.*

class DisplayIndividualContacts : AppCompatActivity() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var profileImageForIntent: String = ""
    var phoneNumber: String? = ""
    var profileImage: String? = ""
    var name: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_individual_contacts)

        changeNotificationbarColor()
        getViewsContent()
        setListners()
    }


    private fun getViewsContent() {
        name = intent.getStringExtra("userName")
        phoneNumber= intent.getStringExtra("userPhone")
        profileImage= intent.getStringExtra("profileImage")
        setViewsContent(name, phoneNumber, profileImage)
    }

    private fun setListners() {
        displayProfileImageView.setOnClickListener {
            val intent = Intent(this, FullscreenDisplay::class.java)
            intent.putExtra("imageUrl", profileImage)
            intent.putExtra("personName", name)
            startActivity(intent)
        }
    }

    private fun setViewsContent(name: String?, phoneNumber: String?, profileImage: String?) {
        profileImageForIntent = profileImage!!
        displayProfileName.setText(name)
        displayProfilePhone.setText(phoneNumber)
        logD(profileImage)
        Glide.with(this).load(profileImage).placeholder(R.drawable.avatar).into(displayProfileImageView)
    }

    private fun changeNotificationbarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.chatNotificationbar)
        }
    }
}