package com.example.listascolaborativas.helper


import com.example.listascolaborativas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// getDatabase: retorna a referencia do banco de dados
// getUserAutenticated: retorna a instanacia do usuario logado
// getUserId: recupera o id do usuario logado
// isAutenticated: verifica se o usuario está autenticado
// validError: identifica as mensagens de erro do firebase, e entao exibe elas em portugues ao usuario
class FirebaseHelper {

    companion object {
        fun getDatabase() = FirebaseDatabase.getInstance().reference
        private fun getUserAutenticated() = FirebaseAuth.getInstance()
        fun getUserId() = getUserAutenticated().uid
        fun isAutenticated() = getUserAutenticated().currentUser != null

        // identifica as mensagens de erro do firebase
        // e entao exibe elas em portugues ao usuario
        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier") -> {
                    R.string.account_not_registered_register_fragment
                }
                error.contains("The email address is badly formatted") -> {
                    R.string.invalid_email_register_fragment
                }
                error.contains("The supplied auth credential is incorrect, malformed or has expired") -> {
                    R.string.incorrect_email_or_password_register_fragment
                }error.contains("The supplied auth credential is incorrect, malformed or has expired") -> {
                    R.string.invalid_email_or_password_register_fragment
                }
                error.contains("The password is invalid or the user does not have a password") -> {
                    R.string.invalid_password_register_fragment
                }
                error.contains("The email address is already in use by another account") -> {
                    R.string.email_in_use_register_fragment
                }
                error.contains("Password should be at least 6 characters") -> {
                    R.string.strong_password_register_fragment
                }
                else -> {
                    R.string.error_generic
                }
            }
        }
    }

}