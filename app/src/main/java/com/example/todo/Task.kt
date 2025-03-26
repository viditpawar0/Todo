package com.example.todo

data class Task(
    val title: String = "",
    val priority: Int = 0,
    val completed: Boolean = false,
): FirestoreEntity<TaskDto> {
    override fun toD(id: String): TaskDto = TaskDto(title, priority, completed, id)
}