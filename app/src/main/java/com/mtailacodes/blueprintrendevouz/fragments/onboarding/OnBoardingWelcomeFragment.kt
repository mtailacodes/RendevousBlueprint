package com.mtailacodes.blueprintrendevouz.fragments.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.FragmentOnboardingWelcomeBinding


/**
 * Created by matthewtaila on 2/10/18.
 */
class OnBoardingWelcomeFragment: Fragment(), View.OnClickListener{

    private lateinit var mBinding : FragmentOnboardingWelcomeBinding
    companion object {
        fun newInstance(): OnBoardingWelcomeFragment {
            var mFragment = OnBoardingWelcomeFragment()
            return mFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_onboarding_welcome, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEnterAnimation()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        mBinding.tvNext.setOnClickListener(this)
    }

    private fun onEnterAnimation() {

        // may need to refactorThis

        var headerStart = ObjectAnimator.ofFloat(mBinding.tvWelcomeHeader, View.TRANSLATION_Y, -40f)
        var topBreakerStart = ObjectAnimator.ofFloat(mBinding.vWelcomeHeaderBreaker, View.TRANSLATION_Y, -40f)
        var contentStart = ObjectAnimator.ofFloat(mBinding.tvContent, View.TRANSLATION_Y, -40f)
        var bottomBreakerStart = ObjectAnimator.ofFloat(mBinding.vNextBreaker, View.TRANSLATION_Y, -40f)
        var nextStart = ObjectAnimator.ofFloat(mBinding.tvNext, View.TRANSLATION_Y, -40f)

        var startingPositions = AnimatorSet()
        startingPositions.playTogether(headerStart, topBreakerStart, contentStart, bottomBreakerStart, nextStart)
        startingPositions.duration = 0
        startingPositions.start()

        var headerTranslateY = ObjectAnimator.ofFloat(mBinding.tvWelcomeHeader, View.TRANSLATION_Y, 0f)
        var headerAlpha = ObjectAnimator.ofFloat(mBinding.tvWelcomeHeader, View.ALPHA, 1f)
        var topBreakerTranslateY = ObjectAnimator.ofFloat(mBinding.vWelcomeHeaderBreaker, View.TRANSLATION_Y, 0f)
        var topBreakerAlpha = ObjectAnimator.ofFloat(mBinding.vWelcomeHeaderBreaker, View.ALPHA, 1f)

        var headerViewsAnimatorSet = AnimatorSet()
        headerViewsAnimatorSet.playTogether(headerTranslateY, headerAlpha, topBreakerTranslateY, topBreakerAlpha)
        headerViewsAnimatorSet.startDelay = 175
        headerViewsAnimatorSet.duration = 300

        var contentTranslateY = ObjectAnimator.ofFloat(mBinding.tvContent, View.TRANSLATION_Y, 0f)
        var contentAlpha = ObjectAnimator.ofFloat(mBinding.tvContent, View.ALPHA, 1f)

        var contentAnimatorSet = AnimatorSet()
        contentAnimatorSet.playTogether(contentTranslateY, contentAlpha)
        contentAnimatorSet.startDelay = 200
        contentAnimatorSet.duration = 300

        var nextTranslateY = ObjectAnimator.ofFloat(mBinding.vNextBreaker, View.TRANSLATION_Y, 0f)
        var nextAlpha = ObjectAnimator.ofFloat(mBinding.vNextBreaker, View.ALPHA, 1f)
        var nextBreakerTranslateY = ObjectAnimator.ofFloat(mBinding.tvNext, View.TRANSLATION_Y, 0f)
        var nextBreakerAlpha = ObjectAnimator.ofFloat(mBinding.tvNext, View.ALPHA, 1f)

        var nextViewsAnimatorSet = AnimatorSet()
        nextViewsAnimatorSet.playTogether(nextTranslateY, nextAlpha, nextBreakerAlpha, nextBreakerTranslateY)
        nextViewsAnimatorSet.startDelay = 225
        nextViewsAnimatorSet.duration = 300

        var finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.playTogether(headerViewsAnimatorSet, contentAnimatorSet, nextViewsAnimatorSet)
        finalAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalAnimatorSet.start()

    }

    override fun onClick(p0: View) {
        when (p0.id){
            R.id.tv_Next -> {
                (activity.application as MyApplication)
                        .bus()
                        .send("NEXT")
            }

        }
    }
}
