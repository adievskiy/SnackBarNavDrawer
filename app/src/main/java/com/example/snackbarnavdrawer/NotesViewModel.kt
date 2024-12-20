package com.example.snackbarnavdrawer

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NotesViewModel : ViewModel() {
    private val _notesList = mutableStateListOf<Note>()
    val notesList get() = _notesList

    fun addNote(title: String, body: String) {
        _notesList.add(Note(title, body))
    }
}