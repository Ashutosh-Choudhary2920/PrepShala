package com.study.prepshala.secretDiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.R
import com.study.prepshala.handleSwipes.SwipeToDelete
import com.study.prepshala.secretDiaryDatabase.SecretDiary
import com.study.prepshala.secretDiaryDatabase.SecretDiaryViewModel
import kotlinx.android.synthetic.main.activity_secret_diary.*
import java.util.*

class SecretDiaryActivity : AppCompatActivity() {

    lateinit var viewModel: SecretDiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret_diary)

        diaryRecycler.layoutManager = LinearLayoutManager(this)
        val adapter = SecretDiaryAdapter(this)
        diaryRecycler.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SecretDiaryViewModel::class.java)

        viewModel.allEntries.observe(this, Observer {
            adapter.updateList(it)
            if(adapter.getDiaryListFromAdapter().size>0) {
                diaryDisplayMessage.visibility = View.GONE
            }
            else {
                diaryDisplayMessage.visibility = View.VISIBLE
            }

        })

        setListner()
        manageTouch(adapter.getDiaryListFromAdapter())

    }


    private fun setListner() {
        diaryAddBtn.setOnClickListener {
            val intent: Intent = Intent(this,DiaryContent::class.java)
            intent.putExtra(DiaryContent.clickType,"add")
            startActivity(intent)
        }
    }

    private fun manageTouch(entries: ArrayList<SecretDiary>) {
        val item = object : SwipeToDelete(this, 0, ItemTouchHelper.LEFT) {
            //ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END

//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                val fromPosition: Int = viewHolder.adapterPosition
//                val toPosition : Int = target.adapterPosition
//                Collections.swap(entries,fromPosition,toPosition)
//                recyclerView.adapter?.notifyItemMoved(fromPosition,toPosition)
//                return false
//            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteEntries(entries[viewHolder.adapterPosition])
            }
        }
        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(diaryRecycler)
    }


}