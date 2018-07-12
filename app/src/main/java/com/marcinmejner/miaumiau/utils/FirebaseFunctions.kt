package com.marcinmejner.miaumiau.utils

import android.content.Context
import android.support.constraint.Constraints.TAG
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.models.UserAccount

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

//                    settings.setUsername(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getUsername()
//                    )
//                    settings.setWebsite(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getWebsite()
//                    )
//                    settings.setDescription(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getDescription()
//                    )
//                    settings.setProfile_photo(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getProfile_photo()
//                    )
//                    settings.setPosts(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getPosts()
//                    )
//                    settings.setFollowing(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getFollowing()
//                    )
//                    settings.setFollowers(
//                            ds.child(userID)
//                                    .getValue<UserAccountSettings>(UserAccountSettings::class.java)!!
//                                    .getFollowers()
//                    )

                    Log.d(TAG, "getUserAccountSettings: Otrzymane informacje z user_account_settings: " + settings.toString())
                } catch (e: NullPointerException) {
                    Log.e(TAG, "NullPointerException: ${e.message} ")
                }

            }
            /*users node*/
            if (ds.key == mContex.getString(R.string.dbname_users)) {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot: $ds")
                user.setUsername(
                        ds.child(userID)
                                .getValue<User>(User::class.java)!!
                                .getUsername()
                )
                user.setEmail(
                        ds.child(userID)
                                .getValue<User>(User::class.java)!!
                                .getEmail()
                )
                user.setPhone_number(
                        ds.child(userID)
                                .getValue<User>(User::class.java)!!
                                .getPhone_number()
                )
                user.setUser_id(
                        ds.child(userID)
                                .getValue<User>(User::class.java)!!
                                .getUser_id()
                )
                Log.d(TAG, "getUserAccountSettings: Otrzymane informacje z user: " + user.toString())


            }


        }
        return UserSettings(user, settings)
    }


}