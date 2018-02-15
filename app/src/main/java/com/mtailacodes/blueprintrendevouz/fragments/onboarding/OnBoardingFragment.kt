package com.mtailacodes.blueprintrendevouz.fragments.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentOnboardingBinding
import com.mtailacodes.blueprintrendevouz.viewpagerAdapter.OnBoardingViewPagerAdapter



/**
 * Created by matthewtaila on 2/10/18.
 */

class OnBoardingFragment : Fragment(){

    private lateinit var mBinding : FragmentOnboardingBinding
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onEnterAnimation()

        (activity.application as MyApplication)
                .bus()
                .toObservable()
                .subscribe { `object` ->
                    if (`object` is String) {
                        when (`object`.toString()) {
                            "NEXT" ->{
                                mBinding.vpOnBoardingViewPager.currentItem = 1
                            }
                            "USER_DATA_STORED" ->{
                                mBinding.vpOnBoardingViewPager.currentItem = 2
                            }
                        }
                    }
                }
    }

    private fun setupOnBoardingViewPager() {
        var mViewPagerAdapter = OnBoardingViewPagerAdapter(fragmentManager)
        mBinding.vpOnBoardingViewPager.adapter = mViewPagerAdapter
        mBinding.vpOnBoardingViewPager.setPagingEnabled(false)
    }

    private fun onEnterAnimation() {

        mBinding.cvOnBoardingCardviewContainer.pivotX = 0f
        var scaleXAnimator = ObjectAnimator.ofFloat(mBinding.cvOnBoardingCardviewContainer, View.SCALE_X, 1f)
        var scaleYAnimator = ObjectAnimator.ofFloat(mBinding.cvOnBoardingCardviewContainer, View.SCALE_Y, 1f)

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.duration = 400
        animatorSet.addListener(object : AnimatorListenerAdapter(){

            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                setupOnBoardingViewPager()
            }
        })
        animatorSet.start()

    }


}