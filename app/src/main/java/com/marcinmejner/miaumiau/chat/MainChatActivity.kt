package com.marcinmejner.miaumiau.chat

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.base.BaseActivity
import com.marcinmejner.miaumiau.login.LoginActivity
import com.marcinmejner.miaumiau.models.UserAccount
import com.marcinmejner.miaumiau.profile.AccountSettingsActivity
import com.marcinmejner.miaumiau.utils.FirebaseFunctions
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_chat.*

import kotlinx.android.synthetic.main.snippet_top_profilebar.*
import javax.inject.Inject

class MainChatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "MainChatActivity"

    private val contex = this@MainChatActivity

    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    @Inject
    lateinit var mFirebaseFunctions: FirebaseFunctions
    @Inject
    lateinit var uImageLoader: UniversalImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()

        navigateToEditProfile()

    }



    private fun init() {
        initDagger()
        initImageLoader()
        setupFirebaseAuth()
        initNavDrawer()

    }

    private fun initImageLoader() {
//        ImageLoader.getInstance().init(uImageLoader.getConfig())
    }

    private fun initDagger() {
        BaseActivity.component.inject(this)
    }

    private fun setProfileWidgets(userAccount: UserAccount?) {
        Log.d(TAG, "setProfileWidgets: ustawianie wigdetów z uzyciem bazy z firebase" + userAccount.toString())
        Log.d(TAG, "setProfileWidgets: ustawianie wigdetów z uzyciem bazy z firebase" + userAccount?.username)

        val account = userAccount
        username.text = account?.username

    }

    private fun initNavDrawer() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout1, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout1.addDrawerListener(toggle)
        toggle.syncState()

        nav_view1.setNavigationItemSelectedListener(this)

        burger_menu.setOnClickListener {
            drawer_layout1.openDrawer(Gravity.START)
        }
    }

    private fun navigateToEditProfile() {
        edit_profile.setOnClickListener {
            Intent(this@MainChatActivity, AccountSettingsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout1.isDrawerOpen(GravityCompat.START)) {
            drawer_layout1.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout1.closeDrawer(GravityCompat.START)
        return true
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
        //        mAuth.signOut();
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase?.reference


        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            checkCurrentUser(user)

            if (user != null) {
                Log.d(TAG, "user signed_in:  " + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged: user signed_out")
            }
        }

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*Getting user data from database*/
                setProfileWidgets(mFirebaseFunctions?.getUserAccount(dataSnapshot))

                /*Pozyskiwanie obrazków usera z bazy danych*/

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
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
