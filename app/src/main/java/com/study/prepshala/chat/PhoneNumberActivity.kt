package com.study.prepshala.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.study.prepshala.R
import com.study.prepshala.Utils.toast
import kotlinx.android.synthetic.main.activity_phone_number.*

class PhoneNumberActivity : AppCompatActivity() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var countryCode: String
    private lateinit var phoneNumber: String
    private lateinit var alertDialogBuilder: MaterialAlertDialogBuilder

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
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + phoneBox.text.toString()

        if (validatePhoneNumber(phoneBox.text.toString())) {
            notifyUserBeforeVerify(
                "We will be verifying the phone number:$phoneNumber\n" +
                        "Is this OK, or would you like to edit the number?"
            )
        } else {
            toast("Please enter a valid number to continue!")
        }
    }

    private fun validatePhoneNumber(phone: String): Boolean {
        if (phone.isEmpty()) {
            return false
        }
        return true
    }

    private fun notifyUserBeforeVerify(message: String) {
        alertDialogBuilder = MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                val intent = Intent(this@PhoneNumberActivity, OTPActivity::class.java)
                intent.putExtra("phoneNumber",phoneNumber)
                startActivity(intent)
            }

            setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }
}