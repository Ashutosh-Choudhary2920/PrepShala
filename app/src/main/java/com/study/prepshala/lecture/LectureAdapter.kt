package com.study.prepshala.lecture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.R
import com.study.prepshala.eLectureDatabase.Lecture

class LectureAdapter(private val context: Context, private val listener: iLectureAdapter): RecyclerView.Adapter<LectureAdapter.LectureViewHolder>() {

    private val allLecture = ArrayList<Lecture>()

    inner class LectureViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val lectureTitle:TextView = itemView.findViewById<TextView>(R.id.lectureTitle)
        val lectureFaculty:TextView = itemView.findViewById<TextView>(R.id.lectureFaculty)
        val lectureUrl:TextView = itemView.findViewById<TextView>(R.id.lectureUrlItem)
        val lectureContainer:CardView = itemView.findViewById<CardView>(R.id.lectureContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val viewHolder = LectureViewHolder(LayoutInflater.from(context).inflate(R.layout.lecture_item,parent,false))
        viewHolder.lectureContainer.setOnClickListener {
            listener.onItemClicked(allLecture[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return allLecture.size
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val currentNode = allLecture[position]
        holder.lectureFaculty.text = currentNode.facultyName.toString()
        holder.lectureTitle.text = currentNode.lectureName.toString()
        holder.lectureUrl.text = currentNode.lectureUrl.toString()
        holder.itemView.setOnClickListener{
            val webIntent: Intent = Uri.parse(currentNode.lectureUrl.toString()).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            context.startActivity(webIntent)
        }

    }

    fun updateList(newList: List<Lecture>) {
        allLecture.clear()
        allLecture.addAll(newList)
        notifyDataSetChanged()
    }

    fun getLectureListFromAdapter():ArrayList<Lecture>{
        return allLecture
    }

    interface iLectureAdapter {
        fun onItemClicked(lecture: Lecture)
    }
}