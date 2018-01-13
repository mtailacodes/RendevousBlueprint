package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.appeaser.imagetransitionlibrary.ImageTransitionUtil
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityProfileBinding
import java.io.File

/**
 * Created by matthewtaila on 1/8/18.
 */
class ProfileActivity: AppCompatActivity(){

    private lateinit var photoFile: File
    private lateinit var mBinding: ActivityProfileBinding

    //    activity variables
    var mAnimationList : ArrayList<Animator> = ArrayList()
    var guideline = 0
    var top = 0
    var fabPivotX = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK)
        photoFile = intent.extras.get("profilePic") as File
        Glide.with(this).load(photoFile.path).into(mBinding.ivProfilePicImageView)

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
}