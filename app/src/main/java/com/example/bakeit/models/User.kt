package com.example.bakeit.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val _id: String = " ",
    var email: String = " ",
    var username: String = " ",
    var phone: String = " ",
    val addresses: ArrayList<UserAddress> = arrayListOf(),
): Parcelable {

    constructor(parcel: Parcel):this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        ArrayList<UserAddress>().apply {
            parcel.readList(this, UserAddress::class.java.classLoader)
        }
    )

    override fun describeContents() = 0


    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(_id)
        writeString(email)
        writeString(username)
        writeString(phone)
        dest.writeList(addresses)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
