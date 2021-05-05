package com.study.prepshala.eLectureDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LectureViewModel(application: Application): AndroidViewModel(application) {
    val allLecture: LiveData<List<Lecture>>

    private val repository:LectureRepository

    init {
        val dao = LectureDatabase.getDatabase(application).getLectureDao()
        repository =  LectureRepository(dao)
        allLecture = repository.allLecture
    }

    fun deleteLecture(lecture: Lecture) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(lecture)
    }

    fun addLecture(lecture: Lecture) = viewModelScope.launch (Dispatchers.IO){
        repository.insert(lecture)
    }
}