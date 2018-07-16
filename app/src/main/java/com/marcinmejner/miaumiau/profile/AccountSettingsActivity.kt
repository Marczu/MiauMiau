package com.marcinmejner.miaumiau.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.base.BaseActivity
import com.marcinmejner.miaumiau.models.UserAccount
import com.marcinmejner.miaumiau.utils.FirebaseFunctions
import kotlinx.android.synthetic.main.snippet_top_edit_profilebar.*

class AccountSettingsActivity : AppCompatActivity() {
    private val TAG = "AccountSettingsActivity"

    //Firebase Auth
    var mAuth: FirebaseAuth? = null
    var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    var mFirebaseDatabase: FirebaseDatabase? = null
    var myRef: DatabaseReference? = null
    lateinit var firebaseFunctions: FirebaseFunctions
    var userID: String? = null

    //vars
    lateinit var fragmentManager: FragmentManager
    lateinit var userAccount: UserAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        initFirebaseFunctions()
        setupFirebaseAuth()
        finishActivity()
        displayLogout()
        changeProfilePhoto()
    }

    private fun initFirebaseFunctions() {
        BaseActivity.component.inject(this)
    }


    private fun finishActivity() {
        back_arrow.setOnClickListener {
            finish()
        }
    }

    private fun displayLogout() {
        logout_editprofile.setOnClickListener {
            val fragment = SignOutFragment()
            fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
                    .add(R.id.container, fragment, getString(R.string.logout_fragment))
                    .addToBackStack(getString(R.string.logout_fragment))
                    .commit()
        }
    }

    private fun setProfileWidgets(userAccount: UserAccount) {
        Log.d(TAG, "setProfileWidgets: setting profile data from : $userAccount")

        this.userAccount = userAccount


    }

    private fun changeProfilePhoto() {

    }

    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }

    }

    /*
        ------------------------------FIREBASE -----------------------------------------
    */

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase")

        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase?.getReference()
        userID = mAuth?.currentUser!!.uid

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                Log.d(TAG, "user signed_in, with userUID:  " + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }
        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*getting user data from database*/
                setProfileWidgets(firebaseFunctions.getUserAccount(dataSnapshot))


            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
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
