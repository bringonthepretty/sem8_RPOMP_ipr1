package com.wah.sem8_rpomp_ipr1.ui.theme

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.wah.sem8_rpomp_ipr1.MainActivity
import com.wah.sem8_rpomp_ipr1.model.Note

@Composable
fun NotesScreen(
    launcher: ActivityResultLauncher<Intent>,
    intent: Intent
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dp(10f)),
        verticalArrangement = Arrangement.spacedBy(Dp(2f))
    ) {
        var notesListState by remember {
            mutableStateOf(listOf<Note>())
        }
        Header(
            launcher, intent, notesListState.toMutableList()
        ) { note ->
            notesListState = notesListState + note
        }
        NotesList(notesListState) {
            val mutableRepresentation = notesListState.toMutableList()
            mutableRepresentation.removeAt(it)
            notesListState = mutableRepresentation
        }
    }
}

@Composable
fun NotesList(
    notes: List<Note>,
    onNoteDelete: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dp(10f))
    ) {
        itemsIndexed(notes) {index, it ->
            Note(note = it) {
                onNoteDelete(index)
            }
        }
    }
}

@Composable
fun Header(
    launcher: ActivityResultLauncher<Intent>,
    intent: Intent,
    notes: MutableList<Note>,
    onNoteAdded: (Note) -> Unit
) {
    var titleState by remember {
        mutableStateOf("")
    }
    var textState by remember {
        mutableStateOf("")
    }
    TextField(
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Gray,
            unfocusedContainerColor = Color.Gray,
            focusedTextColor = Color.Cyan,
            unfocusedTextColor = Color.Cyan,
            focusedLabelColor = Color.Cyan,
            unfocusedLabelColor = Color.Cyan
        ),
        value = titleState,
        onValueChange = {titleState = it},
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Title") }
    )
    TextField(
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Gray,
            unfocusedContainerColor = Color.Gray,
            focusedTextColor = Color.Cyan,
            unfocusedTextColor = Color.Cyan,
            focusedLabelColor = Color.Cyan,
            unfocusedLabelColor = Color.Cyan
        ),
        value = textState,
        onValueChange = {textState = it},
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Text") }
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ){
            Button(
                modifier = Modifier.padding(Dp(5f)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                onClick = { onNoteAdded(Note(titleState, textState)) }
            ) {
                Text(text = "Add note", color = Color.Cyan)
            }
            Button(
                modifier = Modifier.padding(Dp(5f)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                onClick = {
                    intent.putExtra("data", notes.joinToString("\n"))
                    launcher.launch(intent)
                }
            ) {
                Text(text = "Upload to GDrive", color = Color.Cyan)
            }
        }
    }
}

@Composable
fun Note(
    note: Note,
    onNoteDelete: () -> Unit
) {
    Column {
        Text(text = note.title, color = Color.Cyan)
        Text(text = note.text, color = Color.Cyan)
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            onClick = { onNoteDelete() }
        ) {
            Text(text = "Delete", color = Color.Cyan)
        }
    }
}
