package com.marcinmejner.miaumiau.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.chat.MainChatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    //vars
    lateinit var userEmail: EditText
    lateinit var userPassowrd: EditText
    lateinit var progressBar: ProgressBar
    lateinit var loginBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupFirebaseAuth()

        init()

    }

    fun init() {
        initWidgets()
        initLogin()
        initRegisterNewUser()
    }

    private fun initWidgets() {
        userEmail = user_email_ed
        userPassowrd = user_passowrd_ed
        progressBar = login_progresbar
        loginBtn = login_btn
    }

    private fun isStringNull(string: String): Boolean {
        return string == ""
    }

    /*--------------------------------------------------------------------------------
      ------------------------------FIREBASE -----------------------------------------
      --------------------------------------------------------------------------------*/

    private fun initLogin() {

        loginBtn.setOnClickListener {
            Log.d(TAG, "initLogin: attempt to login")

            val emailText = userEmail.text.toString()
            val passwordText = userPassowrd.text.toString()

            if (isStringNull(emailText) || isStringNull(passwordText)) run {
                Toast.makeText(this@LoginActivity, "Musisz wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE

                mAuth?.signInWithEmailAndPassword(emailText, passwordText)
                        ?.addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                val user = mAuth?.currentUser
                                Log.d(TAG, "initLogin: user to: $user")

                                val intent = Intent(this@LoginActivity, MainChatActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(this@LoginActivity, "Nie udało sie zalogować.",
                                        Toast.LENGTH_SHORT).show()
                                progressBar.visibility = View.GONE

                            }
                        }
            }
        }
    }

    fun initRegisterNewUser(){
        register_new_user_btn.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    /*Setup Firebase*/

    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                Log.d(TAG, "onAuthStateChanged: user signed_in" + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthStateListener!!)

    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth?.removeAuthStateListener(mAuthStateListener!!)
        }
    }
}
