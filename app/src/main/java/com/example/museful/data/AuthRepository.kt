package com.example.museful.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val avatar_id: Int = 1
)

class AuthRepository {

    suspend fun register(email: String, password: String, username: String): Result<Unit> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Не удалось получить ID"))
            supabase.from("users").insert(
                UserProfile(id = userId, username = username, email = email)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }

    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            supabase.auth.updateUser {
                email = newEmail
            }
            val userId = supabase.auth.currentUserOrNull()?.id ?: return Result.failure(Exception("Ошибка"))
            supabase.from("users").update({ set("email", newEmail) }) { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            supabase.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = supabase.auth.currentUserOrNull() != null

    fun currentUserId(): String? = supabase.auth.currentUserOrNull()?.id
}