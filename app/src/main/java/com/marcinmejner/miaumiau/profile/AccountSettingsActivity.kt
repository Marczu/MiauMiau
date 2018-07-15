package com.marcinmejner.miaumiau.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.marcinmejner.miaumiau.R
import kotlinx.android.synthetic.main.snippet_top_edit_profilebar.*

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        finishActivity()
        displayLogout()
    }

    private fun finishActivity(){
        back_arrow.setOnClickListener {
            finish()
        }
    }

    private fun displayLogout(){
        logout_editprofile.setOnClickListener {
            val fragment = SignOutFragment()
            val fm = supportFragmentManager
            val transaction = fm.beginTransaction()
                    .add(R.id.container, fragment, getString(R.string.logout_fragment))
                    .commit()
        }
    }
}
