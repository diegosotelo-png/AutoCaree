package com.auto_care_test.data.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthDataSource(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun registerUser(email: String, password: String, nombre: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val profileUpdates = userProfileChangeRequest {
                displayName = nombre
            }
            authResult.user?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
