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
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.mtailacodes.blueprintrendevouz.Manifest
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.R.id.map
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.databinding.ActivityMapSearchBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback {


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
                })
    }


    // firebase variables
    lateinit var mUser : RendevouzUserModel
    private var ACTIVE_USERS = "Active Users"
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
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        map = (fragmentManager.findFragmentById(R.id.map) as MapFragment)
        map.getMapAsync(this)

        mUser = intent.getParcelableExtra(Constants.RENDEVOUZ_USER_MODEL_BUNDLE)
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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


        // todo - check user permissions - DONE
            // todo - get location permissions value?
            // todo - if user permission value is false, ask for permission and upadte locationPermitted variable
                // todo - handle startActivityForResult
            // todo - if user permission value is true, update locationPermittedVariable
                // todo - get current location and set it to:
                // todo - "databaseReference/ActiveUsers/pushID/UuID/currentLocation

    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                0f, mLocationListener)
    }

    private fun setLocationVariable(location: Location) {
        val mFirebaseBaseDatabase = FirebaseDatabase.getInstance()
        val mFirebaseReference = mFirebaseBaseDatabase.reference.child(ACTIVE_USERS)
                .child(mUser.pushID)
                .child(mUser.UuID)
                .child("Location")
        mFirebaseReference.setValue(location)
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