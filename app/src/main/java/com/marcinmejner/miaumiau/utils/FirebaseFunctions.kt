package com.marcinmejner.miaumiau.utils

import android.content.Context
import android.graphics.Bitmap
import android.support.constraint.Constraints.TAG
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.models.UserAccount
import com.marcinmejner.miaumiau.profile.AccountSettingsActivity

class FirebaseFunctions(val context: Context) {
    private val TAG = "FirebaseFunctions"

    //Firebase Auth
    private val mAuth: FirebaseAuth
    val mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private val mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference
    private val mStorageReference: StorageReference
    var userID: String? = null

    init {
        myRef = mFirebaseDatabase.reference
        mStorageReference = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            userID = mAuth.currentUser!!.uid
        }

    }

    /**
     * Register new email and password to firebase Auth
     *
     * @param email
     * @param password
     * @param username
     */
    fun registerNewEmail(email: String, password: String, username: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        //sending verification email
                        sendVerificationEmail()

                        // Sign in success, update UI with the signed-in user's information
                        userID = mAuth.currentUser!!.uid
                        Log.d(TAG, "createUserWithEmail:success: userID = $userID")


                    } else {
                        Toast.makeText(context, "Rejestracja nieudana", Toast.LENGTH_SHORT).show()
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    }
                })
    }

    /*Verification by email*/
    private fun sendVerificationEmail() {

        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

            } else {
                Toast.makeText(context, "Nie udało sie wysłać emaila weryfikującego", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /*Adding new User to database*/
    fun addNewUser(email: String, username: String, profile_photo: String) {
        val user = UserAccount(userID!!, username, profile_photo, email)

        myRef.child(context.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user)
    }

    /*Getting data from Users node for currently logger user */
    fun getUserAccount(dataSnapshot: DataSnapshot): UserAccount {
        Log.d(TAG, "getUserAccount: Getting data from user node")

        val userAccount = UserAccount()

        for (ds in dataSnapshot.children) {


            /*user node*/
            if (ds.key == context.getString(R.string.dbname_user_account)) {
                Log.d(TAG, "getUserAccount: $ds")
                try {
                    userAccount.username =
                            ds.child(userID)
                                    .getValue<UserAccount>(UserAccount::class.java)!!
                                    .username

                    userAccount.user_email =
                            ds.child(userID)
                                    .getValue<UserAccount>(UserAccount::class.java)!!
                                    .user_email

                    userAccount.profile_photo =
                            ds.child(userID)
                                    .getValue<UserAccount>(UserAccount::class.java)!!
                                    .profile_photo

                    userAccount.user_id =
                            ds.child(userID)
                                    .getValue<UserAccount>(UserAccount::class.java)!!
                                    .user_id


                    Log.d(TAG, "getUserAccountSettings: Otrzymane informacje z user_account_settings: " + userAccount.toString())
                } catch (e: NullPointerException) {
                    Log.e(TAG, "NullPointerException: ${e.message} ")
                }

            }


        }
        return userAccount
    }

    fun uploadNewPhoto(photoType: String, caption: String, imageCount: Int, imgUrl: String,
                       bm: Bitmap?) {


        Log.d(TAG, "uploadNewPhoto: uploaduje nowe zdjecie profilowe")


        val user_id = FirebaseAuth.getInstance().currentUser!!.uid

        val storageReference = mStorageReference
                .child(FilePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo")

        //Konwertujemy image URL na bitmap
       var bit = bm
            bit = ImageManager.getBitmap(imgUrl)


        val bytes = ImageManager.getBytesFromBitmap(bm, 100)

        var uploadTask: UploadTask? = null
        uploadTask = storageReference.putBytes(bytes)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            val firebaseUrl = taskSnapshot.downloadUrl

            Toast.makeText(mContex, "Photo upload success", Toast.LENGTH_SHORT).show()

            //insert into user_account_settings node
            setProfilePhoto(firebaseUrl!!.toString())

            (mContex as AccountSettingsActivity).setViewPager(
                    (mContex as AccountSettingsActivity).pagerAdapter
                            .getFragmentNumber(mContex.getString(R.string.edit_profile_fragment))
            )
        }.addOnFailureListener {
            Log.d(TAG, "onFailure: Photo upload failed")
            Toast.makeText(mContex, "Photo upload failed", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toDouble()

            if (progress - 15 > mPhotoUploadProgress) {
                Toast.makeText(mContex, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show()
                mPhotoUploadProgress = progress
            }
            Log.d(TAG, "onProgress: upload progress: $progress %")
        }

    }

}