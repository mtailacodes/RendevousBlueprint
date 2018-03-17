package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivityOnboardingBinding

/**
 * Created by matthewtaila on 3/14/18.
 */
class OnBoardingActivity : AppCompatActivity(){

    lateinit var mBinding : ActivityOnboardingBinding

    // custom transition activity variables
    var mLeftDelta : Int = 0
    var mTopDelta : Int = 0
    var mWidthScale : Float = 0f
    var mHeightScale : Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)

        val bundle = intent.extras
        val givenLeft = bundle.getInt("Left")
        val givenTop = bundle.getInt("Top")
        val givenWidth = bundle.getInt("Width")
        val givenHeight = bundle.getInt("Height")
        val container = mBinding.cvTransitionContainer

        mBinding.root.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val screenLocation = IntArray(2)
                container.getLocationOnScreen(screenLocation)
                mLeftDelta = givenLeft - screenLocation[0]
                mTopDelta = givenTop - screenLocation[1]
                mWidthScale = (givenWidth/container.width).toFloat()
                mHeightScale = (givenHeight/container.height).toFloat()

                runOnEnterAnimation()
                container.viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }

    private fun runOnEnterAnimation() {

        // Transition animation set
        val container = mBinding.cvTransitionContainer
        container.pivotY = 0f
        container.pivotX = 0f
        container.scaleX = 0.8f
        container.scaleY = 0.363f
        container.translationX = mLeftDelta.toFloat()
        container.translationY = mTopDelta.toFloat()

        var scaleY = ObjectAnimator.ofFloat(container, View.SCALE_Y, 1f)
        var scaleX = ObjectAnimator.ofFloat(container, View.SCALE_X, 1f)
        var transX = ObjectAnimator.ofFloat(container, View.TRANSLATION_X, 0f)
        var transY = ObjectAnimator.ofFloat(container, View.TRANSLATION_Y, 0f)
        var alpha = ObjectAnimator.ofFloat(container, View.ALPHA, 1f, 1f)

        var cornerRadius = ValueAnimator.ofFloat( 1f)
        cornerRadius.addUpdateListener { animator -> container.radius = animator.animatedValue as Float }

        var mAnimationList : ArrayList<Animator> = ArrayList()
        mAnimationList.add(transX)
        mAnimationList.add(transY)
        mAnimationList.add(alpha)
        mAnimationList.add(cornerRadius)
        mAnimationList.add(scaleX)
        mAnimationList.add(scaleY)

        var finalTransitionAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        finalTransitionAnimatorSet.duration = 200
        finalTransitionAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalTransitionAnimatorSet.start()

    }
}