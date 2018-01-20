package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityNotificationSettingsBinding

/**
 * Created by matthewtaila on 1/18/18.
 */
class NotificationSettingsActivity: AppCompatActivity(){

    private var mAnimationList : ArrayList<Animator> = ArrayList()
    lateinit var mBinding : ActivityNotificationSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_settings)
        onEnterActivityAnimation()
    }

    private fun onEnterActivityAnimation() {
        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.translateY(mBinding.tvNotificationTitle, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue),
                duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvNotificationTitle, duration = 700, startDelay = 200,
                alphaValue = 1f))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        mAnimatorSet.start()
    }
}