package com.study.prepshala.chat

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.fullscreenImageDisplay.FullscreenDisplay
import com.study.prepshala.lakshyaDatabase.Lakshya
import kotlinx.android.synthetic.main.activity_update_profile.*

class UpdateProfile : AppCompatActivity() {
    var progressDialog: ProgressDialog? = null
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var profileImageForIntent: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Updating Username...")
        progressDialog!!.setCancelable(false)

        changeNotificationbarColor()
        getViewsContent()
        setListners()
    }

    private fun setListners() {
        updateProfileBackBtn.setOnClickListener {
            finish()
        }

        updateProfileImageView.setOnClickListener {
            val intent = Intent(this, FullscreenDisplay::class.java)
            intent.putExtra("imageUrl", profileImageForIntent)
            intent.putExtra("personName", "Profile Picture")
            intent.putExtra("calledFrom", "UpdateProfile")
            startActivity(intent)
        }

        updateProfileName.setOnClickListener {
            val inflter = LayoutInflater.from(this)
            val v = inflter.inflate(R.layout.edit_talkto_profile_name, null)

            /**set view*/
            val editTalktoProfileName = v.findViewById<EditText>(R.id.editTalktoProfileName)
            val addDialog = AlertDialog.Builder(this)
            addDialog.setView(v)
            addDialog.setPositiveButton("Ok") { dialog, _ ->
                val newName = editTalktoProfileName.text.toString()

                if (newName.isEmpty()) {
                    toast("Name cannot be empty")
                    return@setPositiveButton
                }

                progressDialog!!.show()
                database.getReference().child("users").child(FirebaseAuth.getInstance().currentUser.uid.toString()).child("name")
                    .setValue(newName).addOnSuccessListener {
                        toast("Username Updated!")
                        progressDialog!!.dismiss()
                        getViewsContent()
                    }
                dialog.dismiss()
            }
            addDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()

            }
            addDialog.create()
            addDialog.show()
        }
    }

    private fun getViewsContent() {
        var name: String
        var phoneNumber: String
        var profileImage: String
        database.getReference().child("users").child(FirebaseAuth.getInstance().currentUser.uid.toString()).get().addOnSuccessListener {
            name = it.child("name").value.toString()
            phoneNumber = it.child("phoneNumber").value.toString()
            profileImage = it.child("profileImage").value.toString()
            setViewsContent(name, phoneNumber, profileImage)
        }
    }

    private fun setViewsContent(name: String, phoneNumber: String, profileImage: String) {
        profileImageForIntent = profileImage
        updateName.setText(name)
        updateProfilePhone.setText(phoneNumber)
        logD(profileImage)
        Glide.with(this).load(profileImage).placeholder(R.drawable.avatar).into(updateProfileImageView)
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