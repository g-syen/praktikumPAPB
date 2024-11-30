package com.example.praktikumpapb


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth



class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState




    // Fungsi untuk check status autentikasi
    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated(currentUser.email)
        }
    }

    // Fungsi untuk login dengan email dan password
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated(auth.currentUser?.email)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    // Fungsi untuk registrasi akun baru
    fun signup(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value = AuthState.Error("Email, Password, or Confirm Password can't be empty")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated(auth.currentUser?.email)
                    // Navigasi ke halaman home setelah signup berhasil
                    // Anda dapat menggunakan navController jika tersedia
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong, please try again")
                }
            }
    }

    // Fungsi untuk sign out
    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

// State autentikasi
sealed class AuthState {
    data class Authenticated(val email: String?) : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}


data class User(
    val displayName: String,
    val photoUrl: String?,
    val email: String
)