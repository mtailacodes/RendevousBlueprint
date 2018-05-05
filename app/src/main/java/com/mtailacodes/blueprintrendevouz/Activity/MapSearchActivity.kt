package com.mtailacodes.blueprintrendevouz.Activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.Util.MapUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityMapSearchBinding
import com.mtailacodes.blueprintrendevouz.fragments.PromptSettingsFragment
import com.mtailacodes.blueprintrendevouz.fragments.TopContainerFragment
import com.mtailacodes.blueprintrendevouz.fragments.UserCardFragment
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by matthewtaila on 12/25/17.
 */

class MapSearchActivity : FragmentActivity(), OnMapReadyCallback, View.OnClickListener,
        PromptSettingsFragment.UserSearchSettingsListener{

    var maps: GoogleMap? = null



    // userFlow variables
    var gotUserFromFirestoreDocuments = false


    //Activity variables
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1
    var marker : Marker? = null
    var markerMatch : Marker? = null

    //camera variables
    private var photoFile: File? = null
    private lateinit var timeStamp: String

    // firebase variables
    private var mUser = RendevouzUserModel()
    private var USER_PERMISSION_FINE_LOCATION = 11
    private var USER_PERMISSION_COARSE_LOCATION = 21

    var imageStored = false

    // location variables
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    lateinit var map: MapFragment
    lateinit var mFusedLocationProvider: FusedLocationProviderClient

    // activity variables
    var mAnimationList : ArrayList<Animator> = ArrayList()
    var stubList : ArrayList<RendevouzUserModel> = ArrayList()
    lateinit var mBinding : ActivityMapSearchBinding
    var mSearchSettings =  UserSearchSettings()

    // todo - this will be replaced with groups when it's there's a stable release
    // views variables
    var onboardingMessageTopViews: ArrayList<View> = ArrayList()
    var onboardingMessageMiddleViews: ArrayList<View> = ArrayList()
    var onboardingMessageBottomViews: ArrayList<View> = ArrayList()
    var onboardingMessageAllViews: ArrayList<View> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map_search)

        gotUserFromFirestoreDocuments = true // guards against race condition issue with getUser from onResume

        // needed to get heights of topCurveContainerFragment for animation
        mBinding.root.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                slideIn(mBinding.topContainerFrame, -1f, 0f)
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })

        stubList = MapUtil.generateStubData()

        startListeningForEventBus()
        checkUserPermissionGrantStatus()
        setOnClickListeners()
//        showCard()
        setupTopContainer()
    }

    private fun setupTopContainer() {
        var fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.topContainerFrame, TopContainerFragment())
        fragmentManager.commit()

    }

    private fun slideIn(view: View, from: Float, to: Float) {
        val mOnEnterSlideInAnimator = AnimationUtil.slideInAnimator(view, from, to)
        mOnEnterSlideInAnimator.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                picturePreviewOnEnterAnimation()
            }
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                getUserDocument()
            }
        })
        mOnEnterSlideInAnimator.start()
    }

    private fun picturePreviewOnEnterAnimation() {

        val mPicturePreviewAnimator = AnimationUtil.picturePreviewSlideInAnimator(mBinding.picturePreview, mBinding.topContainerFrame.measuredHeight)
        mPicturePreviewAnimator.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                handleCaptureImageCardView(1f)
            }
        })
        mPicturePreviewAnimator.start()
    }

    private fun startListeningForEventBus() {
        (application as MyApplication)
                .bus()
                .toObservable()
                .subscribe { `object` ->
                    if (`object` is String) {
                        when (`object`.toString()) {
                            "USER_SEARCH_SETTINGS_STORED"->{
                                var anim = ViewAnimationUtils.createCircularReveal(mBinding.clOnBoardUserContainer,
                                        (mBinding.clOnBoardUserContainer.right)/2,
                                        (mBinding.clOnBoardUserContainer.bottom)/2,
                                        (mBinding.clOnBoardUserContainer.height).toFloat(),
                                        0f)
                                anim.interpolator = AccelerateDecelerateInterpolator()
                                anim.duration = 450
                                anim.addListener(object: AnimatorListenerAdapter(){
                                    override fun onAnimationEnd(animation: Animator?) {
                                        super.onAnimationEnd(animation)
                                        mBinding.clOnBoardUserContainer.visibility = View.GONE
//                                        handleCaptureImageCardView(1f)
                                    }
                                })
                                anim.start()
                            }
                            TopContainerFragment().HIDE_CONTAINER->{
                                mBinding.picturePreview.visibility = GONE
                            }
                            TopContainerFragment().SHOW_CONTAINER->{
                                mBinding.picturePreview.visibility = VISIBLE
                                if (photoFile != null){
                                    var bMap = BitmapFactory.decodeFile(photoFile!!.path)
                                    mBinding.picturePreview.setImageBitmap(bMap)
                                }
                            }
                        }
                    }
                }
    }

    private fun showCard() {
        val fragment = UserCardFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.userListFragment, fragment).commit()
    }

    override fun onResume() {
        super.onResume()

        // if mUser already instantiated from onCreate, no need to do it again
        // if onPause is called, update the user on onResume
        if (!gotUserFromFirestoreDocuments){
            getUserDocument()
        }
    }

    private fun getUserDocument() {
        var mUserRef = RxUserUtil().getUserDocument()

        rx.Single.create<Unit> {
            mUserRef.get().addOnSuccessListener {
                data : DocumentSnapshot -> if (data.exists()){
                // handle hardcoded values (strings) correctly
                    mUser.emailAddress = data.getString("emailAddress")
                    mUser.uuID = data.getString("uuID")
                    mUser.username = data.getString("username")
                    mUser.requiresOnboarding = data.getBoolean("requiresOnboarding")
                    gotUserFromFirestoreDocuments = true
                    directUser(mUser)
                }
            }.addOnFailureListener {
                gotUserFromFirestoreDocuments = false
            }
        }.subscribe()
    }

    override fun onPause() {
        super.onPause()
        gotUserFromFirestoreDocuments = false
    }

    private fun directUser(mUser: RendevouzUserModel) {
        if (mUser.requiresOnboarding){
            startUserOnBoardingProcess()
        } else {
            var  mSearchSettings = getUserSearchSettings()
            // todo - check to see if image is already taken within 24 hours
            if(!imageStored) {
//                handleCaptureImageCardView(1f)
            }
        }
    }

    private fun startUserOnBoardingProcess() {
        // make container visible + background
        mBinding.cvOnBoardingMessageContainer.visibility = VISIBLE
        val container = mBinding.cvOnBoardingMessageContainer
        mBinding.vOnBoardingTintBackground.visibility = VISIBLE

        mBinding.vOnBoardingTintBackground.z = 6f
        container.z = 8f
        mAnimationList.clear()

        // animate - the expansion of the container with a bounce scaleX/Y
        // animate - alpha

        var scaleAnim = ScaleAnimation(0.3f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 650
        scaleAnim.interpolator = OvershootInterpolator(1.5f)

        mAnimationList.add(AnimationUtil.alpha(view = container, alphaValue = 1f))
        mAnimationList.add(AnimationUtil.alpha(view = mBinding.vOnBoardingTintBackground, alphaValue = 0.75f))

        val showContainerAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)

        showContainerAnimatorSet.duration = 300
        showContainerAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        showContainerAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                container.startAnimation(scaleAnim)
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)

                mBinding.tvBottomObBoardingMessage.setOnClickListener {
                    _ -> customTransitionIntoOnBoardingActivity(container)
                }

                onboardingMessageTopViews.add(mBinding.tvTopOnBoardingMessage)
                onboardingMessageTopViews.add(mBinding.vTopBreaker)

                onboardingMessageMiddleViews.add(mBinding.tvMiddleOnBoardingMessage)

                onboardingMessageBottomViews.add(mBinding.vWelcomeHeaderBreaker)
                onboardingMessageBottomViews.add(mBinding.tvBottomObBoardingMessage)


                var topAnimatorSet = AnimationUtil.onBoardingStaggered(onboardingMessageTopViews,
                        standardHeight = mBinding.tvTopOnBoardingMessage.measuredHeight, startDelay = 0 )

                var middleAnimatorSet = AnimationUtil.onBoardingStaggered(onboardingMessageMiddleViews,
                        standardHeight = mBinding.tvTopOnBoardingMessage.measuredHeight, startDelay = 100 )

                var bottomAnimatorSet = AnimationUtil.onBoardingStaggered(onboardingMessageBottomViews,
                        standardHeight = mBinding.tvTopOnBoardingMessage.measuredHeight, startDelay = 200 )

                val staggeredContentAnimatorSet = AnimatorSet()
                staggeredContentAnimatorSet.playTogether(topAnimatorSet, middleAnimatorSet, bottomAnimatorSet)
                staggeredContentAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
                staggeredContentAnimatorSet.duration = 350
                staggeredContentAnimatorSet.start()

            }
        })
        showContainerAnimatorSet.start()

    }

    private fun customTransitionIntoOnBoardingActivity(container: CardView) {
        val screenLocation : IntArray = IntArray(2)
        container.getLocationOnScreen(screenLocation)
        var intent = Intent(this@MapSearchActivity, OnBoardingActivity::class.java)
        intent.putExtra("Left", screenLocation[0])
        intent.putExtra("Top", screenLocation[1])
        intent.putExtra("Width", container.width)
        intent.putExtra("Height", container.height)
        intent.putExtra("mUser", mUser)
        startActivity(intent)
        overridePendingTransition(0, 0)
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

        if (!hide){
            var animatorSet = AnimationUtil.handleCaptureImageCardview(mBinding.cvPromptUserImage)
            animatorSet.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (hide){
                        mBinding.cvPromptUserImage.visibility = GONE
                        mBinding.tvTakeAPicture.setOnClickListener(null)
                    } else {
                        mBinding.tvTakeAPicture.setOnClickListener(this@MapSearchActivity)
                    }
                }
            })
//            animatorSet.start()
        } else {
            mBinding.cvPromptUserImage.visibility = GONE
            mBinding.tvTakeAPicture.setOnClickListener(null)
        }

    }


    private fun getUserSearchSettings() : UserSearchSettings {
        var uuID = FirebaseAuth.getInstance().currentUser!!.uid
        var mFirestore = RxUserUtil().UserSettingsCollectionReference(uuID)
        mFirestore.document(uuID).get().addOnSuccessListener {
            data : DocumentSnapshot ->
            if (data.exists()){
                if (data.getBoolean("settingsCompleted")){
                    mSearchSettings = data.toObject(UserSearchSettings::class.java)
                }
            } else {
                Log.d(javaClass.simpleName, "document snapshot does not exis")
            }
        }
        return mSearchSettings
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_Settings ->{
                var intent = Intent(this@MapSearchActivity, SearchSettingsActivity::class.java)
                intent.putExtra(Constants().USER_SEARCH_SETTINGS_OBJECT, mSearchSettings)
                intent.putExtra(Constants().SETTINGS_VIEWPAGER_LANDING, 0)
                startActivity(intent)
            }
            R.id.tv_TakeAPicture ->{
                launchCamera()
            }
             R.id.picturePreview ->{
                var intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("ADSAD", mSearchSettings)
                intent.putExtra("PHOTO", photoFile)

                var options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        mBinding.picturePreview,
                        ViewCompat.getTransitionName(mBinding.picturePreview))
                startActivity(intent, options.toBundle())
            }
        }
    }

    private fun launchCamera() {

        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFileUri(generateTimeStamp())

        var fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
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
            imageStored = true
            mBinding.picturePreview.visibility = VISIBLE
            var bMap = BitmapFactory.decodeFile(photoFile!!.path)
            mBinding.picturePreview.setImageBitmap(bMap)
//            Glide.with(this).load(photoFile!!.path).apply(RequestOptions.circleCropTransform()).into(mBinding.picturePreview)
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
//                    handleCaptureImageCardView(1f)
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        maps = MapUtil.buildGoogleMapFrag(p0!!, this)
        MapUtil.setupCameraPosition(mFusedLocationProvider, this, maps!!)
    }

    open fun setupUserMarkers(location: Location){
        marker = maps!!.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
        markerMatch = MapUtil.setupUsersAroundMeMarker(this)
        MapUtil.showEveryTen(this)
    }

    private fun setupLocationListener() {
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (mUser.uuID == FirebaseAuth.getInstance().currentUser!!.uid){
                    MapUtil.updateUserLocation(location)
                }
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    // user permissions functions
    private fun checkUserPermissionGrantStatus() {
        val locationPermission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)

        when {
                locationPermission == PackageManager.PERMISSION_DENIED -> {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        USER_PERMISSION_FINE_LOCATION)
                }
                coarsePermission == PackageManager.PERMISSION_DENIED -> {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            USER_PERMISSION_COARSE_LOCATION)
                }
                else-> {
                mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                map = (fragmentManager.findFragmentById(R.id.map) as MapFragment)
                map.getMapAsync(this)
                mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                setupLocationListener()
                registerLocationListener()
                }
            }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            USER_PERMISSION_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkUserPermissionGrantStatus()
                } else {
                    // todo prevent location based stuff
                }
                return
            }
            USER_PERMISSION_COARSE_LOCATION ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkUserPermissionGrantStatus()
                } else {
                    // todo prevent location based stuff
                }
                return
            }
        }
    }

}