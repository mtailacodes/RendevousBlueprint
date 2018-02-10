package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import com.appeaser.imagetransitionlibrary.ImageTransitionUtil
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.adapter.HeterogenousProfileSettingsAdapter
import com.mtailacodes.blueprintrendevouz.databinding.ActivityProfileBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings.*
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import java.io.File

/**
 * Created by matthewtaila on 1/8/18.
 */
class ProfileActivity: AppCompatActivity(), HeterogenousProfileSettingsAdapter.OnItemClickListener{

    private lateinit var photoFile: File
    private lateinit var mBinding: ActivityProfileBinding

    //    activity variables
    private var mAnimationList : ArrayList<Animator> = ArrayList()
    private var mProfileSettingsList = ArrayList<ProfileSettingsHeader>()
    private var profilePictureAnimationGuideline = 0
    private var rvContainerAnimationGuideline = 0
    private var fabPivotX = 0
    private lateinit var mSearchSettings : UserSearchSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        mSearchSettings = UserSearchSettings()

        setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK) // animate the photo

        // todo - might need to change this in the future by just passing the string value instead of the whole object
        // use photofile object to grab path of photo
        photoFile = intent.extras.get(Constants.PHOTO_FILE__OBJECT) as File
        Glide.with(this).load(photoFile.path)!!.into(mBinding.ivProfilePicImageView)

        // instead of making another API call, the userSearchSettings gets passed to this activity from the MapSearchSettingsActivity
        mSearchSettings = intent.extras.get(Constants.USER_SEARCH_SETTINGS_OBJECT)!! as UserSearchSettings

        // set the profilePictureAnimationGuideline value - which will be later used to determine the translateY value when OnEnterAnimation is called
        mBinding.ivProfilePicImageView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.ivProfilePicImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                profilePictureAnimationGuideline = mBinding.ivProfilePicImageView.bottom
            }
        })

        // set the rvContainerAnimationGuideline value - which will be later used to determine the translateY value when OnEnterAnimation is called
        mBinding.clSettingsRVContainer.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.clSettingsRVContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        rvContainerAnimationGuideline = mBinding.clSettingsRVContainer.top
                    }
                })

        // determine the pivotValue to animate the FAB
        mBinding.fabCamera.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.fabCamera.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        fabPivotX = mBinding.fabCamera.width/2
                        onEnterAnimation() // animation on enter activity
                    }
                })

        generateSettingsHeadersList() // for settings recyclerview
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        var layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mBinding.rvProfileSetting.layoutManager = layoutManager
        var mSettingsAdapter = HeterogenousProfileSettingsAdapter(mProfileSettingsList, this)
        mBinding.rvProfileSetting.adapter = mSettingsAdapter
    }

    private fun generateSettingsHeadersList() {
        mProfileSettingsList.add(ProfileHightlight())
        // todo (for Session Description) - add a if statement to check if this has been filled out by the user (needs to be added to the RendevousUser Object)
        // todo - if yes, add this view to the rv header list, otherwise dont
        mProfileSettingsList.add(SessionDescription("Hanging out with Oscar")) // todo - grab this from the user's profile
        mProfileSettingsList.add(ProfileSettingsBreak()) // break
        mProfileSettingsList.add(ProfileSettings(Constants.SEARCH_TITLE)) // Search Settings shortcut
        mProfileSettingsList.add(ProfileSettings(Constants.PROFILE_TITLE)) // Profile Settings shortcut
        mProfileSettingsList.add(ProfileSettings(Constants.NOTIFICATION_TITLE)) // Notification Settings shortcut
        mProfileSettingsList.add(ProfileSettingsBreak()) // break
    }

    private fun onEnterAnimation() {

        var settingsContainerTranslateYValue = (rvContainerAnimationGuideline - profilePictureAnimationGuideline).toFloat()
        var translateContent = AnimationUtil.translateY(mBinding.clSettingsRVContainer, duration = 400, translationYValue = -settingsContainerTranslateYValue)

        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.scaleY(mBinding.fabCamera, duration = 200, heightToValue = 1f, startDelay = 75, pivot = fabPivotX.toFloat(),
                interpolator = OvershootInterpolator(1.7f)))
        mAnimationList.add(AnimationUtil.scaleX(mBinding.fabCamera, duration = 200, heightToValue = 1f, startDelay = 75, pivot = fabPivotX.toFloat(),
                interpolator = OvershootInterpolator(1.7f)))
        mAnimationList.add(AnimationUtil.alpha(mBinding.fabCamera, duration = 150, alphaValue = 1f, interpolator = AccelerateInterpolator()))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)

        translateContent.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mAnimatorSet.start()
            }
        })
        translateContent.start()
    }

    override fun onBackPressed() {
        var hideFAB = AnimationUtil.alpha(mBinding.fabCamera, duration = 50, alphaValue = 0f)
        hideFAB.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                supportFinishAfterTransition()
            }
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                mBinding.fabCamera.z = 0f
            }
        })
        hideFAB.start()
        super.onBackPressed()
    }

    override fun onItemClick(profileSetting: ProfileSettingsHeader, viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (profileSetting.headerType){
            3->{

                var intent = Intent(this@ProfileActivity, SearchSettingsActivity::class.java)
                intent.putExtra(Constants.USER_SEARCH_SETTINGS_OBJECT, mSearchSettings)

                var landing = 0 // determine landing page for viewpager in SearchSettingsActivity

                when(profileSetting.description){
                    Constants.SEARCH_TITLE ->{
                           landing = 0
                    }
                    Constants.PROFILE_TITLE ->{
                        landing = 1
                    }
                    Constants.NOTIFICATION_TITLE ->{
                        landing = 2
                    }
                }
                intent.putExtra(Constants.SETTINGS_VIEWPAGER_LANDING, landing)
                startActivity(intent)
            }
            11->{
                Log.i("Settings", "Break")
            }
        }
    }
}