package com.marcinmejner.miaumiau.chat

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.login.LoginActivity

class MainChatActivity : AppCompatActivity() {
    private val TAG = "MainChatActivity"

    private val contex = this@MainChatActivity

    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupFirebaseAuth()

    }

    /*
        ------------------------------FIREBASE -----------------------------------------
    */

    private fun checkCurrentUser(user: FirebaseUser?) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in")
        if (user == null) {
            val intent = Intent(contex, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        //        mAuth.signOut();
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            checkCurrentUser(user)

            if (user != null) {
                Log.d(TAG, "user signed_in:  " + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthStateListener!!)

        checkCurrentUser(mAuth?.getCurrentUser())
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth?.removeAuthStateListener(mAuthStateListener!!)
        }
    }

}
