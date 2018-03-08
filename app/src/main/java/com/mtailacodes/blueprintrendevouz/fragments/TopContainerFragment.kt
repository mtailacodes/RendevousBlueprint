package com.mtailacodes.blueprintrendevouz.fragments

import android.animation.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.R.id.tv_Settings
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.FragmentTopContainerBinding


/**
 * Created by matthewtaila on 2/24/18.
 */

class TopContainerFragment : Fragment(), View.OnClickListener {

    var down = false
    lateinit var mBinding : FragmentTopContainerBinding
    val HIDE_CONTAINER = "HIDE PICTURE PREVIEW"
    val SHOW_CONTAINER = "SHOW PICTURE PREVIEW"
    val containerMaxHeight = 1200
    var mAnimationList : ArrayList<Animator> = ArrayList()
    var mViewsList : ArrayList<View> = ArrayList()

    companion object {
        fun newInstance(): TopContainerFragment {
            var mFragment = TopContainerFragment()
            return mFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_container, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        staggeredHeaderNavAnimation()
        mBinding.curveParentContainer.setOnClickListener(this)
        generateViewsList() // view to be animated when user interacts with the container
    }

    private fun generateViewsList() {
        mViewsList.add(mBinding.tvSettings)
        mViewsList.add(mBinding.ivSettingsIcon)
        mViewsList.add(mBinding.tvMatchesHeader)
        mViewsList.add(mBinding.ivMatchesIcon)
    }

    override fun onClick(p0: View) {
        when (p0.id){
            R.id.curveParentContainer ->{
                if (down){
                    pullUp()
                } else {
                    pullDown()
                }
            }
        }
    }

    private fun staggeredHeaderNavAnimation() {
        var viewsList : ArrayList<View> = ArrayList()

        viewsList.add(mBinding.ivMatchesIcon)
        viewsList.add(mBinding.ivSettingsIcon)

        var iconsAnimatorSet = AnimatorSet()

        for (v in viewsList){
            val animator = ValueAnimator.ofFloat(-0.25f, 0f)
            animator.addUpdateListener {
                animator -> v.translationY = (animator.animatedValue as Float) * mBinding.curveParentContainer.measuredHeight
            }
            iconsAnimatorSet.play(animator)
        }
        iconsAnimatorSet.duration = 750

        viewsList.clear()

        viewsList.add(mBinding.tvSettings)
        viewsList.add(mBinding.tvMatchesHeader)

        var headerAnimatorSet = AnimatorSet()
        for (v in viewsList){
            val animator = ValueAnimator.ofFloat(-0.4f, 0f)
            animator.addUpdateListener {
                animator -> v.translationY = (animator.animatedValue as Float) * mBinding.curveParentContainer.measuredHeight
            }
            headerAnimatorSet.play(animator)
        }
        headerAnimatorSet.duration = 850

        var finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.playTogether(iconsAnimatorSet, headerAnimatorSet)
        finalAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
//                handleCaptureImageCardView(1f)
            }
        })
        finalAnimatorSet.start()

    }

    private fun pullUp() {
        var params = mBinding.curveParentContainer.layoutParams
        mBinding.curveParentContainer.layoutParams = params

        val heightAnimator = ValueAnimator.ofInt( containerMaxHeight, 263)
        heightAnimator.addUpdateListener { animator ->
            params.height = animator.animatedValue as Int
            mBinding.curveParentContainer.layoutParams = params
        }

        val curveAnimator = ValueAnimator.ofInt( 0, mBinding.curveParentContainer.biezerYDefaultVal2)
        curveAnimator.addUpdateListener { animator ->
            mBinding.curveParentContainer.setBiezerY(animator.animatedValue as Int)
        }

        val sideContainerAnimator = ValueAnimator.ofFloat(mBinding.curveParentContainer.sideControl,
                mBinding.curveParentContainer.sideControlDefaultValue)
        sideContainerAnimator.addUpdateListener { animator ->
            mBinding.curveParentContainer.setSideYValue(animator.animatedValue as Float)
        }

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(curveAnimator, heightAnimator, sideContainerAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                (activity.application as MyApplication)
                        .bus()
                        .send(SHOW_CONTAINER)
            }

            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                mAnimationList.clear()
                for (view in mViewsList) {
                    mAnimationList.add(AnimationUtil.alpha(view = view, alphaValue =  1f))
                }
                var hideContainerSettingsShortcutViews = AnimationUtil.combineToAnimatorSet(mAnimationList)
                hideContainerSettingsShortcutViews.duration = 200
                hideContainerSettingsShortcutViews.interpolator = AccelerateInterpolator()
                hideContainerSettingsShortcutViews.start()
            }
        })
        animatorSet.start()
        down = false
    }

    private fun pullDown() {
        var params = mBinding.curveParentContainer.layoutParams
        var currentHeight = (mBinding.curveParentContainer.xHeight).toInt()
        mBinding.curveParentContainer.layoutParams = params

        val heightAnimator = ValueAnimator.ofInt(currentHeight, containerMaxHeight)
        heightAnimator.addUpdateListener { animator ->
            params.height = animator.animatedValue as Int
            mBinding.curveParentContainer.layoutParams = params
        }

        val curveAnimator = ValueAnimator.ofInt(mBinding.curveParentContainer.biezerYValue, 0)
        curveAnimator.addUpdateListener { animator ->
            mBinding.curveParentContainer.setBiezerY(animator.animatedValue as Int)
        }

        val sideContainerAnimator = ValueAnimator.ofFloat(mBinding.curveParentContainer.sideControl,
                mBinding.curveParentContainer.sideControlMaxValue)
                        sideContainerAnimator.addUpdateListener { animator ->
            mBinding.curveParentContainer.setSideYValue(animator.animatedValue as Float)
        }


        var animatorSet = AnimatorSet()
        animatorSet.playTogether(curveAnimator, heightAnimator, sideContainerAnimator)
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                (activity.application as MyApplication)
                        .bus()
                        .send(HIDE_CONTAINER)

                mAnimationList.clear()
                for (view in mViewsList) {
                    mAnimationList.add(AnimationUtil.alpha(view = view, alphaValue =  0f))
                }
                var hideContainerSettingsShortcutViews = AnimationUtil.combineToAnimatorSet(mAnimationList)
                hideContainerSettingsShortcutViews.duration = 200
                hideContainerSettingsShortcutViews.interpolator = AccelerateInterpolator()
                hideContainerSettingsShortcutViews.start()

            }
        })
        animatorSet.start()

        down = true

    }

}