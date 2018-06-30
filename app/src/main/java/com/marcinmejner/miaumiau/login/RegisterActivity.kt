package com.marcinmejner.miaumiau.login

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.models.UserAccount
import com.marcinmejner.miaumiau.utils.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    private var email: String? = ""
    private var username:String? = ""
    private var password:String? = ""

    var context: Context? = null

    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var firebaseFunctions: FirebaseFunctions? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    private var append = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "onCreate: started")

        context = this@RegisterActivity
        firebaseFunctions = FirebaseFunctions(this)

        setupFirebaseAuth()

        init()

    }

    private fun init() {
        register_new_user_btn.setOnClickListener {
            email = user_email_ed.text.toString()
            username = username_ed.text.toString()
            password = user_passowrd_ed.text.toString()
            Log.d(TAG, "onClick: inside register")

            if (checkInputs(email!!, username!!, password!!)) {
                register_progressbar.visibility = View.VISIBLE

                firebaseFunctions?.registerNewEmail(email!!, password!!, username!!)

            }
        }
    }

    private fun checkInputs(email: String, username: String, password: String): Boolean {
        Log.d(TAG, "checkInputs: checking for null")
        if (email == "" || username == "" || password == "") {
            Toast.makeText(context, "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    /*Setup Firebase*/

    /**
     * sprawdzamy czy @param username juz istnieje w bazie danych
     * @param username
     */
    private fun checkIfUsernameExists(username: String) {
        Log.d(TAG, "checkIfUsernameExists: sprawdzamy czy " + username + "juz istnieje w bazie")

        val reference = FirebaseDatabase.getInstance().reference
        val query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (dataSnapshot1 in dataSnapshot.children) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists FOUND A MATCH " + dataSnapshot.getValue<UserAccount>(UserAccount::class.java)!!.username)
                        append = myRef?.push()!!.key.substring(3, 10)
                        Log.d(TAG, "onDataChange: Username already exists, appending random string to name: $append")
                    }
                }

                var mUsername = ""
                mUsername = username + append

                //adding user to databse

                firebaseFunctions?.addNewUser(email!!, mUsername, "")

                Toast.makeText(context, "Rejestracja udana, email weryfikujący został wysłany", Toast.LENGTH_LONG).show()
                mAuth?.signOut()

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: ")
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase?.reference

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                Log.d(TAG, "onAuthStateChanged: user signed_in" + user.uid)

                myRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        checkIfUsernameExists(username!!)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

                finish()

            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthStateListener!!)

    }

    public override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth?.removeAuthStateListener(mAuthStateListener!!)
        }
    }

}
