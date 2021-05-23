package com.study.prepshala.chat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.study.prepshala.R
import com.study.prepshala.models.User
import kotlinx.android.synthetic.main.activity_setup_profile.*

class SetupProfileActivity : AppCompatActivity() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Creating profile...")
        dialog!!.setCancelable(false)

        setListner()


    }

    private fun setListner() {

        imageView.setOnClickListener {
            var intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 45)
        }

        continueBtn.setOnClickListener {
            var name: String = nameBox.text.toString()

            if(name.isEmpty()) {
                nameBox.setError("Please type a name")
                return@setOnClickListener
            }

            dialog!!.show()
            if(selectedImage != null) {
                var reference: StorageReference = storage.reference.child("Profiles").child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener {
                    if(it.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            val uid = auth.uid
                            val phone = auth.currentUser.phoneNumber
                            val name = nameBox.text.toString()
                            val user = User(uid, name, phone, imageUrl)
                            database.getReference()
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnSuccessListener {
                                    dialog!!.dismiss()
                                    val intent = Intent(this, ChatHomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    }
                }
            }

            else {
                val uid = auth!!.uid
                val phone = auth!!.currentUser!!.phoneNumber
                val user = User(uid, name, phone, "No Image")
                database!!.reference
                    .child("users")
                    .child(uid!!)
                    .setValue(user)
                    .addOnSuccessListener {
                        dialog!!.dismiss()
                        val intent = Intent(this@SetupProfileActivity, ChatHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data!=null) {
            if(data.data != null) {
                imageView.setImageURI(data.data)
                selectedImage = data.data
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }




}