package com.study.prepshala

//import androidx.biometric.BiometricPrompt
import android.Manifest
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.SearchView.OnQueryTextListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.study.prepshala.Search.SearchActivity
import com.study.prepshala.Utils.toast
import com.study.prepshala.chat.PhoneNumberActivity
import com.study.prepshala.groupStudy.GroupStudy
import com.study.prepshala.lakshaya.LakshyaActivity
import com.study.prepshala.lakshyaDatabase.Lakshya
import com.study.prepshala.lakshyaDatabase.LakshyaViewModel
import com.study.prepshala.lecture.LectureActivity
import com.study.prepshala.notes.NotesActivity
import com.study.prepshala.secretDiary.SecretDiaryActivity
import com.study.prepshala.toDo.ToDoActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

class DashboardActivity : AppCompatActivity() {

    //variables for biometric implementation

    private var cancellationSignal: CancellationSignal? = null
    private var authenticationCallback: BiometricPrompt.AuthenticationCallback =
        @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                activityStarter(SecretDiaryActivity::class.java)
            }
        }

    //Variables for Notification
    private val CHANNEL_ID = "channel_id_prepshala_01"
    private val longTermNotificationID = 101
    private val shortTermNotificationID = 102

    //Variables for getting Long and Short term goals
    lateinit var viewModel: LakshyaViewModel
    private var allEntries = listOf<Lakshya>()
    var shortTermGoal: String = "Please set your Short Term Goal"
    var longTermGoal: String = "Please set your Long Term Goal"



    //....................................................................................//


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getPermissions()
        }
        init()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(LakshyaViewModel::class.java)

        viewModel.allEntries.observe(this, Observer {
            allEntries = it
            if(allEntries != null) {
                setGoals(allEntries)
            }
        })

        createNotificationChannel()
    }


    override fun onResume() {
        super.onResume()
        searchBar.clearFocus();
    }


    //....................................................................................//


    @RequiresApi(Build.VERSION_CODES.P)
    private fun init() {
        checkBiometricSupport()
        setQuotes()
        setListners()
    }


   //......................................................................................//


    @RequiresApi(Build.VERSION_CODES.P)
    private fun setListners() {
        chat.setOnClickListener() {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                getPermissions()
            }
            else {
                activityStarter(PhoneNumberActivity::class.java)
            }
        }

        todo.setOnClickListener() {
            activityStarter(ToDoActivity::class.java)
        }

        notes.setOnClickListener() {
            activityStarter(NotesActivity::class.java)
        }

        lectures.setOnClickListener() {
            activityStarter(LectureActivity::class.java)
        }

        groupStudy.setOnClickListener() {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                getPermissions()
            }
            else {
                activityStarter(GroupStudy::class.java)
            }
        }

        lakshya.setOnClickListener() {
            activityStarter(LakshyaActivity::class.java)
        }

        appIcon.setOnClickListener() {
            setQuotes()
        }

        searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent: Intent = Intent(this@DashboardActivity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.URL,"https://www.google.com/search?q="+searchBar.query)
                startActivity(intent)
                searchBar.setIconified(true);
                searchBar.clearFocus();
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })


        appName.setOnClickListener() {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Secret Diary")
                .setDescription("Your Diary is Secret")
                .setSubtitle("Authentication is required")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener{ dialog, which ->
                    notifyUser("Authentication cancelled")
                }).build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    } // Functions to set values to UI elements

    private fun setQuotes() {
        var json : String? = null
        try {
            val inputSream : InputStream = assets.open("quotes.json")
            json = inputSream.bufferedReader().use { it.readText() }
            var jsonarr = JSONArray(json)
            var random = rand(0,jsonarr.length()-1)
            var jsonObj = jsonarr.getJSONObject(random)
            var authorName = jsonObj.getString("author")
            quotes.text = jsonObj.getString("text")
            if(authorName != "null")
                author.text = "--- ${authorName}"
            else
                author.text = "--- Anonymous"
        }
        catch (e : IOException) {
            toast("No Quotes for today")
        }
    }  // Function to randomise quotes


    //....................................................................................//


    //utilities

    private fun activityStarter(activityName: Class<*>) {
        val intent: Intent = Intent(this, activityName)
        startActivity(intent)
    }  //Utility function to start a new activity

    private fun notifyUser(message: String) {
        toast(message)
    }  //utility function to notify users about Biometric Status

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }  //Utility function to handle cancellation of biometric signal

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure) {
            notifyUser("Fingerprint authentication has not been enabled in setting")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    } //utility function to check biometric status

    private fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (start..end).random()
    } //utility function to generation random number between start and end

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PrepShala Notification"
            val descriptionText = "My Goals"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                //vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendLongTermGoalAsNotification(goal: String) {
        val intent = Intent(this, LakshyaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val bitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.goal_image)
        val bitmapLargeIcon: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.front_image)

        val builder = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.front_image)
            //.setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setContentTitle("My Long Term Goal is: ")
//            .setContentText("To become a software Engineer")
            .setLargeIcon(bitmap)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(goal))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(longTermNotificationID, builder.build())
        }
    }

    private fun sendShortTermGoalAsNotification(goal: String) {
        val intent = Intent(this, LakshyaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val bitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.goal_image)
        val bitmapLargeIcon: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.front_image)

        val builder = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.front_image)
            //.setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setContentTitle("My Short Term Goal is: ")
            //.setContentText("To become a software Engineer")
            .setLargeIcon(bitmap)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(goal))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(shortTermNotificationID, builder.build())
        }
    }

    private fun setGoals(entries: List<Lakshya>?) {
        for(i in  0..entries!!.size-1) {
            if(entries[i].goalType == "long") {
                longTermGoal = entries[i].goal
                sendLongTermGoalAsNotification(longTermGoal)  //Pushing Notification
            }
            else if(entries[i].goalType == "short") {
                shortTermGoal = entries[i].goal
                sendShortTermGoalAsNotification(shortTermGoal) //Pushing Notification
            }
        }
    }

    private fun getPermissions() {
        val permissionListner = object: PermissionListener {
            override fun onPermissionGranted() {
                toast("Permission Granted")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                toast("Permission Denied. Kindly grant permissions to continue")
                //finish()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListner)
            .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .check()
    }
}