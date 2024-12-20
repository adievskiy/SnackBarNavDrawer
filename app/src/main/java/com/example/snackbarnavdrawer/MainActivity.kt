package com.example.snackbarnavdrawer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val notes = mutableStateListOf<Note>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScreen()
        }
    }
}

@Composable
private fun AppScreen() {
    var navController by remember { mutableStateOf("main") }

    when (navController) {
        "main" -> MainScreen(
            notes = notes,
            onDeleteNote = { note ->
                notes.remove(note)
            },
            onNavigateToAddNote = { navController = "add" }
        )

        "add" -> AddNoteScreen(onSaveNote = { note ->
            notes.add(note)
            navController = "main"
        })
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(notes: List<Note>, onDeleteNote: (Note) -> Unit, onNavigateToAddNote: () -> Unit) {
    val scrollState = rememberScrollState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddNote) {
                Icon(Icons.Default.Add, contentDescription = "New note")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column {
                        notes.forEach { note ->
                            Row(
                                Modifier
                                    .padding(5.dp)
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                            ) {
                                IconButton(
                                    onClick = {

                                        if (notes.size > 1) {
                                            onDeleteNote(note)
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Должна остаться хотя бы одна заметка")
                                            }
                                        }
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "delete")
                                }
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            selectedNote = note
                                        }
                                    }
                                ) {
                                    Text(
                                        text = note.title,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                },
                content = {
                    Column(Modifier.fillMaxSize()) {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        },
                            content = { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                        )
                        Text(
                            text = selectedNote?.title ?: "Нет ни одной заметки",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = selectedNote?.body ?: "",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                scrimColor = Color.DarkGray
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddNoteScreen(onSaveNote: (Note) -> Unit) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 18.sp
                )

            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Содержание") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 18.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (title.isNotBlank() && body.isNotBlank()) {
                    onSaveNote(Note(title, body))
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Заполните оба поля")
                    }
                }
            }) {
                Text("Сохранить заметку")
            }
        }
    }
}