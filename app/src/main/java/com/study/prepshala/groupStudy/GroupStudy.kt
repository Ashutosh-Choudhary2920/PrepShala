package com.study.prepshala.groupStudy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.study.prepshala.R
import com.study.prepshala.Search.SearchActivity
import com.study.prepshala.Utils.toast
import kotlinx.android.synthetic.main.activity_group_study_home.*
import java.net.MalformedURLException
import java.net.URL

//import org.jitsi.meet.sdk.JitsiMeet
//import org.jitsi.meet.sdk.JitsiMeetActivity
//import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
//import org.jitsi.meet.sdk.JitsiMeetUserInfo

class GroupStudy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_study_home)

        //init1()
        init2()
    }

    private fun init2() {
        setListnersForWebViewLaunch()
    }

    private fun setListnersForWebViewLaunch() {
        joinBtn.setOnClickListener {
            if(codeBox.text.toString()!="") {
                val meetingUrl: String = "https://meet.jit.si/"+codeBox.text.toString()+"#config.disableDeepLinking=true"
                val intent: Intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.URL,meetingUrl)
                startActivity(intent)
            }
            else {
                toast("Please enter a Secret Code to start the Meeting!")
                return@setOnClickListener
            }
        }

        commonSetListenerForShare()
    }

    fun commonSetListenerForShare() {
        shareBtn.setOnClickListener {
            if(codeBox.text.toString()!="") {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT,"Hey join me for Group Study!\nRoom Code: "+codeBox.text.toString())
                    this.type = "text/copy"
                }
                startActivity(shareIntent)
            }
            else {
                toast("Please enter a Secret Code to share!")
                return@setOnClickListener
            }
        }

    }



//    private fun init1() {
//        setListnersForSdkLaunch()
//    }
//
//    private fun setListnersForSdkLaunch() {
//
//        try {
//            val serverUrl: URL = URL("https://meet.jit.si")
//            val defaulOptions = JitsiMeetConferenceOptions.Builder()
//                .setServerURL(serverUrl)
//                .setWelcomePageEnabled(false)
//                .build()
//            JitsiMeet.setDefaultConferenceOptions(defaulOptions)
//        }
//        catch (e: MalformedURLException) {
//            e.printStackTrace();
//        }
//
//
//        joinBtn.setOnClickListener {
//            if(codeBox.text.toString()!="") {
//                val options = JitsiMeetConferenceOptions.Builder()
//                    .setRoom(codeBox.text.toString())
//                    .setWelcomePageEnabled(false)
//                    .build()
//                JitsiMeetActivity.launch(this,options)
//            }
//            else {
//                toast("Please enter a Secret Code to start the Meeting!")
//                return@setOnClickListener
//            }
//        }
//
//        commonSetListenerForShare()
//
//        }


}