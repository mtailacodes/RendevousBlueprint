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
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback, View.OnClickListener{

    //Activity variables
    val activityName_TAG = "MapSearchActivity: "
    val searchSettings_TAG = "Search Settings Data"
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1


    //camera variables
     lateinit var photoFile: File
    lateinit var timeStamp: String
    var canShowPic = false;

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

    private fun setOnClickListeners() {
        mBinding.tvSettings.setOnClickListener(this)
        mBinding.picturePreview.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()

        mUser = RxUserUtil().getUserModel()

        if (!settingsCompleted){
            getUserSearchSettings()
        }

        if (!imageStored){
            handleCaptureImageCardView(1f)
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
        enableSearchSettingsCardView()

        mBinding.searchSettingsPlaceholder.visibility = VISIBLE
        var animateSettingsContainer = ObjectAnimator.ofFloat(mBinding.searchSettingsPlaceholder,
                View.ALPHA, 1f)
        animateSettingsContainer.duration = 100
        animateSettingsContainer.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                if (control == 1){
                    if (mSearchSettings.gender == "Male"){
                        resetView(mBinding.tvUserGenderFemale)
                        applySelectionHighlight(mBinding.tvUserGenderMale)
                    } else if (mSearchSettings.gender == "Female"){
                        resetView(mBinding.tvUserGenderMale)
                        applySelectionHighlight(mBinding.tvUserGenderFemale)
                    }

                    if (mSearchSettings.sexIntereset == "Male"){
                        resetView(mBinding.tvUserInterestFemale)
                        applySelectionHighlight(mBinding.tvUserInterestMale)
                    } else if (mSearchSettings.sexIntereset == "Female"){
                        resetView(mBinding.tvUserInterestMale)
                        applySelectionHighlight(mBinding.tvUserInterestFemale)
                    }
                    mBinding.etUserAgeInput.setText(mSearchSettings.currentAge.toString())
                }
            }
        })
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

        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // creata a camera intent

        photoFile = getPhotoFileUri(generateTimeStamp())

        var fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
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

    // todo refactor this
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