package com.study.prepshala.eLectureDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LectureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lecture : Lecture)

    @Delete
    suspend fun delete(lecture : Lecture)

    @Query("SELECT * FROM Lecture ORDER by id ASC")
    fun getAllLectures() : LiveData<List<Lecture>>
}