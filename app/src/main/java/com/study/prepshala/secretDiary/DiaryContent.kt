package com.study.prepshala.secretDiary

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.study.prepshala.R
import com.study.prepshala.Utils.getCurrentDate
import com.study.prepshala.Utils.getCurrentTime
import com.study.prepshala.secretDiaryDatabase.SecretDiary
import com.study.prepshala.secretDiaryDatabase.SecretDiaryViewModel
import kotlinx.android.synthetic.main.activity_diary_content.*

class DiaryContent : AppCompatActivity() {
    companion object {
        var clickType: String = ""
        var position: String = "-1"
    }

    lateinit var viewModel: SecretDiaryViewModel
    private var allEntries = listOf<SecretDiary>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_content)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SecretDiaryViewModel::class.java)

        viewModel.allEntries.observe(this, Observer {
            allEntries = it
            viewDiary(allEntries)
        })

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        setListners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun viewDiary (entries: List<SecretDiary>) {

        val clicked = intent.getStringExtra(clickType)
        val index = intent.getStringExtra(position)



        if(clicked == "display") {
            val currentNode = entries[index?.toInt()!!]
            changeView()
            diaryContentAddBtn.visibility = View.GONE
            diaryTitleTextView.text = currentNode.title
            diaryTextView.text = currentNode.description

        }

        if(clicked == "edit") {
//            logD("Inside edit check. index = $index")
            val currentNode = entries[index?.toInt()!!]
            diaryContentUpdateBtn.visibility = View.VISIBLE
            diaryTitleEditText.setText(currentNode.title)
            diaryEditText.setText(currentNode.description)
            diaryContentUpdateBtn.setOnClickListener {
                var title: String = diaryTitleEditText.text.toString()
                var description: String = diaryEditText.text.toString()
                var currentDate: String = getCurrentDate()
                var currentTime: String = getCurrentTime()
                viewModel.deleteEntries(currentNode)
                viewModel.addEntries(SecretDiary(title,description,currentDate,currentTime))
                val intent: Intent = Intent(this, SecretDiaryActivity::class.java )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListners() {
        diaryContentAddBtn.setOnClickListener {
            if(diaryTitleEditText.text.toString() != "" && diaryEditText.text.toString() != "" ){
                var title: String = diaryTitleEditText.text.toString()
                var description: String = diaryEditText.text.toString()
                var currentDate: String = getCurrentDate()
                var currentTime: String = getCurrentTime()
                viewModel.addEntries(SecretDiary(title,description,currentDate,currentTime))
                val intent: Intent = Intent(this, SecretDiaryActivity::class.java )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

    }

    private fun changeView() {
        diaryTitleEditText.visibility = View.GONE
        diaryEditText.visibility = View.GONE
        diaryTitleTextView.visibility = View.VISIBLE
        diaryTextView.visibility = View.VISIBLE
        diaryScrollView.visibility = View.VISIBLE
        diaryView.visibility = View.VISIBLE
    }

}