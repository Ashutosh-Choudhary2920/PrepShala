package com.study.prepshala.chat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.databinding.adapters.TextViewBindingAdapter.setPhoneNumber
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.chatAdapter.MessagesAdapter
import com.study.prepshala.models.Messages
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.Runnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    lateinit var messages: ArrayList<Messages>
    var senderRoom: String = ""
    var receiverRoom: String = ""
    var senderUid: String = ""
    var receiverUid: String? = ""
    var userName: String? = ""
    var profileImage: String? = ""
    var userPhone: String? = ""
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    lateinit var messagesAdapter: MessagesAdapter
    var dialog: ProgressDialog? = null
    val handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        senderUid = intent.getStringExtra("uid")!!
        receiverUid = FirebaseAuth.getInstance().uid
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

//        val currentId: String? = FirebaseAuth.getInstance().uid
//        if(currentId != null) {
//            database.getReference().child("presence").child(currentId).setValue("online")
//        }

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Sending Image...")
        dialog!!.setCancelable(false)

        checkAvailibilityStatus()
        changeNotificationbarColor()
        setNameAndImage()
        setAdapter()
        setListners()
        displayMessages()
    }

    private fun checkAvailibilityStatus() {
        database.getReference().child("presence").child(senderUid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val availibility: String? = snapshot.getValue(String::class.java)
                    if(!availibility?.isEmpty()!!) {
                        if(availibility.equals("offline")) {
                            status.visibility = View.GONE
                        }
                        else {
                            status.visibility = View.VISIBLE
                            status.setText(availibility)
                        }
                    }
                    else {
                        status.visibility = View.GONE
                    }

                }
            }

        })
    }

    private fun setAdapter() {
        messages = ArrayList()
        var mLayoutManager: LinearLayoutManager  = LinearLayoutManager(this)
//        mLayoutManager.reverseLayout = true
        mLayoutManager.setStackFromEnd(true)
        chatRecycler.layoutManager = mLayoutManager
        messagesAdapter = MessagesAdapter(this, messages, senderRoom, receiverRoom)
        messagesAdapter.setHasStableIds(false)
        chatRecycler.adapter = messagesAdapter
    }

    override fun onResume() {
        val currentId: String? = FirebaseAuth.getInstance().uid
        if(currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("online")
        }

        super.onResume()
    }

    override fun onPause() {
        val currentId: String? = FirebaseAuth.getInstance().uid
        if(currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("offline")
        }
        super.onPause()
    }

    private fun displayMessages() {
        var noOfMsgBeforeUpdate: Int = 0
        var noOfMsgAfterUpdate: Int = 0
        database = FirebaseDatabase.getInstance()
        database.getReference().child("chats")
            .child(senderRoom)
            .child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    noOfMsgBeforeUpdate = messages.size
                    messages.clear()
                    var snapshot1: DataSnapshot
                    for( snapshot1 in snapshot.children) {
                        var message: Messages = snapshot1.getValue(Messages::class.java)!!
                        message.messageId = snapshot1.key
                        messages.add(message)
                    }
                    noOfMsgAfterUpdate = messages.size
                    if(noOfMsgBeforeUpdate != noOfMsgAfterUpdate) {
                        chatRecycler.adapter?.itemCount?.let { chatRecycler.smoothScrollToPosition(it) }
                    }
                    messagesAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun setListners() {
        chatsGoBack.setOnClickListener {
            finish()
        }

        sendBtn.setOnClickListener {
            sendMessage("text")
        }

        attachment.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 25)
        }

        messageBox.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                database.getReference().child("presence").child(receiverUid!!).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            var userStoppedTyping = Runnable {
                database.reference.child("presence").child(receiverUid!!).setValue("Online")
            }

        })

        chatsDisplayIndividualContacts.setOnClickListener {
            database.reference.child("users").child(senderUid).child("phoneNumber").get().addOnSuccessListener {
               setPhoneNumberAndStartIntent(it.value.toString())
            }
        }
    }

    private fun setPhoneNumberAndStartIntent(ph: String?) {
        userPhone = ph
        val intent = Intent(this, DisplayIndividualContacts::class.java)
        intent.putExtra("userName", userName)
        intent.putExtra("profileImage", profileImage)
        intent.putExtra("userPhone",userPhone)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 25) {
            if(data != null) {
                if(data.data != null ) {
                    val calender: Calendar = Calendar.getInstance()
                    val selectedImage: Uri? = data.data
                    val reference = storage.reference.child("chats").child(calender.timeInMillis.toString() + "")
                    if (selectedImage != null) {
                        dialog!!.show()
                        reference.putFile(selectedImage).addOnCompleteListener {
                            dialog!!.dismiss()
                            if(it.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener {
                                    val filePath: String = it.toString()
                                    sendMessage("attachment", filePath)
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendMessage(messageType: String, filePath: String = "") {
        var date =  Date()
        var messageTxt = messageBox.text.toString()
        database = FirebaseDatabase.getInstance()
        var randomKey = database.reference.push().key
        if(messageTxt != "" || messageType == "attachment") {
            var message: Messages = Messages(messageTxt, senderUid, date.time)
            if(messageType == "attachment") {
                message.imageUrl = filePath
                message.messageType = "image"
            }
            messageBox.setText("")
            var lastMsgObj: HashMap<String, Any?> = HashMap()
            lastMsgObj.put("lastMsg", message.message)
            lastMsgObj.put("lastMsgTime", date.time)
            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj)
            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj)
            if (randomKey != null) {
                database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message).addOnSuccessListener (object: OnSuccessListener<Void> {
                        override fun onSuccess(p0: Void?) {
                            database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener (object: OnSuccessListener<Void> {
                                    override fun onSuccess(p0: Void?) {
                                    }
                                })
                        }
                    })
            }
        }
    }

    private fun setNameAndImage() {
        userName= intent.getStringExtra("name")
        profileImage= intent.getStringExtra("imageUri")
        name.text = userName
        Glide.with(this).load(profileImage)
            .placeholder(R.drawable.avatar)
            .into(profile)
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