package com.example.legalchain.auth

import android.app.Activity
import android.content.Context
import com.example.legalchain.R
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthHelper(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val googleClient = GoogleSignIn.getClient(activity, gso)

    fun getSignInIntent(): Intent = googleClient.signInIntent

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (email: String, uid: String, isNewUser: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(Exception::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { result ->
                    if (result.isSuccessful) {

                        val user = auth.currentUser!!

                        onSuccess(
                            user.email ?: "",
                            user.uid,
                            result.result?.additionalUserInfo?.isNewUser == true
                        )

                    } else {
                        onError("Firebase authentication failed")
                    }
                }

        } catch (e: Exception) {
            onError(e.message ?: "Google sign-in failed")
        }
    }
}
