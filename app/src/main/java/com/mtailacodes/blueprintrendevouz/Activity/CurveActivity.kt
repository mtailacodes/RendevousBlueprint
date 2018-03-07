package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.databinding.ActivityCurveBinding

/**
 * Created by matthewtaila on 2/24/18.
 */

class CurveActivity: AppCompatActivity(), View.OnClickListener {


    lateinit var mBinding : ActivityCurveBinding
    var down = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_curve)
        mBinding.curveContainer.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.curveContainer ->{
                if (down){
                    pullUp()
                } else {
                    pullDown()
                }
            }
        }
    }

    private fun pullUp() {
        var params = mBinding.curveContainer.layoutParams
        mBinding.curveContainer.layoutParams = params

        val heightAnimator = ValueAnimator.ofInt( 1200, 263)
        heightAnimator.addUpdateListener { animator ->
            params.height = animator.animatedValue as Int
            mBinding.curveContainer.layoutParams = params
        }

        val curveAnimator = ValueAnimator.ofInt( 0, 100)
        curveAnimator.addUpdateListener { animator ->
            mBinding.curveContainer.setBiezerY(animator.animatedValue as Int)
        }

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(curveAnimator, heightAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
//                mBinding.cardview.visibility = View.GONE
            }
        })
        animatorSet.start()
        down = false
    }

    private fun pullDown() {
        var params = mBinding.curveContainer.layoutParams
        var currentHeight = (mBinding.curveContainer.xHeight).toInt()
        mBinding.curveContainer.layoutParams = params

        val heightAnimator = ValueAnimator.ofInt(currentHeight, 1200)
        heightAnimator.addUpdateListener { animator ->
            params.height = animator.animatedValue as Int
            mBinding.curveContainer.layoutParams = params
        }

        val curveAnimator = ValueAnimator.ofInt(mBinding.curveContainer.biezerYValue, 0)
        curveAnimator.addUpdateListener { animator ->
            mBinding.curveContainer.setBiezerY(animator.animatedValue as Int)
        }

        var animatorSet = AnimatorSet()
        animatorSet.playTogether(curveAnimator, heightAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
//                mBinding.cardview.visibility = View.VISIBLE
            }
        })
        animatorSet.start()

        down = true

    }
}

