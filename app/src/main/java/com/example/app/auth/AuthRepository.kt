package com.example.app.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }
    
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Sign in failed"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Account creation failed"))
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Account creation failed"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            _isLoading.value = true
            _errorMessage.value = null
            
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Failed to send password reset email"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _errorMessage.value = null
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
}

