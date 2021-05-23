package com.study.prepshala.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.study.prepshala.R
import kotlinx.android.synthetic.main.activity_phone_number.*

class PhoneNumberActivity : AppCompatActivity() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) {
            val intent = Intent(this, ChatHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        phoneBox.requestFocus()
        setListners()
    }

    private fun setListners() {
        continueBtn.setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("phoneNumber",phoneBox.text.toString())
            startActivity(intent)
        }
    }
}