package com.study.prepshala.eLectureDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Lecture :: class],
    version = 1,
    exportSchema = false
)
 abstract class LectureDatabase : RoomDatabase() {
    abstract fun getLectureDao() : LectureDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LectureDatabase? = null

        fun getDatabase(context: Context): LectureDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LectureDatabase::class.java,
                    "LectureDatabase"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}