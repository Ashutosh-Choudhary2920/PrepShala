package com.study.prepshala.lakshyaDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Lakshya(
    @PrimaryKey
    var goalType: String,
    var goal: String
)