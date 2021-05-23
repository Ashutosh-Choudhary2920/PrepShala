package com.study.prepshala.fullscreenImageDisplay

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.study.prepshala.R
import com.study.prepshala.Utils.toast
import com.study.prepshala.chat.ChatHomeActivity
import com.study.prepshala.chat.UpdateProfile
import com.study.prepshala.models.User
import kotlinx.android.synthetic.main.activity_fullscreen_display.*
import kotlinx.android.synthetic.main.activity_setup_profile.*
import uk.co.senab.photoview.PhotoViewAttacher


class FullscreenDisplay : AppCompatActivity() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null
    var isUpdated: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_display)

        changeNotificationbarColor()
        //setZoomable()
        val personName: String? = intent.getStringExtra("personName")
        val imageUrl: String? = intent.getStringExtra("imageUrl")
        if(personName == "Profile Picture" ) {
            fullscreenEditBtn.visibility = View.VISIBLE
        }

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Updating Profile Picture...")
        dialog!!.setCancelable(false)

        setViewParameters(personName, imageUrl)
        setListners()
    }

    private fun setZoomable() {
        val pAttacher: PhotoViewAttacher
        pAttacher = PhotoViewAttacher(fullscreenImageView)
        pAttacher.update()
    }

    private fun setViewParameters(personName: String?, imageUrl: String?) {
        fullscreenName.setText(personName)
        Glide.with(this).load(imageUrl).placeholder(R.drawable.avatar).into(fullscreenImageView)
    }

    private fun setListners() {
        fullscreenBackBtn.setOnClickListener {
            if(isUpdated == true) {
                Intent.FLAG_ACTIVITY_CLEAR_TASK
                val intent = Intent(this, UpdateProfile::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            finish()
        }

        fullscreenEditBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 69)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null) {
            if(data.data != null) {
                selectedImage = data.data
                Glide.with(this).load(selectedImage).placeholder(R.drawable.avatar).into(fullscreenImageView)
                dialog!!.show()
                var reference: StorageReference = storage.reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid.toString())
                reference.putFile(selectedImage!!).addOnCompleteListener {
                    if(it.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener {
                            database.getReference().child("users").child(FirebaseAuth.getInstance().currentUser.uid.toString()).child("profileImage")
                                .setValue(it.toString()).addOnSuccessListener {
                                    toast("Profile Picture Updated!")
                                    isUpdated = true
                                    dialog!!.dismiss()
                                }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if(isUpdated == true) {
            Intent.FLAG_ACTIVITY_CLEAR_TASK
            val intent = Intent(this, UpdateProfile::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        finish()
    }

    private fun changeNotificationbarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.black)
        }
    }
}