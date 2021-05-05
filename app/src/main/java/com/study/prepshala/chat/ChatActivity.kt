package com.study.prepshala.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.study.prepshala.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()
    }

    fun init() {
        ChatTextView.setOnClickListener() {
            Toast.makeText(this, "We need to work on this", Toast.LENGTH_SHORT).show()
        }
    }
}