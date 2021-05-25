package com.study.prepshala.chatAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.study.prepshala.R
import com.study.prepshala.Utils.displayImage
import com.study.prepshala.Utils.logD
import com.study.prepshala.chat.ChatActivity
import com.study.prepshala.models.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_home.*
import kotlinx.android.synthetic.main.row_conversations.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UsersAdapter(var context: Context, var users: ArrayList<User>, val contactList : ArrayList<String>): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), Filterable {

    var inContactsUsers = arrayListOf<User>()
    //var inContactsUsersSearched = arrayListOf<User>()
    var inContactsUserNames = arrayListOf<String>()
    var inContactsUsersSearchedNames = arrayListOf<User>()
    private var isListFiltered = false
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<CircleImageView>(R.id.profile)
        val username = itemView.findViewById<TextView>(R.id.username)
        val lastMsg = itemView.findViewById<TextView>(R.id.lastMsg)
        val msgTime = itemView.findViewById<TextView>(R.id.msgTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_conversations, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {
        var count = 0
        for(i in 0..users.size-1) {
            if(doesContactContain(users[i].phoneNumber)) {
                count ++
                if(!inContactsUsers.contains(users[i])) {
                    inContactsUsers.add(users[i])
                    inContactsUsersSearchedNames.add(users[i])
                }
            }
        }

        if(!isListFiltered) {
            return count
        }
        else {
            return inContactsUsersSearchedNames.size
        }

    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        //val user: User = users.get(position)
        val user: User = if (!isListFiltered) inContactsUsers.get(position) else inContactsUsersSearchedNames.get(position)
        if (!isListFiltered) {
            val senderID = FirebaseAuth.getInstance().uid
            val senderRoom = senderID + user.uid

            FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            var lastMsg: String? = snapshot.child("lastMsg").getValue(String::class.java)
                            var time: Long? = snapshot.child("lastMsgTime").getValue(Long::class.java)
                            val dateFormat = SimpleDateFormat("hh:mm a")
                            holder.lastMsg.setText(lastMsg)
                            holder.msgTime.setText(dateFormat.format(time?.let { Date(it) }))
                        }
                        else {
                            holder.msgTime.setText("Tap to chat")
                            holder.msgTime.visibility = View.GONE
                        }

                    }

                })
        }

        holder.username.setText(user.name)

        Glide.with(context).load(user.profileImage)
            .placeholder(R.drawable.avatar)
            .into(holder.profile)

        holder.itemView.profile.setOnClickListener {
            displayImage(context, user.profileImage, user.name)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("uid",user.uid)
            intent.putExtra("imageUri", user.profileImage)
            context.startActivity(intent)
        }
    }



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList = listOf<User>()
                val charSearch = constraint.toString()
                filteredList = inContactsUsers.filter {user ->
                    user.name?.let {
                        it.contains(charSearch, ignoreCase = true)
                    } ?: false
                }

//                if (charSearch.isEmpty()) {
//                    filteredList.addAll(inContactsUserNames)
//                } else {
//                    for (eachName in inContactsUserNames) {
//                        if (eachName.toLowerCase().contains(charSearch.toLowerCase())) {
//                            logD("$eachName")
//                            filteredList.add(eachName)
//                        }
//                    }
//                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                inContactsUsersSearchedNames.clear()
                inContactsUsersSearchedNames.addAll(results?.values as ArrayList<User>)
                isListFiltered = true
                notifyDataSetChanged()
            }

        }
    }

    private fun doesContactContain(number: String?): Boolean {
        for(contact in contactList) {
            if(contact.contains(number!!)) {
                return true
            }
        }
        return false
    }
}
