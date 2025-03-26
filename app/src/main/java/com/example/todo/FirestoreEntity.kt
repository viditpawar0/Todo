package com.example.todo

interface FirestoreEntity<D> {
    fun toDto(id: String): D
}