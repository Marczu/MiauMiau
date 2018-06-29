package com.marcinmejner.miaumiau.models

import android.os.Parcel
import android.os.Parcelable

data class UserAccount(
        var username: String = "",
        var profile_photo: String = "",
        var user_id: String = "",
        var user_email: String = ""

        ) : Parcelable {



    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(profile_photo)
        parcel.writeString(user_id)
        parcel.writeString(user_email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserAccount> {
        override fun createFromParcel(parcel: Parcel): UserAccount {
            return UserAccount(parcel)
        }

        override fun newArray(size: Int): Array<UserAccount?> {
            return arrayOfNulls(size)
        }
    }
}