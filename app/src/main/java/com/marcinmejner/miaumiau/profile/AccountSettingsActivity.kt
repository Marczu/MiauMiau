package com.marcinmejner.miaumiau.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.marcinmejner.miaumiau.R
import kotlinx.android.synthetic.main.snippet_top_edit_profilebar.*

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        finishActivity()
    }

    fun finishActivity(){
        back_arrow.setOnClickListener {
            finish()
        }
    }
}
