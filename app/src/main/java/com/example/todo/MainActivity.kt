package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Root()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun Root(modifier: Modifier = Modifier) {
        val viewModel = viewModel<TodoViewModel>()
        TodoTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text("Todo")
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            openNewTaskDialog(viewModel)
                        }
                    ) {
                        Icon(Icons.Default.Add, "New Task")
                    }
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                ) {
                    items(viewModel.tasks.size) {
                        val task = viewModel.tasks[it]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    openTaskEditorDialog(viewModel, task)
                                }
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        task.title,
                                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                                        modifier = Modifier.weight(2f),
                                    )
                                },
                                leadingContent = {
                                    Checkbox(
                                        modifier = Modifier.weight(1f),
                                        checked = task.completed,
                                        onCheckedChange = {
                                            task.completed = it
                                            viewModel.updateTask(task)
                                        }
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        task.priority.toString(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    }
                }
            }
            when {viewModel.taskDialogOpen ->
                Dialog(
                    onDismissRequest = { viewModel.taskDialogOpen = false }
                ) {
                    viewModel.titleInput = viewModel.activeTask.title
                    viewModel.priorityInput = viewModel.activeTask.priority
                    viewModel.completedChecked = viewModel.activeTask.completed
                    Card {
                        Box(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextField(
                                    value = viewModel.titleInput,
                                    onValueChange = { viewModel.titleInput = it },
                                    label = { Text("Title") },
                                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
                                )
                                TextButton(
                                    { viewModel.priorityMenuOpen = true }
                                ) {
                                    Text("Priority: ${viewModel.priorityInput}")
                                }
                                DropdownMenu(
                                    expanded = viewModel.priorityMenuOpen,
                                    onDismissRequest = { viewModel.priorityMenuOpen = false }
                                ) {
                                    for (i in 1 .. 5) {
                                        DropdownMenuItem(
                                            text = { Text(i.toString()) },
                                            onClick = {
                                                viewModel.priorityInput = i
                                                viewModel.priorityMenuOpen = false
                                            }
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = viewModel.completedChecked,
                                        onCheckedChange = { viewModel.completedChecked = it }
                                    )
                                    Text("Completed")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    if (!viewModel.activeTask.isNew()) {
                                        Button({
                                            viewModel.deleteTask(viewModel.activeTask)
                                            viewModel.taskDialogOpen = false
                                            viewModel.activeTask = TaskDto()
                                            viewModel.titleInput = ""
                                            viewModel.priorityInput = 0
                                            viewModel.completedChecked = false
                                        }) { Text("Delete") }
                                    }
                                    Button({
                                        viewModel.activeTask.title = viewModel.titleInput
                                        viewModel.activeTask.priority = viewModel.priorityInput
                                        viewModel.activeTask.completed = viewModel.completedChecked
                                        if (viewModel.activeTask.isNew()) {
                                            viewModel.addTask(viewModel.activeTask)
                                        } else {
                                            viewModel.updateTask(viewModel.activeTask)
                                        }
                                        viewModel.taskDialogOpen = false
                                        viewModel.activeTask = TaskDto()
                                        viewModel.titleInput = ""
                                        viewModel.priorityInput = 0
                                        viewModel.completedChecked = false
                                    }) {
                                        Text(if (viewModel.activeTask.isNew()) "Add" else "Update")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun openTaskEditorDialog(viewModel: TodoViewModel, task: TaskDto) {
        viewModel.activeTask = task
        viewModel.titleInput = task.title
        viewModel.priorityInput = task.priority
        viewModel.completedChecked = task.completed
        viewModel.taskDialogOpen = true
    }
    fun openNewTaskDialog(viewModel: TodoViewModel) {
        viewModel.activeTask = TaskDto()
        viewModel.titleInput = ""
        viewModel.priorityInput = 0
        viewModel.completedChecked = false
        viewModel.taskDialogOpen = true
    }
}