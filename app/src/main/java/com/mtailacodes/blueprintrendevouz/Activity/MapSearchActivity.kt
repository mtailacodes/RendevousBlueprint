package com.mtailacodes.blueprintrendevouz.Activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.ActivityMapSearchBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback, View.OnClickListener{

    //Activity variables
    val activityName_TAG = "MapSearchActivity: "
    val searchSettings_TAG = "Search Settings Data"


    // firebase variables
    var mUser = RendevouzUserModel()
    var USER_PERMISSION_FINE_LOCATION = 0

    var settingsCompleted = false

    // location variables
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    lateinit var map: MapFragment
    lateinit var mFusedLocationProvider: FusedLocationProviderClient

    lateinit var mBinding : ActivityMapSearchBinding
    var mSearchSettings =  UserSearchSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search)

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        map = (fragmentManager.findFragmentById(R.id.map) as MapFragment)
        map.getMapAsync(this)

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()

        mUser = RxUserUtil().getUserModel()

        if (!settingsCompleted){
            getUserSearchSettings()
        }
    }

    private fun getUserSearchSettings() {
        var uuID = FirebaseAuth.getInstance().currentUser!!.uid
        var mFirestore = RxUserUtil().UserSettingsCollectionReference(uuID)
        mFirestore.document(uuID).get().addOnSuccessListener {
            data : DocumentSnapshot ->
            if (data.exists()){
                if (data.getBoolean("settingsCompleted")){
                    settingsCompleted = true
                } else {
                    showSettingsCardView()
                }
            } else {
                Log.d(searchSettings_TAG, "document snapshot does not exis")
            }
        }
    }

    private fun showSettingsCardView() {
        enableSearchSettingsCardView()

        mBinding.searchSettingsPlaceholder.visibility = VISIBLE
        var animateSettingsContainer = ObjectAnimator.ofFloat(mBinding.searchSettingsPlaceholder,
                View.ALPHA, 1f)
        animateSettingsContainer.duration = 100
        animateSettingsContainer.start()
    }

    private fun enableSearchSettingsCardView() {
        mBinding.tvUserGenderMale.setOnClickListener(this)
        mBinding.tvUserGenderFemale.setOnClickListener(this)
        mBinding.tvUserInterestMale.setOnClickListener(this)
        mBinding.tvUserInterestFemale.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.searchSettingsPlaceholder.setOnClickListener ({  _->  })

        mBinding.rbAgeRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            mBinding.tvAgeRangeMin.text = minValue.toString()
            mBinding.tvAgeRangeMax.text = maxValue.toString()
            // todo  need to change drawable for the rings
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_UserGenderMale -> {
                resetView(mBinding.tvUserGenderFemale)
                applySelectionHighlight(mBinding.tvUserGenderMale)
                mSearchSettings.gender = "Male"
                return
            }
            R.id.tv_UserGenderFemale -> {
                resetView(mBinding.tvUserGenderMale)
                applySelectionHighlight(mBinding.tvUserGenderFemale)
                mSearchSettings.gender = "Female"
                return
            }
            R.id.tv_UserInterestMale -> {
                resetView(mBinding.tvUserInterestFemale)
                applySelectionHighlight(mBinding.tvUserInterestMale)
                mSearchSettings.sexIntereset= "Male"
                return
            }
            R.id.tv_UserInterestFemale -> {
                resetView(mBinding.tvUserInterestMale)
                applySelectionHighlight(mBinding.tvUserInterestFemale)
                mSearchSettings.sexIntereset= "Female"
                return
            }
            R.id.tv_Save-> {
                checkSettingsInput(mSearchSettings)
                return
            }
        }
    }

    private fun checkSettingsInput(searchSettings: UserSearchSettings) {
        var pass = false

        // todo maybe add animations to show which fields are set correctly - not necesary right now but when the UI is done we can think about that

        pass = searchSettings.gender == "Male" || searchSettings.gender == "Female"

        pass = searchSettings.sexIntereset ==  "Male" || searchSettings.sexIntereset == "Female"

        if (mBinding.etUserAgeInput.toString().isEmpty()){
            pass = false
        }  else {
            pass = Integer.parseInt(mBinding.etUserAgeInput.text.toString()) >= 18
        }

        mSearchSettings.currentAge = Integer.parseInt(mBinding.etUserAgeInput.text.toString())
        mSearchSettings.ageRangeMin = Integer.parseInt(mBinding.tvAgeRangeMin.text.toString())
        mSearchSettings.ageRangeMax = Integer.parseInt(mBinding.tvAgeRangeMax.text.toString())

        mSearchSettings.settingsCompleted = pass

        var mFirestore = RxUserUtil().UserSettingsCollectionReference(mUser.uuID)
        mFirestore .document(mUser.uuID).set(mSearchSettings)
            .addOnSuccessListener{ _ ->
                hideSettingsCardview()
            }.addOnFailureListener { e ->
                Log.d("FirestoreFailure", e.localizedMessage)
        }
        // todo - add on success listener - onSuccess - hide cardview and show map
    }

    private fun hideSettingsCardview() {

        var scaleY = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.SCALE_Y, 0f)
        var scaleX = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.SCALE_X, 0f)
        var alphaCardview = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.ALPHA, 0f)

        var cardviewAnimatorSet = AnimatorSet()
        cardviewAnimatorSet.playTogether(scaleX, scaleY, alphaCardview)
        cardviewAnimatorSet.interpolator = AccelerateInterpolator()
        cardviewAnimatorSet.duration = 200

        var alphaContainer = ObjectAnimator.ofFloat(mBinding.searchSettingsPlaceholder, View.ALPHA, 0f)
        alphaContainer.interpolator = AccelerateInterpolator()
        alphaContainer.duration = 400

        var finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.playTogether(alphaContainer, cardviewAnimatorSet)
        finalAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                var scaleY = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.SCALE_Y, 1f)
                var scaleX = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.SCALE_X, 1f)
                var alphaCardview = ObjectAnimator.ofFloat(mBinding.cvSearchSettingsContainer, View.ALPHA, 1f)

                var cardviewAnimatorSet = AnimatorSet()
                cardviewAnimatorSet.playTogether(scaleX, scaleY, alphaCardview)
                cardviewAnimatorSet.duration = 0
                cardviewAnimatorSet.start()

                mBinding.searchSettingsPlaceholder.visibility = GONE

            }
        })
        finalAnimatorSet.start()
    }

    private fun applySelectionHighlight(view: TextView) {
        view.setTextColor(ContextCompat.getColor(this, R.color.green))
    }

    private fun resetView(view: TextView) {
        view.setTextColor(ContextCompat.getColor(this, R.color.black100))
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

                        if (mUser.uuID == FirebaseAuth.getInstance().currentUser!!.uid){
                            setLocationVariable(location = location)
                        }
                    })
    }

    private fun setupLocationListener() {
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (mUser.uuID == FirebaseAuth.getInstance().currentUser!!.uid){
                    setLocationVariable(location = location)
                }
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
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