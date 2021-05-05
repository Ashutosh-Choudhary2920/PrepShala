package com.study.prepshala.eLectureDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Lecture (
    var lectureName: String,
    var facultyName: String,
    var lectureUrl: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)