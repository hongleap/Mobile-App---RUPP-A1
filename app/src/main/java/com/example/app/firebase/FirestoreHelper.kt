package com.example.app.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Add a document to a collection
     */
    suspend fun addDocument(
        collection: String,
        data: Map<String, Any>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = db.collection(collection).add(data).await()
            onSuccess(result.id)
        } catch (e: Exception) {
            onError("Error adding document: ${e.message}")
        }
    }

    /**
     * Add a document with a specific ID
     */
    suspend fun setDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            db.collection(collection).document(documentId).set(data).await()
            onSuccess()
        } catch (e: Exception) {
            onError("Error setting document: ${e.message}")
        }
    }

    /**
     * Get a document by ID
     */
    suspend fun getDocument(
        collection: String,
        documentId: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val document = db.collection(collection).document(documentId).get().await()
            if (document.exists()) {
                val data = document.data as? Map<String, Any>
                onSuccess(data)
            } else {
                onSuccess(null)
            }
        } catch (e: Exception) {
            onError("Error getting document: ${e.message}")
        }
    }

    /**
     * Get all documents from a collection
     */
    suspend fun getDocuments(
        collection: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = db.collection(collection).get().await()
            val documents = result.documents.mapNotNull { doc ->
                doc.data
            }
            onSuccess(documents)
        } catch (e: Exception) {
            onError("Error getting documents: ${e.message}")
        }
    }

    /**
     * Query documents with filters
     */
    suspend fun queryDocuments(
        collection: String,
        field: String,
        value: Any,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = db.collection(collection)
                .whereEqualTo(field, value)
                .get()
                .await()
            val documents = result.documents.mapNotNull { doc ->
                doc.data
            }
            onSuccess(documents)
        } catch (e: Exception) {
            onError("Error querying documents: ${e.message}")
        }
    }

    /**
     * Update a document
     */
    suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            db.collection(collection).document(documentId).update(data).await()
            onSuccess()
        } catch (e: Exception) {
            onError("Error updating document: ${e.message}")
        }
    }

    /**
     * Delete a document
     */
    suspend fun deleteDocument(
        collection: String,
        documentId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            db.collection(collection).document(documentId).delete().await()
            onSuccess()
        } catch (e: Exception) {
            onError("Error deleting document: ${e.message}")
        }
    }

    /**
     * Listen to real-time updates from a collection
     */
    fun listenToCollection(
        collection: String,
        onUpdate: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection(collection)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError("Error listening: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val documents = snapshot.documents.mapNotNull { doc ->
                        doc.data
                    }
                    onUpdate(documents)
                }
            }
    }

    /**
     * Listen to real-time updates from a single document
     */
    fun listenToDocument(
        collection: String,
        documentId: String,
        onUpdate: (Map<String, Any>?) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError("Error listening: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data as? Map<String, Any>
                    onUpdate(data)
                } else {
                    onUpdate(null)
                }
            }
    }
}

