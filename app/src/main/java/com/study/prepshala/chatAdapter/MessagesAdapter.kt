package com.study.prepshala.chatAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.github.pgreze.reactions.dsl.reactionConfig
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.fullscreenImageDisplay.FullscreenDisplay
import com.study.prepshala.models.Messages
import com.study.prepshala.models.User
import java.util.ArrayList

class MessagesAdapter(var context: Context, var messages: ArrayList<Messages>, var senderRoom: String, var receiverRoom: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT: Int = 1
    val ITEM_RECEIVE: Int = 2

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentImage = itemView.findViewById<ImageView>(R.id.sentImage)
        val sentMessage = itemView.findViewById<TextView>(R.id.sentMessage)
        val sentFeelings = itemView.findViewById<ImageView>(R.id.sentFeelings)
    }

    inner class RecieverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveImage = itemView.findViewById<ImageView>(R.id.receiveImage)
        val receiveMessage = itemView.findViewById<TextView>(R.id.receiveMessage)
        val receiveFeelings = itemView.findViewById<ImageView>(R.id.receiveFeelings)
    }

    override fun getItemCount(): Int {
        //logD("returning message size ${messages.size}")
        return messages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_SENT) {
            //logD("ITEM_SENT returning")
            val view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false)
            SentViewHolder(view)
        }
        else {
            //logD("ITEM_Reveive returning")
            val view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false)
            RecieverViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        if(FirebaseAuth.getInstance().uid == message.senderId) {
            return ITEM_RECEIVE
        }
        else {
            return ITEM_SENT
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages.get(position)
        val reactions = intArrayOf(
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
        )
        val config = ReactionsConfigBuilder(context)
            .withReactions(reactions)
            .build()

        val popup = ReactionPopup(context, config) { pos -> true.also {
            if(holder::class.java == SentViewHolder::class.java) {
                val viewHolder: SentViewHolder = holder as SentViewHolder
                if(pos >=0 && pos < 6){
                    viewHolder.sentFeelings.setImageResource(reactions[pos])
                    viewHolder.sentFeelings.visibility = View.VISIBLE
                }

            }
            else {
                val viewHolder: RecieverViewHolder = holder as RecieverViewHolder
                if(pos >=0 && pos < 6) {
                    viewHolder.receiveFeelings.setImageResource(reactions[pos])
                    viewHolder.receiveFeelings.visibility = View.VISIBLE
                }
            }
            message.feeling = pos
            FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .child(message.messageId.toString()).setValue(message)

            FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(receiverRoom)
                .child("messages")
                .child(message.messageId.toString()).setValue(message)

            // position = -1 if no selection
        } }

        if(holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.setText(message.message)

            if(message.messageType == "image") {
                viewHolder.sentImage.visibility = View.VISIBLE
                viewHolder.sentMessage.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.sentImage)

            }
            else {
                viewHolder.sentImage.visibility = View.GONE
                viewHolder.sentMessage.visibility = View.VISIBLE
            }


            viewHolder.sentMessage.setOnTouchListener(object: View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event != null) {
                        if (v != null) {
                            popup.onTouch(v, event)
                        }
                    }
                    return false
                }
            })
//            viewHolder.sentImage.setOnTouchListener(object: View.OnTouchListener{
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                    if (event != null) {
//                        if (v != null) {
//                            popup.onTouch(v, event)
//                        }
//                    }
//                    return false
//                }
//            })

            viewHolder.sentImage.setOnClickListener {
                val intent = Intent(context, FullscreenDisplay::class.java)
                intent.putExtra("personName", "")
                intent.putExtra("imageUrl", message.imageUrl)
                context.startActivity(intent)
            }

            if(message.feeling >= 0 && message.feeling < 6) {
                holder.sentFeelings.setImageResource(reactions[message.feeling])
                holder.sentFeelings.visibility = View.VISIBLE
            }
            else {
                holder.sentFeelings.visibility = View.GONE
            }

        }
        else {
            val viewHolder: RecieverViewHolder = holder as RecieverViewHolder
            viewHolder.receiveMessage.setText(message.message)

            if(message.messageType == "image") {
                viewHolder.receiveImage.visibility = View.VISIBLE
                viewHolder.receiveMessage.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.receiveImage)
            }
            else {
                viewHolder.receiveImage.visibility = View.GONE
                holder.receiveMessage.visibility = View.VISIBLE
            }


            viewHolder.receiveMessage.setOnTouchListener(object: View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event != null) {
                        if (v != null) {
                            popup.onTouch(v, event)
                        }
                    }
                    return false
                }
            })
//            viewHolder.receiveImage.setOnTouchListener(object: View.OnTouchListener{
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                    if (event != null) {
//                        if (v != null) {
//                            popup.onTouch(v, event)
//                        }
//                    }
//                    return false
//                }
//            })

            viewHolder.receiveImage.setOnClickListener {
                val intent = Intent(context, FullscreenDisplay::class.java)
                intent.putExtra("personName", "")
                intent.putExtra("imageUrl", message.imageUrl)
                context.startActivity(intent)
            }

            if(message.feeling >= 0 && message.feeling < 6) {
                viewHolder.receiveFeelings.setImageResource(reactions[message.feeling])
                viewHolder.receiveFeelings.visibility = View.VISIBLE
            }
            else {
                viewHolder.receiveFeelings.visibility = View.GONE
            }

        }

    }



}