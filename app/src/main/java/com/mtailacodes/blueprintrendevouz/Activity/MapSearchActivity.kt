package com.mtailacodes.blueprintrendevouz.Activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.ActivityMapSearchBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback {

    // firebase variables
    lateinit var mUser : RendevouzUserModel
    var USER_PERMISSION_FINE_LOCATION = 0

    // location variables
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    lateinit var map: MapFragment
    lateinit var mFusedLocationProvider: FusedLocationProviderClient

    lateinit var mBinding : ActivityMapSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search)
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        mUser = RendevouzUserModel()

        var mFirestore = RxUserUtil().GlobalUserCollectionReference().document(FirebaseAuth.getInstance().uid.toString())
        mFirestore.get().addOnSuccessListener {
            data : DocumentSnapshot ->
            if (data.exists()){
                mUser.emailAddress = data.getString("emailAddress")
                mUser.uuID = data.getString("uuID")
                mUser.username = data.getString("username")

                getUserSearchSettings()
            }
        }

        map = (fragmentManager.findFragmentById(R.id.map) as MapFragment)
        map.getMapAsync(this)

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun getUserSearchSettings() {
        var mFirestore = RxUserUtil().UserSettingsCollectionReference(mUser.uuID)
        mFirestore.document(mUser.uuID).get().addOnSuccessListener {
            data : DocumentSnapshot ->

            if (data.getBoolean("settingsCompleted")){
                // todo - show map
            } else {
                // todo prompt user to 
            }


        }
    }

    override fun onStart() {
        super.onStart()

        val locationPermission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)

        if (locationPermission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    USER_PERMISSION_FINE_LOCATION)
        } else {
            setupLocationListener()
            registerLocationListener()
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                0f, mLocationListener)
    }

    private fun setLocationVariable(location: Location) {
        var latLng = LatLng(location.latitude, location.longitude)
        mUser.latLng = latLng

        var mFirestore = RxUserUtil().GlobalUserCollectionReference()
        mFirestore.document(mUser.uuID).set(mUser)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        var maps = p0 // m
        mFusedLocationProvider.lastLocation
                .addOnSuccessListener(this, { location ->
                    var cameraPosition =  CameraPosition.Builder()
                            .target( LatLng(location.latitude, location.longitude))
                            .zoom(17f)
                            .bearing(90f)                // Sets the orientation of the camera to east
                            .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                            .build()
                    maps!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    setLocationVariable(location = location)
                })
    }

    private fun setupLocationListener() {
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                setLocationVariable(location)
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    override fun onResume() {
        super.onResume()
        // todo - check if " firebaseDatabase/userList/pushID/RendevouzModel/settings/settingsCompleted "
            // todo - if the user settingsCompleted is false - need to hide the map and onboard the user
                // todo - map is set unclickable
                // todo grey foreground view is shown
                // todo give message saying default search values will be used -  "would you like to change those now"

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            USER_PERMISSION_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setupLocationListener()
                    registerLocationListener()
                } else {
                    // todo prevent location based stuff
                }
                return
            }
        }
    }

}