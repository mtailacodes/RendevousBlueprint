package com.mtailacodes.blueprintrendevouz.models.user.user.login

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by matthewtaila on 12/31/17.
 */
class UserSearchSettings() : Parcelable{

    var gender = "default user"
    var sexIntereset = "default user"
    var currentAge = 0
    var ageRangeMax  = 0
    var ageRangeMin  = 0
    var settingsCompleted = false

    constructor(parcel: Parcel) : this() {
        gender = parcel.readString()
        sexIntereset = parcel.readString()
        currentAge = parcel.readInt()
        ageRangeMax = parcel.readInt()
        ageRangeMin = parcel.readInt()
        settingsCompleted = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gender)
        parcel.writeString(sexIntereset)
        parcel.writeInt(currentAge)
        parcel.writeInt(ageRangeMax)
        parcel.writeInt(ageRangeMin)
        parcel.writeByte(if (settingsCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserSearchSettings> {
        override fun createFromParcel(parcel: Parcel): UserSearchSettings {
            return UserSearchSettings(parcel)
        }

        override fun newArray(size: Int): Array<UserSearchSettings?> {
            return arrayOfNulls(size)
        }
    }
}