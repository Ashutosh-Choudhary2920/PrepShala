package com.study.prepshala.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.pdfReader.PdfViewActivity
import com.study.prepshala.R
import com.study.prepshala.notesDatabase.Notes

class NotesAdapter(private val context: Context): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val allNotes = ArrayList<Notes>()

    inner class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val notesName: TextView = itemView.findViewById(R.id.notesName)
        val notesDescription: TextView = itemView.findViewById(R.id.notesDescription)
        val notesPath: TextView = itemView.findViewById(R.id.notesPath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val viewHolder = NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_item,parent,false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = allNotes[position]
        holder.notesName.text = currentNote.notesName.toString()
        holder.notesDescription.text = currentNote.notesDescription.toString()
        holder.notesPath.text = currentNote.notesPath.toString()

        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(context, PdfViewActivity::class.java)
            intent.putExtra(PdfViewActivity.path,currentNote.notesPath)
            context.startActivity(intent)
        }
    }

    fun updateList(newList: List<Notes>) {
        allNotes.clear()
        allNotes.addAll(newList)
        notifyDataSetChanged()
    }

    fun getNotesListFromAdapter():ArrayList<Notes>{
        return allNotes
    }
}