package com.mtailacodes.blueprintrendevouz.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentOnboardingBinding

/**
 * Created by matthewtaila on 2/10/18.
 */
class OnBoardingFragment : Fragment(){

    private lateinit var mBinding : FragmentOnboardingBinding
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding, container, false)

        // animate in Views
        onEnterAnimation()
        return mBinding.root
    }

    private fun onEnterAnimation() {
        mBinding.cvOnBoardingCardviewContainer.pivotX = 0f
        var scaleXAnimator = ObjectAnimator.ofFloat(mBinding.cvOnBoardingCardviewContainer, View.SCALE_X, 1f)
        var scaleYAnimator = ObjectAnimator.ofFloat(mBinding.cvOnBoardingCardviewContainer, View.SCALE_Y, 1f)

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 450
        animatorSet.start()

    }
}