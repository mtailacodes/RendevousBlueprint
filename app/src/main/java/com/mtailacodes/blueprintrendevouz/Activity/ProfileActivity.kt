package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import com.appeaser.imagetransitionlibrary.ImageTransitionUtil
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.ActivityProfileBinding
import java.io.File

/**
 * Created by matthewtaila on 1/8/18.
 */
class ProfileActivity: AppCompatActivity(){

    private lateinit var photoFile: File
    private lateinit var mBinding: ActivityProfileBinding

    var guideline = 0
    var top = 0

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

    }

    private fun onEnterAnimation() {
        var asdasd = (top - guideline).toFloat()
        var batner = ObjectAnimator.ofFloat(mBinding.testView,
                View.TRANSLATION_Y,
                - asdasd)
        batner.duration = 400
        batner.start()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }
}