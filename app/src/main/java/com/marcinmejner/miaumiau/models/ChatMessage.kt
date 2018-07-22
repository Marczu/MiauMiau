package com.marcinmejner.miaumiau.models

import android.os.Parcel
import android.os.Parcelable

data class ChatMessage(
        var username: String = "",
        var user_id: String = "",
        var profile_photo: String = "",
        var date_created: String = "",
        var chat_message: String = ""
//        var message_id: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(user_id)
        parcel.writeString(profile_photo)
        parcel.writeString(date_created)
        parcel.writeString(chat_message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }
}

