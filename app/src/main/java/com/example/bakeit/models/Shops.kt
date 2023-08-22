package com.example.bakeit.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString

data class Shops(
    var _id: String = "",
    var shopname: String = "",
    var shopimg: String = "",
    var shopaddr: String = "",
    var shopoutlet: String = "",
    var shopavgrating: Float = 0.0F,
    var shopcategories: ArrayList<String> = arrayListOf(),
    var veg: Boolean = true

): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.readByte() != 0.toByte()



    )

    override fun describeContents() = 0

    override fun writeToParcel(p0: Parcel, p1: Int) = with(p0) {
        writeString(_id)
        writeString(shopname)
        writeString(shopimg)
        writeString(shopaddr)
        writeString(shopoutlet)
        writeFloat(shopavgrating)
        writeStringList(shopcategories)
        writeByte(if (veg) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Shops> {
        override fun createFromParcel(parcel: Parcel): Shops {
            return Shops(parcel)
        }

        override fun newArray(size: Int): Array<Shops?> {
            return arrayOfNulls(size)
        }
    }

}
