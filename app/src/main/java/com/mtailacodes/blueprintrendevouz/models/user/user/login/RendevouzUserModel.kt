package com.mtailacodes.blueprintrendevouz.models.user.user.login

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

/**
 * Created by matthewtaila on 12/24/17.
 */

class RendevouzUserModel {


    constructor()

    var emailAddress = "defaulUser"
    var uuID = "defaulUser"
    var username = "defaultUser"

    // coodinates
    internal var latLng = LatLng(0.4, 0.4)

}



