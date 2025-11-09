package com.example.app.profile.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    private fun getUserProfileCollection() = db.collection("userProfiles")
    
    suspend fun saveAddress(address: Address): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val addressId = if (address.id.isEmpty()) {
                getUserProfileCollection()
                    .document(userId)
                    .collection("addresses")
                    .document().id
            } else {
                address.id
            }
            
            val addressData = address.copy(id = addressId)
            
            // If this is set as default, unset others
            if (addressData.isDefault) {
                val existingAddresses = getUserProfileCollection()
                    .document(userId)
                    .collection("addresses")
                    .get()
                    .await()
                
                existingAddresses.documents.forEach { doc ->
                    if (doc.id != addressId) {
                        doc.reference.update("isDefault", false).await()
                    }
                }
            }
            
            getUserProfileCollection()
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .set(addressData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAddresses(): Result<List<Address>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val snapshot = getUserProfileCollection()
                .document(userId)
                .collection("addresses")
                .get()
                .await()
            
            val addresses = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    Address(
                        id = doc.id,
                        fullName = data["fullName"] as? String ?: "",
                        phoneNumber = data["phoneNumber"] as? String ?: "",
                        addressLine1 = data["addressLine1"] as? String ?: "",
                        addressLine2 = data["addressLine2"] as? String ?: "",
                        city = data["city"] as? String ?: "",
                        state = data["state"] as? String ?: "",
                        zipCode = data["zipCode"] as? String ?: "",
                        country = data["country"] as? String ?: "",
                        isDefault = data["isDefault"] as? Boolean ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(addresses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            getUserProfileCollection()
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDefaultAddress(): Address? {
        return try {
            val result = getAddresses()
            result.getOrNull()?.find { it.isDefault }
        } catch (e: Exception) {
            null
        }
    }
}

