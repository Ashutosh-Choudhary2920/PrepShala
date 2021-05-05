package com.study.prepshala.notesDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes : Notes)

    @Delete
    suspend fun delete(notes : Notes)

    @Query("SELECT * FROM Notes ORDER by id ASC")
    fun getAllNotes() : LiveData<List<Notes>>
}