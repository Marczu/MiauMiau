package com.marcinmejner.miaumiau.models

data class ChatMessage(
        var username: String = "",
        var profile_photo: String = "",
        var dateCreated: String = "",
        var chatMessage: String = ""
)
