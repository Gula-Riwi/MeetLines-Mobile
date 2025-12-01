package com.meetline.app.domain.repository

import com.meetline.app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<User>
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
    suspend fun updateProfile(user: User): Result<User>
    suspend fun requestPasswordReset(email: String): Result<Boolean>
}
