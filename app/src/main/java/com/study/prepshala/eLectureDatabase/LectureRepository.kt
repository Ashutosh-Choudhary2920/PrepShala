package com.study.prepshala.eLectureDatabase

import androidx.lifecycle.LiveData

class LectureRepository(private val lectureDao: LectureDao) {

    val allLecture: LiveData<List<Lecture>> = lectureDao.getAllLectures()
    suspend fun insert(lecture: Lecture){
        lectureDao.insert(lecture)
    }
    suspend fun delete(lecture: Lecture) {
        lectureDao.delete(lecture)
    }
}