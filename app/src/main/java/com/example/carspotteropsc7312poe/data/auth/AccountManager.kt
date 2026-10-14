package com.example.carspotteropsc7312poe.data.auth

import android.content.Context
import com.example.carspotteropsc7312poe.data.local.AppDatabase
import com.example.carspotteropsc7312poe.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Creates accounts and signs users in, entirely on the device. */
class AccountManager(context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()
    val session = SessionManager(context)

    sealed class Result {
        data class Success(val user: User) : Result()
        object EmailTaken : Result()
        object InvalidCredentials : Result()
    }

    suspend fun register(email: String, password: String): Result {
        val normalized = normalize(email)
        if (userDao.findByEmail(normalized) != null) return Result.EmailTaken

        val user = withContext(Dispatchers.Default) {
            val salt = PasswordHasher.newSalt()
            User(email = normalized, passwordHash = PasswordHasher.hash(password, salt), salt = salt)
        }
        val saved = user.copy(id = userDao.insert(user))
        session.signIn(saved.id)
        return Result.Success(saved)
    }

    suspend fun signIn(email: String, password: String): Result {
        val user = userDao.findByEmail(normalize(email)) ?: return Result.InvalidCredentials
        val ok = withContext(Dispatchers.Default) {
            PasswordHasher.verify(password, user.salt, user.passwordHash)
        }
        if (!ok) return Result.InvalidCredentials
        session.signIn(user.id)
        return Result.Success(user)
    }

    /** Signs the previous user back in after a successful biometric check. */
    suspend fun signInLastUser(): Boolean {
        val user = userDao.findById(session.lastUserId) ?: return false
        session.signIn(user.id)
        return true
    }

    fun hasLastUser(): Boolean = session.lastUserId != 0L

    suspend fun currentUser(): User? = if (session.isSignedIn) userDao.findById(session.currentUserId) else null

    fun signOut() = session.signOut()

    private fun normalize(email: String) = email.trim().lowercase()
}
