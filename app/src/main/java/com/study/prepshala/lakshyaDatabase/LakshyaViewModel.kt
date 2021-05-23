package com.study.prepshala.lakshyaDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LakshyaViewModel(application: Application): AndroidViewModel(application) {
    val allEntries: LiveData<List<Lakshya>>
    private val repository: LakshyaRepository

    init {
        val dao = LakshyaDatabase.getDatabase(application).getLakshyaDao()
        repository = LakshyaRepository(dao)
        allEntries = repository.allEntries
    }

    fun deleteEntries(lakshya: Lakshya) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(lakshya)
    }

    fun addEntries(lakshya: Lakshya) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(lakshya)
    }

}