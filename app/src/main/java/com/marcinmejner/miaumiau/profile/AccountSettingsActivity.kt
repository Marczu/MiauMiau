package com.marcinmejner.miaumiau.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.marcinmejner.miaumiau.R
import kotlinx.android.synthetic.main.snippet_top_edit_profilebar.*

class AccountSettingsActivity : AppCompatActivity() {
    private val TAG = "AccountSettingsActivity"

    //vars
    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        finishActivity()
        displayLogout()
        changeProfilePhoto()
    }

    private fun finishActivity(){
        back_arrow.setOnClickListener {
            finish()
        }
    }

    private fun displayLogout(){
        logout_editprofile.setOnClickListener {
            val fragment = SignOutFragment()
            fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
                    .add(R.id.container, fragment, getString(R.string.logout_fragment))
                    .addToBackStack(getString(R.string.logout_fragment))
                    .commit()
        }
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
}
