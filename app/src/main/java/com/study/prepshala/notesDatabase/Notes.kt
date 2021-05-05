package com.study.prepshala.notesDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Notes (
    var notesName: String,
    var notesDescription: String,
    var notesPath: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)