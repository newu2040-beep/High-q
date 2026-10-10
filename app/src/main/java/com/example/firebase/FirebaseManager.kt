package com.example.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.model.VideoProject
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean = false
)

sealed class AuthResult {
    data class Success(val user: UserProfile) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class FirebaseManager private constructor(private val context: Context) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    private val _currentUserState = MutableStateFlow<UserProfile?>(null)
    val currentUserState: StateFlow<UserProfile?> = _currentUserState.asStateFlow()

    init {
        val user = auth.currentUser
        if (user != null) {
            _currentUserState.value = user.toProfile()
        }
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    suspend fun signInWithGoogle(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val profile = firebaseUser.toProfile()
                    _currentUserState.value = profile
                    saveUserToFirestore(profile)
                    AuthResult.Success(profile)
                } else {
                    AuthResult.Error("Google Sign-In returned null user")
                }
            } else {
                AuthResult.Error("Unsupported credential type: ${credential.type}")
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "CredentialManager error", e)
            AuthResult.Error(e.localizedMessage ?: "Google sign-in cancelled or failed")
        } catch (e: Exception) {
            Log.e(TAG, "Google sign in error", e)
            AuthResult.Error(e.localizedMessage ?: "Authentication failed")
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user
            if (user != null) {
                val profile = user.toProfile()
                _currentUserState.value = profile
                AuthResult.Success(profile)
            } else {
                AuthResult.Error("User login failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Email login error", e)
            AuthResult.Error(e.localizedMessage ?: "Invalid email or password")
        }
    }

    suspend fun registerWithEmail(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user
            if (user != null) {
                val profile = user.toProfile()
                _currentUserState.value = profile
                saveUserToFirestore(profile)
                AuthResult.Success(profile)
            } else {
                AuthResult.Error("Registration failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            AuthResult.Error(e.localizedMessage ?: "Failed to create account")
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUserState.value = null
    }

    private suspend fun saveUserToFirestore(profile: UserProfile) {
        try {
            val data = mapOf(
                "uid" to profile.uid,
                "email" to (profile.email ?: ""),
                "displayName" to (profile.displayName ?: ""),
                "photoUrl" to (profile.photoUrl ?: ""),
                "lastActive" to System.currentTimeMillis()
            )
            firestore.collection("users").document(profile.uid)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync user to Firestore", e)
        }
    }

    suspend fun syncProjectToFirestore(project: VideoProject) = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext
        try {
            val data = mapOf(
                "id" to project.id,
                "title" to project.title,
                "durationSeconds" to project.durationSeconds,
                "sourceResolution" to project.sourceResolution,
                "targetResolution" to project.targetResolution,
                "enhancementType" to project.enhancementType,
                "enhancementPreset" to project.enhancementPreset,
                "fps" to project.fps,
                "bitrateMbps" to project.bitrateMbps,
                "format" to project.format,
                "fileSizeFormatted" to project.fileSizeFormatted,
                "createdAtMillis" to project.createdAtMillis,
                "isEnhanced" to project.isEnhanced
            )
            firestore.collection("users").document(uid)
                .collection("projects").document(project.id)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore project sync error", e)
        }
    }

    private fun FirebaseUser.toProfile(): UserProfile {
        return UserProfile(
            uid = uid,
            email = email,
            displayName = displayName ?: email?.substringBefore("@"),
            photoUrl = photoUrl?.toString(),
            isAnonymous = isAnonymous
        )
    }

    companion object {
        private const val TAG = "FirebaseManager"
        const val WEB_CLIENT_ID = "530218685955-3ha9faug73qlr2npsrdmftt43qd4rh0d.apps.googleusercontent.com"

        @Volatile
        private var INSTANCE: FirebaseManager? = null

        fun getInstance(context: Context): FirebaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
