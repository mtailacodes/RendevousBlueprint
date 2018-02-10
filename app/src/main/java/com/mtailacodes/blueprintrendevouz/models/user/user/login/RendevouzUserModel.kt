package com.mtailacodes.blueprintrendevouz.models.user.user.login

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

/**
 * Created by matthewtaila on 12/24/17.
 */

class RendevouzUserModel() : Parcelable {

    var emailAddress = "defaulUser"
    var uuID = "defaulUser"
    var username = "defaultUser"
    var birthDay = 0
    var birthMonth = 0
    var birthYear = 0
    var gender = "defaultUser"
    var stub = 0
    var requiresOnboarding = true
    internal var latLng = LatLng(0.4, 0.4)

    constructor(parcel: Parcel) : this() {
        emailAddress = parcel.readString()
        uuID = parcel.readString()
        username = parcel.readString()
        birthDay = parcel.readInt()
        birthMonth = parcel.readInt()
        birthYear = parcel.readInt()
        gender = parcel.readString()
        stub = parcel.readInt()
        requiresOnboarding = parcel.readByte() != 0.toByte()
        latLng = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(emailAddress)
        parcel.writeString(uuID)
        parcel.writeString(username)
        parcel.writeInt(birthDay)
        parcel.writeInt(birthMonth)
        parcel.writeInt(birthYear)
        parcel.writeString(gender)
        parcel.writeInt(stub)
        parcel.writeByte(if (requiresOnboarding) 1 else 0)
        parcel.writeParcelable(latLng, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RendevouzUserModel> {
        override fun createFromParcel(parcel: Parcel): RendevouzUserModel {
            return RendevouzUserModel(parcel)
        }

        override fun newArray(size: Int): Array<RendevouzUserModel?> {
            return arrayOfNulls(size)
        }
    }


}



