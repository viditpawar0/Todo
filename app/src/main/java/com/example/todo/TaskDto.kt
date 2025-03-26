package com.example.todo

class TaskDto(
    var title: String = "",
    var priority: Int = 0,
    var completed: Boolean = false,
    val id: String? = null
): FirestoreEntityDto<Task> {
    override fun getDocumentID(): String? = id
    override fun isNew(): Boolean = id == null
    override fun toE(): Task = Task(title, priority, completed)
}