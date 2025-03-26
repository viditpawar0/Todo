package com.example.todo

interface FirestoreEntity<D> {
    fun toD(id: String): D
}