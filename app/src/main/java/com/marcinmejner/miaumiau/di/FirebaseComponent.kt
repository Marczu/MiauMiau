package com.marcinmejner.miaumiau.di

import com.marcinmejner.miaumiau.chat.MainChatActivity
import com.marcinmejner.miaumiau.profile.AccountSettingsActivity
import dagger.Component


@FirebaseScope
@Component(modules = arrayOf(
        FirebaseModule::class,
        UniversalImageLoaderModule::class)
)
interface FirebaseComponent {

    fun inject(mainChatActivity: MainChatActivity)
    fun inject(accountSettingsActivity: AccountSettingsActivity)
}