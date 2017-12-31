package com.mtailacodes.blueprintrendevouz.models.user.user.login

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser

/**
 * Created by matthewtaila on 12/24/17.
 */

class RendevouzUserModel() : Parcelable {

    internal var  emailAddress  = "defaulUser"
    internal var  UuID = "defaulUser"
    internal var pushID = "defaultUser"
    internal var username = "defaultUser"

    constructor(parcel: Parcel) : this() {
        emailAddress = parcel.readString()
        UuID = parcel.readString()
        pushID = parcel.readString()
        username = parcel.readString()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(emailAddress)
        parcel.writeString(UuID)
        parcel.writeString(pushID)
        parcel.writeString(username)
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