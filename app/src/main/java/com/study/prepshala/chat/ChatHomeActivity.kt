package com.study.prepshala.chat

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.study.prepshala.DashboardActivity
import com.study.prepshala.R
import com.study.prepshala.Utils.closeKeyboard
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.chatAdapter.TopStatusAdapter
import com.study.prepshala.chatAdapter.UsersAdapter
import com.study.prepshala.models.Status
import com.study.prepshala.models.User
import com.study.prepshala.models.UserStatus
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_home.*
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatHomeActivity : AppCompatActivity() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var users: ArrayList<User>
    lateinit var userStatuses: ArrayList<UserStatus>
    var dialog: ProgressDialog? = null
    var user = User()
    lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Updating Status...")
        dialog!!.setCancelable(false)


        supportActionBar?.setTitle("TalkTo")

        init()
        setListners()

    }

    override fun onResume() {
        val currentId: String? = FirebaseAuth.getInstance().uid
        if(currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("online")
        }
        super.onResume()
    }

    override fun onPause() {
        logD("On pause of ChatHomeActivity Called")
        val currentId: String? = FirebaseAuth.getInstance().uid
        if(currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("offline")
        }
        super.onPause()
    }

    override fun onDestroy() {
        val currentId: String? = FirebaseAuth.getInstance().uid
        if(currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("offline")
        }
        super.onDestroy()
    }

    fun init() {
        val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        val contactList : MutableList<String> = ArrayList()
        if (contacts != null) {
            while (contacts.moveToNext()) {
                var number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactList.add(number)
            }
        }

        users = ArrayList()
        usersAdapter = UsersAdapter(this, users, contactList as ArrayList)
        usersRecyclerView.adapter = usersAdapter

        database.getReference().child("users").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!
                }

            })

        userStatuses = ArrayList()
        val statusAdapter = TopStatusAdapter(this, userStatuses, contactList as ArrayList)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        statusList.adapter = statusAdapter

        usersRecyclerView.showShimmerAdapter()


        database.getReference().child("users").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                var snapshot1: DataSnapshot
                for(snapshot1 in snapshot.children) {
                    val user: User = snapshot1.getValue(User::class.java)!!
                    if(FirebaseAuth.getInstance().uid != user.uid) {
                        users.add(user)
                    }
                }
                usersRecyclerView.hideShimmerAdapter()
                usersAdapter.notifyDataSetChanged()
            }
        })

        //statusList.showShimmerAdapter()
        database.getReference().child("stories").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear()
                    for(storySnapshot: DataSnapshot in snapshot.children) {
                        var status: UserStatus = UserStatus()
                        status.name = storySnapshot.child("name").getValue(String::class.java)
                        status.profileImage = storySnapshot.child("profileImage").getValue(String::class.java)
                        status.lastUpdated = storySnapshot.child("lastUpdated").getValue(Long::class.java)!!
                        status.phone = storySnapshot.child("phone").getValue(String::class.java)

                        val statuses = ArrayList<Status>()
                        for(statusSnapshot: DataSnapshot in storySnapshot.child("statuses").children) {
                            var sampleStatus: Status = statusSnapshot.getValue(Status::class.java)!!
                            statuses.add(sampleStatus)
                        }

                        status.statuses = statuses
                        userStatuses.add(status)
                    }
                    //statusList.hideShimmerAdapter()
                    statusAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setListners() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.status -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(intent, 75)
                }
                R.id.updateProfile -> {
                    val intent = Intent(this, UpdateProfile::class.java)
                    startActivity(intent)
                }
                R.id.lowerMenuLogout -> {
                    auth.signOut()
                    toast("Signed Out!")
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null ) {
            if(data.data != null ) {
                dialog!!.show()
                val date: Date = Date()
                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val reference = storage.reference.child("status").child(date.time.toString() + "" )
                reference.putFile(data.data!!).addOnCompleteListener {
                    if(it.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener {
                            val userStatus = UserStatus()
                            userStatus.phone = user.phoneNumber
                            userStatus.name = user.name
                            userStatus.profileImage = user.profileImage
                            userStatus.lastUpdated = date.time
                            userStatus.updatedDate = date
                            val obj = HashMap<String, Any?>()
                            obj.put("name", userStatus.name)
                            obj.put("profileImage", userStatus.profileImage)
                            obj.put("lastUpdated", userStatus.lastUpdated)
                            obj.put("updatedDate", userStatus.updatedDate)
                            obj.put("phone", userStatus.phone)

                            database.getReference()
                                .child("stories")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj)

                            var status: Status = Status(it.toString(), userStatus.lastUpdated, userStatus.updatedDate)
                            database.getReference().child("stories")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .child("statuses")
                                .push()
                                .setValue(status)
                            dialog!!.dismiss()
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.search -> {
                var searchView: androidx.appcompat.widget.SearchView = item.actionView as androidx.appcompat.widget.SearchView
                searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        usersAdapter.filter.filter(query)
                        return false
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        usersAdapter.filter.filter(newText)
                        return false
                    }

                })
            }
            R.id.settings -> {
                val intent = Intent(this, UpdateProfile::class.java)
                startActivity(intent)
            }
            R.id.logOut -> {
                //Implement this in background thread
                auth.signOut()
                toast("Signed Out!")
                val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
//                val intent = Intent(this, DashboardActivity::class.java)
//                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }



}