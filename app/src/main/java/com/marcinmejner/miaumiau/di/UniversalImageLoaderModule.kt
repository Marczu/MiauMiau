package com.marcinmejner.miaumiau.di

import android.content.Context
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import dagger.Module
import dagger.Provides

@Module
class UniversalImageLoaderModule(val context: Context) {

    @FirebaseScope
    @Provides
    fun provideUniversalImageLoader(): UniversalImageLoader{
        return UniversalImageLoader(context)
    }

}