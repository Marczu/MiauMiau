package com.marcinmejner.miaumiau.chat

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.adapters.MainChatRecyclerAdapter
import com.marcinmejner.miaumiau.base.BaseActivity
import com.marcinmejner.miaumiau.login.LoginActivity
import com.marcinmejner.miaumiau.models.ChatMessage
import com.marcinmejner.miaumiau.models.UserAccount
import com.marcinmejner.miaumiau.profile.AccountSettingsActivity
import com.marcinmejner.miaumiau.utils.FirebaseFunctions
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.nav_header_main_chat.view.*
import kotlinx.android.synthetic.main.snippet_top_profilebar.*
import javax.inject.Inject


class MainChatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "MainChatActivity"



    //Firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    @Inject
    lateinit var mFirebaseFunctions: FirebaseFunctions
    @Inject
    lateinit var uImageLoader: UniversalImageLoader

    //vars
    private val contex = this@MainChatActivity
    lateinit var chatMessages: ArrayList<ChatMessage>
    lateinit var chatAdapter: MainChatRecyclerAdapter

    //widgets
    lateinit var recyclerView: RecyclerView

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
//        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView?.hasFixedSize()
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val divider = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(divider)



//       val message = ChatMessage()
//        message.profile_photo = account?.profile_photo
//        message.dateCreated = "11:30"
//        message.chatMessage = "testowa wiadomość"
//        message.username = account?.username
//
//        chatMessages.add(message)
        chatAdapter = MainChatRecyclerAdapter(chatMessages, this)

    }

    private fun initImageLoader() {
        ImageLoader.getInstance().init(uImageLoader.getConfig())
    }

    private fun initDagger() {
        BaseActivity.component.inject(this)
    }


    private fun setProfileWidgets(userAccount: UserAccount?) {
        Log.d(TAG, "setProfileWidgets: ustawianie wigdetów z uzyciem bazy z firebase" + userAccount.toString())
        Log.d(TAG, "setProfileWidgets: ustawianie wigdetów z uzyciem bazy z firebase" + userAccount?.username)

        val account = userAccount
        username.text = account?.username

        uImageLoader.setImage(userAccount?.profile_photo, toolbar_profile_photo, "")

        /*Set nav Drawer profile*/
        val navigationView = findViewById<NavigationView>(R.id.nav_view1)
        val hView = navigationView.getHeaderView(0)
        hView.username_nav_drawer.text = account?.username
        hView.email_nav_drawer.text = account?.user_email
        uImageLoader.setImage(userAccount?.profile_photo, hView.profile_photo_nav_drawer, "")



        var message = ChatMessage()
        var message2 = ChatMessage()
        var message3 = ChatMessage()
        message.profile_photo = account?.profile_photo!!
        message.dateCreated = "11:30"
        message.chatMessage = "testowa wiadomość, jestem kaczusią i kocham mojego kotka miauuuuuuuuuuuuuuuu"
        message.username = account.username

        message2.profile_photo = account?.profile_photo!!
        message2.dateCreated = "12:48"
        message2.chatMessage = "kolejna wiadomość"
        message2.username = account.username

        message3.profile_photo = account?.profile_photo!!
        message3.dateCreated = "12:48"
        message3.chatMessage = "kolejna wiadomość"
        message3.username = account.username



        chatMessages = ArrayList()
        chatMessages.add(message)
        chatMessages.add(message2)
        chatMessages.add(message3)


        initRecyclerView()

        recyclerView.adapter = chatAdapter
//        chatAdapter.notifyDataSetChanged()


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
