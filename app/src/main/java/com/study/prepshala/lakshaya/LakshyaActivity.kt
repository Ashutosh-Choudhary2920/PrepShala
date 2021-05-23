package com.study.prepshala.lakshaya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.study.prepshala.R
import com.study.prepshala.Utils.toast
import com.study.prepshala.eLectureDatabase.Lecture
import com.study.prepshala.lakshyaDatabase.Lakshya
import com.study.prepshala.lakshyaDatabase.LakshyaViewModel
import kotlinx.android.synthetic.main.activity_lakshaya_home.*

class LakshyaActivity : AppCompatActivity() {
    lateinit var viewModel: LakshyaViewModel
    private var allEntries = listOf<Lakshya>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lakshaya_home)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(LakshyaViewModel::class.java)

        viewModel.allEntries.observe(this, Observer {
            allEntries = it
            if (allEntries != null) {
                setViews(allEntries)
            }
        })

        setListners()
    }

    private fun setListners() {
        longTermCardView.setOnClickListener {
            setGoal("long")
        }

        shortTermCardView.setOnClickListener {
            setGoal("short")
        }

        longTermCardView.setOnLongClickListener {
            EditGoal("long")
        }

        shortTermCardView.setOnLongClickListener {
            EditGoal("short")
        }
    }

    private fun setViews(entries: List<Lakshya>) {
        for (i in 0..entries.size - 1) {
            if (entries[i].goalType == "long") {
                longTermGoal.setText(entries[i].goal)
            } else if (entries[i].goalType == "short") {
                shortTermGoal.setText(entries[i].goal)
            }
        }
    }

    private fun setGoal(goalType: String) {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.lakshya_add, null)

        /**set view*/
        val addGoal = v.findViewById<EditText>(R.id.addGoal)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val goal = addGoal.text.toString()

            if (goal.isEmpty()) {
                toast("Goal cannot be empty")
                return@setPositiveButton
            }

            viewModel.addEntries(Lakshya(goalType, goal))
            Toast.makeText(this, "Goal Added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    private fun EditGoal(goalType: String): Boolean {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.lakshya_add, null)

        /**set view*/
        val addGoal = v.findViewById<EditText>(R.id.addGoal)
        if (goalType == "long") {
            addGoal.setText(longTermGoal.text.toString())
        } else {
            addGoal.setText(shortTermGoal.text.toString())
        }
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val goal = addGoal.text.toString()

            if (goal.isEmpty()) {
                toast("Goal cannot be empty")
                return@setPositiveButton
            }

            viewModel.addEntries(Lakshya(goalType, goal))
            Toast.makeText(this, "Goal Added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
        return true
    }
}