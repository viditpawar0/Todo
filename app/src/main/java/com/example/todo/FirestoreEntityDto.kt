package com.example.todo

interface FirestoreEntityDto<E> {
    fun getDocumentID(): String?
    fun isNew(): Boolean
    fun toT(): E
}