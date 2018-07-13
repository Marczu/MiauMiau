package com.marcinmejner.miaumiau.base

import android.app.Application
import com.marcinmejner.miaumiau.di.DaggerFirebaseComponent
import com.marcinmejner.miaumiau.di.FirebaseComponent
import com.marcinmejner.miaumiau.di.FirebaseModule

class BaseActivity: Application() {

    companion object {
        lateinit var component: FirebaseComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerFirebaseComponent.builder()
                .firebaseModule(FirebaseModule(this))
                .build()
    }

}