package com.study.prepshala.toDo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoModel::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun todoDao(): TodoDao
}