package com.example.todo

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CrudDao<E: FirestoreEntity<D>, D: FirestoreEntityDto<E>>(
    collectionName: String,
    val clazz: Class<E>
) {
    private val collection = Firebase.firestore.collection(collectionName)

    fun add(dto : FirestoreEntityDto<E>) {
        collection.add(dto.toE())
    }
    fun update(dto : FirestoreEntityDto<E>) {
        collection.document(checkNotNull(dto.getDocumentID())).set(dto.toE())
    }
    fun delete(id : String) {
        collection.document(id).delete()
    }
    fun delete(dto : FirestoreEntityDto<E>) {
        collection.document(checkNotNull(dto.getDocumentID())).delete()
    }
    fun getAll(onResult: (result: List<FirestoreEntityDto<E>>) -> Unit) {
        collection.get().addOnSuccessListener {
            onResult(it.map { it.toObject(clazz).toD(it.id) })
        }
    }
    fun get(id: String, onResult: (result: FirestoreEntityDto<E>?) -> Unit) {
        collection.document(id).get().addOnSuccessListener {
            onResult(it.toObject(clazz)?.toD(it.id))
        }
    }
}