package com.marcinmejner.miaumiau.profile

import android.app.PendingIntent.getActivity
import android.content.Context
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
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.snippet_top_edit_profilebar.*
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.marcinmejner.miaumiau.utils.Permissions
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.R.attr.data




class AccountSettingsActivity : AppCompatActivity() {
    private val TAG = "AccountSettingsActivity"

    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null
    @Inject
    lateinit var firebaseFunctions: FirebaseFunctions
    private var userID: String? = null

    private val VERIFY_PERMISSIONS_REQUEST = 1
    private val PICK_PHOTO_CODE = 66

    //vars
    lateinit var fragmentManager: FragmentManager
    lateinit var userAccount: UserAccount
    @Inject
    lateinit var uImageLoader: UniversalImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        initFirebaseFunctions()
        setupFirebaseAuth()
        finishActivity()
        displayLogout()

        fragmentManager = supportFragmentManager
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

            val transaction = fragmentManager.beginTransaction()
                    .add(R.id.container, fragment, getString(R.string.logout_fragment))
                    .addToBackStack(getString(R.string.logout_fragment))
                    .commit()
        }
    }

    private fun setProfileWidgets(userAccount: UserAccount) {
        Log.d(TAG, "setProfileWidgets: setting profile data from : $userAccount")

        this.userAccount = userAccount
        ImageLoader.getInstance().init(uImageLoader.getConfig())

        uImageLoader.setImage(userAccount.profile_photo, profile_image, "")

        username_et.setText(userAccount.username)
        email_et.setText(userAccount.user_email)
        profile_image

        /*Change profile photo*/
        relLayout2_profile_photo.setOnClickListener {
            Log.d(TAG, "changeProfilePhoto: attempt to veryfy permissions, is already granted, then change profile photo")
            if (checkPermissionsArray(Permissions.PERMISSIONS)) {
                /*Working on profile photo*/
                onPickPhoto()

            } else {
                veryfiPermissions(Permissions.PERMISSIONS)
            }
        }
    }

    /*Taking photo from storage*/
    private fun onPickPhoto() {
        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (intent.resolveActivity(packageManager) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE)
            Log.d(TAG, "onPickPhoto: taking photo from storage")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_PHOTO_CODE) {
            if (data != null) {

                val photoUri = data.data
                // Do something with the photo based on Uri
                val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
                Log.d(TAG, "onActivityResult: photo is: $photoUri , and $selectedImage")

                firebaseFunctions.uploadNewPhoto(getString(R.string.profile_photo),
                        null, 0, null, selectedImage)
            }
        }
    }


    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }
    }


    /*Verify all permissions*/
    fun veryfiPermissions(permissions: Array<String>) {
        Log.d(TAG, "veryfiPermissions: veryfing permissions")

        ActivityCompat.requestPermissions(
                this@AccountSettingsActivity,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        )
    }

    /*Check array permissions*/
    fun checkPermissionsArray(permissions: Array<String>): Boolean {

        for (i in permissions.indices) {
            val check = permissions[i]

            if (!checkPermisions(check)) {
                return false
            }
        }
        return true
    }

    /*check single permission*/
    fun checkPermisions(permission: String): Boolean {
        Log.d(TAG, "checkPermisions: sprawdzamy pozwolenie: $permission")

        val permissionRequest = ActivityCompat.checkSelfPermission(this@AccountSettingsActivity, permission)

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermisions: Permission not granted for: $permission")
            return false
        } else {
            Log.d(TAG, "checkPermisions: Permission granted for: $permission")
            return true
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
