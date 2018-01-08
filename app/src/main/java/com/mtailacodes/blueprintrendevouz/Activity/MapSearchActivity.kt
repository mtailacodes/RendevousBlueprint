package com.mtailacodes.blueprintrendevouz.Activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityMapSearchBinding
import com.mtailacodes.blueprintrendevouz.fragments.PromptSettingsFragment
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback, View.OnClickListener, PromptSettingsFragment.UserSearchSettingsListener{


    //Activity variables
    val searchSettings_TAG = "Search Settings Data"
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1

    //camera variables
    lateinit var photoFile: File
    lateinit var timeStamp: String
    var canShowPic = false

    // firebase variables
    var mUser = RendevouzUserModel()
    var USER_PERMISSION_FINE_LOCATION = 0

    var settingsCompleted = false
    var imageStored = false

    // location variables
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    lateinit var map: MapFragment
    lateinit var mFusedLocationProvider: FusedLocationProviderClient
    var mAnimationList : ArrayList<Animator> = ArrayList()

    lateinit var mBinding : ActivityMapSearchBinding
    var mSearchSettings =  UserSearchSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search)

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        map = (fragmentManager.findFragmentById(R.id.map) as MapFragment)
        map.getMapAsync(this)

        setOnClickListeners()
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
    }

    override fun onResume() {
        super.onResume()

        mUser = RxUserUtil().getUserModel()
        //todo - maybe a RxUtil method that returns a userSearchSetting object -
        // todo - use that to set the user.searchSettings / or dont
        // todo - user the search Model to determine what to do for the if statement below

        if (!settingsCompleted){
            getUserSearchSettings()
        } else {
            if (!imageStored) {
                handleCaptureImageCardView(1f)
            }
        }
    }

    private fun setOnClickListeners() {
        mBinding.tvSettings.setOnClickListener(this)
        mBinding.picturePreview.setOnClickListener(this)
    }

    override fun searchSettingsValid(passed: Boolean) {
        if (passed){
            hideSettingsCardview()
        }
    }

    private fun handleCaptureImageCardView(finalFloat: Float, hide: Boolean = false) {
        mBinding.cvPromptUserImage.visibility = VISIBLE
        var animatorSet = AnimationUtil.handleCaptureImageCardview(mBinding.cvPromptUserImage)
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (hide){
                    mBinding.cvPromptUserImage.visibility = GONE
                    mBinding.tvTakeAPicture.setOnClickListener(null)
                }
                mBinding.tvTakeAPicture.setOnClickListener(this@MapSearchActivity)
            }
        })
        animatorSet.start()
    }

    private fun getUserSearchSettings() {
        var uuID = FirebaseAuth.getInstance().currentUser!!.uid
        var mFirestore = RxUserUtil().UserSettingsCollectionReference(uuID)
        mFirestore.document(uuID).get().addOnSuccessListener {
            data : DocumentSnapshot ->
            if (data.exists()){
                if (data.getBoolean("settingsCompleted")){
                    mSearchSettings = data.toObject(UserSearchSettings::class.java)
                    settingsCompleted = true
                } else {
                    showSettingsCardView(0)
                }
            } else {
                Log.d(searchSettings_TAG, "document snapshot does not exis")
            }
        }
    }

    private fun showSettingsCardView(control: Int) {
        mBinding.searchSettingsPlaceholder.visibility = VISIBLE
        var animateSettingsContainer = ObjectAnimator.ofFloat(mBinding.searchSettingsPlaceholder,
                View.ALPHA, 1f)
        animateSettingsContainer.duration = 100
        animateSettingsContainer.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                val fragment = PromptSettingsFragment()
                var fragmentTransaction = supportFragmentManager.beginTransaction()
                        .replace(R.id.zzxxx, fragment)
                        .commit()
            }
        })
        animateSettingsContainer.start()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_Settings ->{
                getUserSearchSettings()
                showSettingsCardView(1)
            }
            R.id.tv_TakeAPicture->{
                launchCamera()
            }
            R.id.picturePreview->{
                if (canShowPic){
                    showProfilePicAndSettingsContainer()
                }
            }
        }
    }

    private fun showProfilePicAndSettingsContainer() {

        var displayMetrics =  DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels

        mBinding.profilePic.layoutParams.height = width
        mBinding.profilePic.layoutParams.width = width

        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.translateY(view = mBinding.clProfileSettingsContainer,
                translationYValue = width.toFloat(),
                startDelay = 175))
        mAnimationList.add(AnimationUtil.scaleY(view = mBinding.clProfileSettingsShortcut,
                heightToValue =  0f,
                startDelay = 125))
        mAnimationList.add(AnimationUtil.alpha(view = mBinding.tvSettings,
                alphaValue = 0f,
                duration = 150))
        mAnimationList.add(AnimationUtil.alpha(view = mBinding.picturePreview,
                duration = 150,
                alphaValue = 0f))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                Glide.with(this@MapSearchActivity).load(photoFile!!.path).into(mBinding.profilePic)
            }
        })
        mAnimatorSet.start()
    }

    private fun launchCamera() {

        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFileUri(generateTimeStamp())

        var fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private fun generateTimeStamp(): String {
        var stamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        timeStamp = stamp + ".jpeg"
        return timeStamp
    }

    private fun getPhotoFileUri(s: String): File {
        var imageDirectory : File
        lateinit var file: File
        if (isExternalStorageAvailable()){
            imageDirectory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MYAPP")
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()){
                Log.d("MYAPP", "failed to create directory")
            }
             file = File(imageDirectory.path + File.separator + s)
        }
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            handleCaptureImageCardView(0f, hide = true)
            mBinding.picturePreview.visibility = VISIBLE
            canShowPic = true
            Glide.with(this).load(photoFile!!.path).apply(RequestOptions.circleCropTransform()).into(mBinding.picturePreview)
        }
    }

    private fun isExternalStorageAvailable(): Boolean {
        var state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED
    }

    private fun hideSettingsCardview() {
        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.scaleY(view = mBinding.cvSearchSettingsContainer,
                heightToValue =  0f, duration = 200))
        mAnimationList.add(AnimationUtil.scaleX(view = mBinding.cvSearchSettingsContainer,
                heightToValue =  0f, duration = 200))
        mAnimationList.add(AnimationUtil.alpha(view = mBinding.cvSearchSettingsContainer,
                alphaValue =  0f, duration = 200))
        mAnimationList.add(AnimationUtil.alpha(view = mBinding.searchSettingsPlaceholder,
                alphaValue =  0f, duration = 400))

        var promptSettingsCardViewAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        promptSettingsCardViewAnimatorSet.interpolator = AccelerateInterpolator()
        promptSettingsCardViewAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mBinding.searchSettingsPlaceholder.visibility = GONE
                mBinding.cvSearchSettingsContainer.visibility = GONE
                if (!imageStored) {
                    handleCaptureImageCardView(1f)
                }
            }
        })
        promptSettingsCardViewAnimatorSet.start()
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
        maps!!.uiSettings.setAllGesturesEnabled(false)
        mFusedLocationProvider.lastLocation.addOnSuccessListener(this, { location ->
            var cameraPosition =  CameraPosition.Builder()
                    .target( LatLng(location.latitude, location.longitude))
                    .zoom(17f)
                    .bearing(90f)
                    .tilt(0f)
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