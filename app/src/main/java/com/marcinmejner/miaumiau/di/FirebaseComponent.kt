package com.marcinmejner.miaumiau.di

import com.marcinmejner.miaumiau.chat.MainChatActivity
import dagger.Component


@FirebaseScope
@Component(modules = arrayOf(
        FirebaseModule::class)
)
interface FirebaseComponent {

    fun inject(mainChatActivity: MainChatActivity)
}