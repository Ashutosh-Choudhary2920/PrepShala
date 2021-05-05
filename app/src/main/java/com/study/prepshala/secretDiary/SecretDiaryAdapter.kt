package com.study.prepshala.secretDiary

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.study.prepshala.R
import com.study.prepshala.Utils.getCurrentDate

import com.study.prepshala.secretDiary.SecretDiaryAdapter.DiaryViewHolder
import com.study.prepshala.secretDiaryDatabase.SecretDiary

class SecretDiaryAdapter(private val context: Context): RecyclerView.Adapter<DiaryViewHolder>() {

    private val allEntries = ArrayList<SecretDiary>()

    inner class DiaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val diaryTime : TextView = itemView.findViewById(R.id.diaryTime)
        val diaryDate : TextView = itemView.findViewById(R.id.diaryDate)
        val diaryTitle: TextView = itemView.findViewById(R.id.diaryTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val viewHolder = DiaryViewHolder(LayoutInflater.from(context).inflate(R.layout.diary_item, parent, false))
        return viewHolder
    }

    override fun getItemCount(): Int {
        return allEntries.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentNode = allEntries[position]
        holder.diaryTitle.text = currentNode.title
        holder.diaryDate.text = currentNode.date
        holder.diaryTime.text = currentNode.time

        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(context, DiaryContent::class.java)
            intent.putExtra(DiaryContent.clickType,"display")
            intent.putExtra(DiaryContent.position,"$position")
//            intent.putExtra(DiaryContent.id,"${currentNode.id}")
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            if(currentNode.date != getCurrentDate()) {
                Toast.makeText(context, "Cannot update previous Diary!", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener false
            }

            val intent: Intent = Intent(context, DiaryContent::class.java)
            intent.putExtra(DiaryContent.clickType,"edit")
            intent.putExtra(DiaryContent.position,"$position")
            context.startActivity(intent)
            return@setOnLongClickListener true
        }

    }

    fun updateList(newList: List<SecretDiary>) {
        allEntries.clear()
        allEntries.addAll(newList)
        notifyDataSetChanged()
    }

    fun getDiaryListFromAdapter():ArrayList<SecretDiary>{
        return allEntries
    }
}