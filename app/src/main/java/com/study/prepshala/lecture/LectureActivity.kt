package com.study.prepshala.lecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.eLectureDatabase.Lecture
import com.study.prepshala.eLectureDatabase.LectureViewModel
import com.study.prepshala.handleSwipes.SwipeToDelete
import kotlinx.android.synthetic.main.activity_lecture_home.*
import java.util.*
import kotlin.collections.ArrayList

class LectureActivity : AppCompatActivity(), LectureAdapter.iLectureAdapter {

    lateinit var viewModel: LectureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecture_home)

        lectureRecycler.layoutManager = LinearLayoutManager(this)
        val adapter = LectureAdapter(this, this)
        lectureRecycler.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(LectureViewModel::class.java)
        viewModel.allLecture.observe(this, Observer {list->
            list?.let {
                adapter.updateList(it)
                if(adapter.getLectureListFromAdapter().size>0) {
                    lecturesDisplayMessage.visibility = View.GONE
                }
                else {
                    lecturesDisplayMessage.visibility = View.VISIBLE
                }

            }

        })

        setListners()
        manageTouch(adapter.getLectureListFromAdapter())
    }

    private fun setListners() {
        lectureAddBtn.setOnClickListener{
            addInfo()
        }
    }

    private fun manageTouch(lecture: ArrayList<Lecture>){
        val item = object : SwipeToDelete(this,ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END , ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition: Int = viewHolder.adapterPosition
                val toPosition : Int = target.adapterPosition
                Collections.swap(lecture,fromPosition,toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition,toPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val builder = AlertDialog.Builder(this@LectureActivity)
//                builder.setMessage("Are you sure you want to delete this?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes"){
//                            dialog, _ ->
//                        viewModel.deleteLecture(lecture[viewHolder.adapterPosition])
//                        toast("Lecture Deleted!")
//                    }
//                    .setNegativeButton("No"){
//                            dialog,_->
//                        dialog.dismiss()
//                    }
//                builder.create()
//                builder.show()
                viewModel.deleteLecture(lecture[viewHolder.adapterPosition])
            }
        }
        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(lectureRecycler)
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.lecture_add,null)
        /**set view*/
        val lectureName = v.findViewById<EditText>(R.id.lectureName)
        val lectureFaculty = v.findViewById<EditText>(R.id.lectureFaculty)
        val lectureUrl = v.findViewById<EditText>(R.id.lectureURL)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val lectureInfo = lectureName.text.toString()
            val facultyInfo = lectureFaculty.text.toString()
            val urlInfo = lectureUrl.text.toString()

            if(lectureInfo.isEmpty()){
                toast("Fields cannot be empty")
                return@setPositiveButton
            }

            if(facultyInfo.isEmpty()) {
                toast("Fields cannot be empty")
                return@setPositiveButton
            }

            if(urlInfo.isEmpty()) {
                toast("Fields cannot be empty")
                return@setPositiveButton
            }

            if(checkValidUrl(urlInfo) == false) {
                toast("Please enter a valid url")
                return@setPositiveButton
            }

            viewModel.addLecture(Lecture(lectureInfo,facultyInfo,urlInfo))
            Toast.makeText(this,"Lecture Added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    private fun checkValidUrl(url: String): Boolean {
        val stringYoutubePlaylist : String = "https://youtube.com/playlist?"
        val stringYoutubeVideoType1 : String = "https://www.youtube.com/watch?"
        val stringYouTubeVideoType2 : String = "https://youtu.be"
        val lastPositionToCheck : Int = url.indexOf('?')+1
        val temp: String = url.substring(0,lastPositionToCheck)
        if((temp == stringYoutubePlaylist) or (temp == stringYoutubeVideoType1)) {
            return true
        }
        if(url.substring(0,url.indexOf('e')+1) == stringYouTubeVideoType2) {
            logD("${url.substring(0,url.indexOf('e'))}")
            return true
        }
        return false
    }

    override fun onItemClicked(lecture: Lecture) {
        TODO("Not yet implemented")
    }
}