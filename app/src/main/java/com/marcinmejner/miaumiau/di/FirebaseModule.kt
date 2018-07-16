package com.marcinmejner.miaumiau.di

import android.content.Context
import com.marcinmejner.miaumiau.utils.FirebaseFunctions
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule(val context: Context) {

    @Provides
    @FirebaseScope
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions(context)
    }


}