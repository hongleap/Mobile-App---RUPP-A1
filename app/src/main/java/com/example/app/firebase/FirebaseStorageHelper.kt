package com.example.app.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

object FirebaseStorageHelper {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    /**
     * Upload an image to Firebase Storage
     * 
     * @param imageUri The URI of the image to upload
     * @param folder The folder path in Storage (e.g., "products", "users", "categories")
     * @param onSuccess Callback with the download URL
     * @param onError Callback with error message
     */
    fun uploadImage(
        imageUri: Uri,
        folder: String = "images",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Generate unique filename
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageRef: StorageReference = storageRef.child("$folder/$fileName")

            // Upload file
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Get download URL
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            onSuccess(uri.toString())
                        }
                        .addOnFailureListener { exception ->
                            onError("Failed to get download URL: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    onError("Upload failed: ${exception.message}")
                }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }

    /**
     * Upload multiple images
     */
    fun uploadImages(
        imageUris: List<Uri>,
        folder: String = "images",
        onProgress: (Int, Int) -> Unit = { _, _ -> }, // current, total
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val uploadedUrls = mutableListOf<String>()
        var completed = 0
        var hasError = false

        imageUris.forEachIndexed { index, uri ->
            uploadImage(
                imageUri = uri,
                folder = folder,
                onSuccess = { url ->
                    if (!hasError) {
                        uploadedUrls.add(url)
                        completed++
                        onProgress(completed, imageUris.size)
                        
                        if (completed == imageUris.size) {
                            onSuccess(uploadedUrls)
                        }
                    }
                },
                onError = { error ->
                    if (!hasError) {
                        hasError = true
                        onError("Failed to upload image ${index + 1}: $error")
                    }
                }
            )
        }
    }

    /**
     * Delete an image from Firebase Storage
     */
    fun deleteImage(
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onError("Delete failed: ${exception.message}")
                }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }

    /**
     * Get download URL from Storage path
     */
    fun getDownloadUrl(
        path: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageRef = storageRef.child(path)
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onError("Failed to get URL: ${exception.message}")
            }
    }
}

