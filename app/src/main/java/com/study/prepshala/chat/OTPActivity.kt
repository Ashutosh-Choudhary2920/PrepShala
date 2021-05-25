package com.study.prepshala.chat

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.models.User
import kotlinx.android.synthetic.main.activity_chat_home.*
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var verificationId: String? = ""
    var dialog: ProgressDialog? = null
    var phoneNumber: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        auth = FirebaseAuth.getInstance()
        //getListOfUsers()

        phoneNumber = intent.getStringExtra("phoneNumber")
        phoneLbl.setText("verify "+phoneNumber)
        logD("$phoneNumber")
        otp_view.requestFocus()
        verifyPhone(phoneNumber)
    }



    private fun verifyPhone(phoneNumber: String?) {
        logD("verify phone number function called")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    logD("inside onVerificationCompleted")
                    signInUser(phoneAuthCredential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                }

                override fun onCodeSent(verifyId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    logD("inside onCodeSent")
                    dialog!!.dismiss()
                    verificationId = verifyId
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    otp_view.requestFocus()
//                    otp_view.setOtpCompletionListener {
//
//                        var verificationCode: String = otp_view.text.toString()
//                        logD("virification code= $verificationCode")
//                        if (verifyId!!.isEmpty()){
//                            return@setOtpCompletionListener
//                        }
//                        //create a credential
////                        val credential = PhoneAuthProvider.getCredential(verifyId, verificationCode)
////                        logD("credential = $credential")
////                        signInUser(credential)
//                    }
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)

       otp_view.setOtpCompletionListener {
            var credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,it)
           logD("credentials = $credential")
            auth.signInWithCredential(credential).addOnCompleteListener {
                if(it.isSuccessful) {
                    checkUser(phoneNumber!!)
                }
                else {
                    toast("Failed")
                }
            }
        }
    }

    private fun checkUser(phone: String = "") {
        database.getReference().child("users").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = 0
                var snapshot1: DataSnapshot
                for(snapshot1 in snapshot.children) {
                    val user: User = snapshot1.getValue(User::class.java)!!
                        if(phone == user.phoneNumber.toString()) {
                            found ++
                            break
                        }
                }
                if(found != 0) {
                    toast("Signed in")
                    val intent = Intent(this@OTPActivity,ChatHomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else {
                    toast("Signed in")
                    val intent = Intent(this@OTPActivity,SetupProfileActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        })
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Logged in")
                    val intent = Intent(this,SetupProfileActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                   logD("Failed")
                }
            }
    }
}