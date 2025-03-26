package com.example.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TodoViewModel: ViewModel() {
    val taskDao = CrudDao<Task, TaskDto>("tasks", Task::class.java)
    var tasks by mutableStateOf<List<TaskDto>>(emptyList())
    var taskDialogOpen by mutableStateOf(false)
    var activeTask by mutableStateOf<TaskDto>(TaskDto())
    var titleInput by mutableStateOf("")
    var priorityInput by mutableIntStateOf(0)
    var priorityMenuOpen by mutableStateOf(false)
    var completedChecked by mutableStateOf(false)

    init {
        fetchTasks()
    }

    fun addTask(task: TaskDto) {
        taskDao.add(task)
        fetchTasks()
    }

    fun updateTask(task: TaskDto) {
        taskDao.update(task)
        fetchTasks()
    }

    fun deleteTask(task: TaskDto) {
        taskDao.delete(task)
        fetchTasks()
    }

    private fun fetchTasks() {
        taskDao.getAll { tasks = (it as List<TaskDto>).sortedWith(compareBy<TaskDto> { it.completed }.thenByDescending { it.priority }) }
    }
}