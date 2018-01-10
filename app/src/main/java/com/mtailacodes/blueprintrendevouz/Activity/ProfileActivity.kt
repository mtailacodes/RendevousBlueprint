package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.ValueAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewPropertyAnimator
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK);
        photoFile = intent.extras.get("profilePic") as File
        Glide.with(this).load(photoFile.path).into(mBinding.ivProfilePicImageView)

        mBinding.testView.radius
        var radius = ValueAnimator.ofFloat(0f, 150f)
        radius.addUpdateListener{ valueAnimator ->
            var value: Float = valueAnimator.animatedValue as Float
            mBinding.testView.radius = value // todo - user proper syntax - ask tom why it wont work when you take out set
        }
        radius.duration = 3000
        radius.start()

    }



    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }
}