package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
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
import com.mtailacodes.blueprintrendevouz.adapter.HeterogenousProfileSettingsAdapter
import com.mtailacodes.blueprintrendevouz.databinding.ActivityProfileBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings.*
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import com.mtailacodes.blueprintrendevouz.viewholder.SettingsViewholder
import java.io.File
import java.text.FieldPosition

/**
 * Created by matthewtaila on 1/8/18.
 */
class ProfileActivity: AppCompatActivity(), HeterogenousProfileSettingsAdapter.OnItemClickListener{


    private lateinit var photoFile: File
    private lateinit var mBinding: ActivityProfileBinding

    //    activity variables
    var mAnimationList : ArrayList<Animator> = ArrayList()
    var mProfileSettingsList = ArrayList<ProfileSettingsHeader>()
    var guideline = 0
    var top = 0
    var fabPivotX = 0
    lateinit var mSearchSettings : UserSearchSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK)

        photoFile = intent.extras.get("profilePic") as File
        mSearchSettings = intent.extras.get("SearchSettings") as UserSearchSettings
        Glide.with(this).load(photoFile.path)!!.into(mBinding.ivProfilePicImageView)

        mBinding.ivProfilePicImageView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.ivProfilePicImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                guideline = mBinding.ivProfilePicImageView.bottom
            }
        })

        mBinding.testView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.testView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        top = mBinding.testView.top
                        onEnterAnimation()
                    }
                })

        mBinding.fabCamera.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.fabCamera.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        fabPivotX = mBinding.fabCamera.width/2
                        onEnterAnimation()
                    }
                })

        generateProfileSettingsList()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        var layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mBinding.rvProfileSetting.layoutManager = layoutManager
        var mSettingsAdapter = HeterogenousProfileSettingsAdapter(mProfileSettingsList, this)
        mBinding.rvProfileSetting.adapter = mSettingsAdapter
    }

    private fun generateProfileSettingsList() {
        mProfileSettingsList.add(ProfileHightlight())
        mProfileSettingsList.add(SessionDescription("Hnaging out with Oscar"))
        mProfileSettingsList.add(ProfileSettingsBreak())
        mProfileSettingsList.add(ProfileSettings("Profile information"))
        mProfileSettingsList.add(ProfileSettings("Search settings"))
        mProfileSettingsList.add(ProfileSettings("Notification settings"))
        mProfileSettingsList.add(ProfileSettingsBreak())
    }

    private fun onEnterAnimation() {

        var settingsContainerTranslateYValue = (top - guideline).toFloat()
        var translateContent = AnimationUtil.translateY(mBinding.testView, duration = 400,
                translationYValue = -settingsContainerTranslateYValue)

        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.scaleY(mBinding.fabCamera, duration = 200,
                heightToValue = 1f, startDelay = 75, pivot = fabPivotX.toFloat(),
                interpolator = OvershootInterpolator(1.7f)))
        mAnimationList.add(AnimationUtil.scaleX(mBinding.fabCamera, duration = 200,
                heightToValue = 1f, startDelay = 75, pivot = fabPivotX.toFloat(),
                interpolator = OvershootInterpolator(1.7f)))
        mAnimationList.add(AnimationUtil.alpha(mBinding.fabCamera, duration = 150,
                alphaValue = 1f, interpolator = AccelerateInterpolator()))

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
                if (profileSetting.description == "Search settings"){
//                    var holder = viewHolder as SettingsViewholder
//                    holder.container.transitionName = "searchSettingsActivity"
//
//                    var hideFAB = AnimationUtil.alpha(mBinding.fabCamera, duration = 50, alphaValue = 0f)
//                    var hidePic = AnimationUtil.alpha(mBinding.ivProfilePicImageView, duration = 50, alphaValue = 0f)
//
//                    var animatorSet = AnimatorSet()
//                    animatorSet.playTogether(hidePic, hideFAB)
//                    animatorSet.addListener(object: AnimatorListenerAdapter(){
//                        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
//                            super.onAnimationEnd(animation, isReverse)
//                            mBinding.fabCamera.z = 0f
//                            var intent = Intent(this@ProfileActivity, SearchSettingsActivity::class.java)
//                            var options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                    this@ProfileActivity, viewHolder.container,
//                                    "searchSettingsActivity")
//                            startActivity(intent, options.toBundle())
//                        }
//                    })
//                    animatorSet.start()
                    var intent = Intent(this@ProfileActivity, SearchSettingsActivity::class.java)
                    intent.putExtra("SearchSettings", mSearchSettings)
                    startActivity(intent)

                } else if (profileSetting.description == "Notification settings"){
                    var intent = Intent(this@ProfileActivity, NotificationSettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            11->{
                Log.i("Settings", "Break")
            }
        }
    }
}