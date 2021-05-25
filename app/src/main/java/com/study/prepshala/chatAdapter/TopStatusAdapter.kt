package com.study.prepshala.chatAdapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devlomi.circularstatusview.CircularStatusView
import com.google.firebase.auth.FirebaseAuth
import com.study.prepshala.R
import com.study.prepshala.Utils.getCurrentDate
import com.study.prepshala.Utils.getCurrentTime
import com.study.prepshala.Utils.logD
import com.study.prepshala.chat.ChatHomeActivity
import com.study.prepshala.models.Status
import com.study.prepshala.models.UserStatus
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_status.view.*
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory
import java.util.*
import kotlin.collections.ArrayList

class TopStatusAdapter(var context: Context, var userStatuses: ArrayList<UserStatus>, val contactList : ArrayList<String>): RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder>() {

    var inContactsUsersStatus = arrayListOf<UserStatus>()
    inner class TopStatusViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val statusImage = itemView.findViewById<CircleImageView>(R.id.statusImage)
        val circular_status_view = itemView.findViewById<CircularStatusView>(R.id.circular_status_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopStatusViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false)
        return TopStatusViewHolder(view)
    }

    override fun getItemCount(): Int {
        var count = 0
        //or userStatuses[i].phone.equals(currentUsersNumber)
       val currentUsersNumber: String = FirebaseAuth.getInstance().currentUser.phoneNumber.toString()
        for(i in 0..userStatuses.size-1) {
            if(contactList.contains(userStatuses[i].phone) or userStatuses[i].phone.equals(currentUsersNumber)) {
                var check = 0
                for(status: Status in userStatuses[i].statuses!!) {
                    val date: Date = Date()
                    val diff = date.time - status?.updatedDate?.time!!
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24
                    if(days <= 0 ) {
                        check ++
                        break
                    }
                }
                if(check > 0) {
                    count ++
                    inContactsUsersStatus.add(userStatuses[i])
                }
            }
        }
        return count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TopStatusViewHolder, position: Int) {
        //var userStatus: UserStatus = userStatuses.get(position)
        var userStatus: UserStatus = inContactsUsersStatus.get(position)
        var lastStatus: Status?
        var portionCount = 0

//        if(inContactsUsersStatus.size - 1 >= 0) {
//            lastStatus = userStatus.statuses!![userStatus.statuses!!.size - 1 ]
//            Glide.with(context).load(lastStatus.imageUrl).into(holder.statusImage)
//        }

        if(userStatus.statuses!!.size - 1 >= 0) {
            lastStatus = userStatus.statuses!![userStatus.statuses!!.size - 1 ]
            if(noOfDays(lastStatus.updatedDate?.time!!) <= 0) {
                Glide.with(context).load(lastStatus.imageUrl).into(holder.statusImage)
            }

        }
        for(status: Status in userStatus.statuses!!) {
            if (noOfDays(status.updatedDate?.time!!) <= 0) {
                portionCount++
            }
        }

        holder.circular_status_view.setPortionsCount(portionCount)
//        holder.circular_status_view.setPortionsCount(userStatus.statuses!!.size) //uncomment this if changes do not work
        holder.circular_status_view.setOnClickListener {
            val myStories = ArrayList<MyStory>()
            for(status: Status in userStatus.statuses!!) {
                if(noOfDays(status.updatedDate?.time!!) <= 0) {
                    myStories.add(MyStory(status.imageUrl))
                    portionCount ++
                }
            }
            StoryView.Builder((context as ChatHomeActivity).supportFragmentManager)
                .setStoriesList(myStories) // Required
                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                .setTitleText(userStatus.name) // Default is Hidden
                .setSubtitleText("") // Default is Hidden
                .setTitleLogoUrl(userStatus.profileImage) // Default is Hidden
                .setStoryClickListeners(object : StoryClickListeners {
                    override fun onDescriptionClickListener(position: Int) {
                        //your action
                    }

                    override fun onTitleIconClickListener(position: Int) {
                        //your action
                    }
                }) // Optional Listeners
                .build() // Must be called before calling show method
                .show()
        }
    }

    private fun noOfDays(checkTime: Long): Long {
        val date: Date = Date()
        val difference = date.time - checkTime
        val seconds = difference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return days
    }
}