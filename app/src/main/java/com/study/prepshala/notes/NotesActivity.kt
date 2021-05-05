package com.study.prepshala.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.R
import com.study.prepshala.Utils.logD
import com.study.prepshala.Utils.toast
import com.study.prepshala.handleSwipes.SwipeToDelete
import com.study.prepshala.notesDatabase.Notes
import com.study.prepshala.notesDatabase.NotesViewModel
import kotlinx.android.synthetic.main.activity_notes.*
import java.util.*


class NotesActivity : AppCompatActivity() {

    lateinit var viewModel: NotesViewModel
    lateinit var myNotesIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        notesRecycler.layoutManager = LinearLayoutManager(this)
        val adapter = NotesAdapter(this)
        notesRecycler.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NotesViewModel::class.java)

        viewModel.allNotes.observe(this, Observer { list ->
            list?.let {
                adapter.updateList(it)
                if(adapter.getNotesListFromAdapter().size>0) {
                    notesDisplayMessage.visibility = View.GONE
                }
                else {
                    notesDisplayMessage.visibility = View.VISIBLE
                }

            }
        })
        setListner()
        manageTouch(adapter.getNotesListFromAdapter())

    }

    private fun setListner() {
        notesAddBtn.setOnClickListener {
            chooseFromPhone()
        }
    }

    private fun chooseFromPhone() {
         myNotesIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
//            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//             addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(Intent.createChooser(myNotesIntent,"Select PDF"), 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode) {
            10-> {
                if(resultCode == Activity.RESULT_OK) {
                    val path = data?.data.toString()
                    logD("$path")
                    addInfo(path)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addInfo(pathInfo: String?) {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.notes_add,null)
        /**set view*/
        val notesName = v.findViewById<EditText>(R.id.addNotesName)
        val notesDescription = v.findViewById<EditText>(R.id.addNotesDescription)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val notesInfo = notesName.text.toString()
            val descriptionInfo = notesDescription.text.toString()

            if(notesInfo.isEmpty()){
                toast("Fields cannot be empty")
                return@setPositiveButton
            }

            if(descriptionInfo.isEmpty()) {
                toast("Fields cannot be empty")
                return@setPositiveButton
            }

            viewModel.addNotes(Notes(notesInfo,descriptionInfo,pathInfo))
            Toast.makeText(this,"Notes Added", Toast.LENGTH_SHORT).show()
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

    private fun manageTouch(notes: ArrayList<Notes>) {
        val item = object : SwipeToDelete(this,
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END , ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition: Int = viewHolder.adapterPosition
                val toPosition : Int = target.adapterPosition
                Collections.swap(notes,fromPosition,toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition,toPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteNotes(notes[viewHolder.adapterPosition])
            }
        }
        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(notesRecycler)
    }
}