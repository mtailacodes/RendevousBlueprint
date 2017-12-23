package com.mtailacodes.blueprintrendevouz.Util

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.widget.TextView
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 12/20/17.
 */

object LoginActivityAnimationUtil {

    fun shiftContainer(x : Float = 0f, showView : View, hideView : View) : AnimatorSet {
        val mAnimatorSet = AnimatorSet()
        val signInShift = ObjectAnimator.ofFloat(showView, View.TRANSLATION_X, x)
        val signUpShift = ObjectAnimator.ofFloat(hideView, View.TRANSLATION_X, x)
        mAnimatorSet.playTogether(signInShift, signUpShift)
        return mAnimatorSet
    }

    fun animateContainerColors(viewToWhite : View, viewToDark : View, lightColor : Int, darkColor : Int) : AnimatorSet {
        val mAnimatorSet = AnimatorSet()
        val toDark = ValueAnimator.ofObject(ArgbEvaluator(), lightColor, darkColor)
        toDark.addUpdateListener { valueAnimator ->
            var value : Int = valueAnimator.animatedValue as Int
            viewToDark.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
        }
        val toLight = ValueAnimator.ofObject(ArgbEvaluator(),darkColor, lightColor)
        toLight.addUpdateListener { valueAnimator ->
            var value : Int = valueAnimator.animatedValue as Int
            viewToWhite.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
        }
        mAnimatorSet.playTogether(toDark, toLight)
        return mAnimatorSet
    }


    fun animateTextViewTitles(viewToShow : View, alpha1 : Float = 1f, viewToHide : View, alpha0 : Float = 0f) : AnimatorSet {
        val mAnimatorSet = AnimatorSet()
        val alphaTo1 = ObjectAnimator.ofFloat(viewToShow, View.ALPHA, alpha1)
        val alphaTo0 = ObjectAnimator.ofFloat(viewToHide, View.ALPHA, alpha0)
        mAnimatorSet.playTogether(alphaTo1, alphaTo0)
        return AnimatorSet()
    }



}