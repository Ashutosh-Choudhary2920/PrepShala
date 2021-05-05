package com.study.prepshala.secretDiaryDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SecretDiary(
    var title: String,
    var description: String,
    var date: String,
    var time: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)