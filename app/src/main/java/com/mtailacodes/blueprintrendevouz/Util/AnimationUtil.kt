package com.mtailacodes.blueprintrendevouz.Util

import android.animation.*
import android.graphics.Color
import android.provider.ContactsContract
import android.renderscript.Sampler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.TypedValue
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mtailacodes.blueprintrendevouz.Activity.OnBoardingActivity
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.customViews.OnBoardingStateIndicator
import com.mtailacodes.blueprintrendevouz.customViews.SelectGenderImageView
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel

/**
 * Created by matthewtaila on 12/20/17.
 */

object AnimationUtil {

    var GLOBAL_USERS = "Global Users"

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

    fun translateLoginButton (loginButton : View, translationYValue : Float){

        val translateAnimation = ObjectAnimator.ofFloat(loginButton, View.TRANSLATION_Y, translationYValue)
        translateAnimation.duration = 220
        translateAnimation.interpolator = AccelerateDecelerateInterpolator()
        translateAnimation.start()

    }

    fun animateLoginButton(backgroundView: CardView, startingBackgroundColor: Int,
                                 endingBackgroundColor: Int, textView: TextView,
                                 startingTextColor: Int, endingTextColor: Int, duration: Int = 0) : AnimatorSet{

        var mAnimatorSet = AnimatorSet()

        val cvBackgroundColorChange= ValueAnimator.ofObject(ArgbEvaluator(), startingBackgroundColor, endingBackgroundColor)
        cvBackgroundColorChange.addUpdateListener { valueAnimator ->
            var value = valueAnimator.animatedValue as Int
            backgroundView.setCardBackgroundColor(value)
        }

        val textViewColorChange = ValueAnimator.ofObject(ArgbEvaluator(), startingTextColor, endingTextColor)
        textViewColorChange.addUpdateListener { valueAnimator ->
            var value = valueAnimator.animatedValue as Int
            textView.setTextColor(value)
        }

        mAnimatorSet.playTogether(cvBackgroundColorChange, textViewColorChange)
        mAnimatorSet.duration = duration.toLong()
        mAnimatorSet.interpolator = AccelerateInterpolator()

        return mAnimatorSet
    }

    fun handleCaptureImageCardview(cardView : CardView, finalValue : Float = 1f, duration: Long = 450): AnimatorSet {

        var animatorSet = AnimatorSet()

        var scaleX = ObjectAnimator.ofFloat(cardView, View.SCALE_X, 0.2f, 1f)
        var scaleY = ObjectAnimator.ofFloat(cardView, View.SCALE_Y, 0.2f, 1f)
        var alpha = ObjectAnimator.ofFloat(cardView, View.ALPHA, 0f, 1f)

        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.duration = duration
        animatorSet.interpolator = OvershootInterpolator(1.4f)

        return animatorSet
    }

    fun translateY (view : View,
                    translationYValue : Float = 0f,
                    interpolator: Interpolator = DecelerateInterpolator(),
                    duration: Long = 500,
                    startDelay: Long = 0): ObjectAnimator{

        var objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, translationYValue)
        objectAnimator.duration = duration
        objectAnimator.interpolator = interpolator
        objectAnimator.startDelay = startDelay

        return objectAnimator
    }

    fun scaleY (view : View,
                      heightToValue : Float = 0f,
                      interpolator: Interpolator = DecelerateInterpolator(),
                      duration: Long = 500,
                      startDelay : Long = 0, pivot: Float = 0f): ObjectAnimator{
        view.pivotY = pivot
        var valueAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, heightToValue)
        valueAnimator.duration = duration
        valueAnimator.startDelay = startDelay
        valueAnimator.interpolator = interpolator

        return valueAnimator
    }

    fun scaleX (view : View,
                heightToValue : Float = 0f,
                interpolator: Interpolator = DecelerateInterpolator(),
                duration: Long = 500,
                startDelay : Long = 0, pivot: Float = 0f): ObjectAnimator{
        view.pivotY = pivot
        var valueAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, heightToValue)
        valueAnimator.duration = duration
        valueAnimator.startDelay = startDelay
        valueAnimator.interpolator = interpolator

        return valueAnimator
    }

    fun alpha (view : View,
                    alphaValue : Float,
                    interpolator: Interpolator = DecelerateInterpolator(),
                    duration: Long = 300,
                    startDelay: Long = 0): ObjectAnimator{

        var objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, alphaValue)
        objectAnimator.duration = duration
        objectAnimator.interpolator = interpolator
        objectAnimator.startDelay = startDelay

        return objectAnimator
    }

    fun combineToAnimatorSet (mAnimatorList : ArrayList<Animator>): AnimatorSet{
        var animatorSet = AnimatorSet()
        for (item in mAnimatorList) {
            animatorSet.play(item)
        }
        return animatorSet
    }

    fun scaleSearchSettingsTextField(selectedView : TextView, unselectedView: TextView,
                                     selectedColor: Int, unselectedColor: Int, firstSelection: Boolean): AnimatorSet {

        var mAnimatorSet = AnimatorSet()

        var enlargeSelectedView = ValueAnimator.ofFloat(14f, 16f)
        enlargeSelectedView.addUpdateListener{ valueAnimator ->
            var value = valueAnimator.animatedValue as Float
            selectedView.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }

        var deselectedView = ValueAnimator.ofFloat(16f, 14f)
        deselectedView.addUpdateListener{ valueAnimator ->
            var value = valueAnimator.animatedValue as Float
            unselectedView.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }

        var changeSelectedColor = ValueAnimator.ofArgb(unselectedColor, selectedColor)
        changeSelectedColor.addUpdateListener { valueAnimator ->
            var value : Int = valueAnimator.animatedValue as Int
            selectedView.setTextColor(value)
        }

        var changeUnselectedColor = ValueAnimator.ofArgb(selectedColor, unselectedColor)
        changeUnselectedColor.addUpdateListener { valueAnimator ->
            var value : Int = valueAnimator.animatedValue as Int
            unselectedView.setTextColor(value)
        }

        enlargeSelectedView.duration = 300
        enlargeSelectedView.interpolator = OvershootInterpolator(1.5f)
        deselectedView.duration = 0
        deselectedView.interpolator = AccelerateInterpolator()

        if (firstSelection){
            mAnimatorSet.playTogether(enlargeSelectedView, changeSelectedColor)
        } else {
            mAnimatorSet.playTogether(enlargeSelectedView, deselectedView, changeSelectedColor, changeUnselectedColor)
        }
        return mAnimatorSet
    }



    // map search activity specific animations

    fun slideInAnimator(view: View, from: Float, to: Float) : ValueAnimator {
        val mOnEnterSlideInAnimator = ValueAnimator.ofFloat(from, to)
        mOnEnterSlideInAnimator.addUpdateListener {
            animator -> view.translationY = (animator.animatedValue as Float) * view.measuredHeight
        }
        mOnEnterSlideInAnimator.duration = 600
        mOnEnterSlideInAnimator.interpolator = AccelerateDecelerateInterpolator()
        return mOnEnterSlideInAnimator
    }

    fun picturePreviewSlideInAnimator(view: View, measuredHeight: Int): ValueAnimator {

        val mPicturePreviewAnimator = ValueAnimator.ofFloat(-1f, 0f)
        mPicturePreviewAnimator.addUpdateListener {
            animator -> view.translationY = (animator.animatedValue as Float) * measuredHeight
        }
        mPicturePreviewAnimator.duration = 650
        mPicturePreviewAnimator.interpolator = AccelerateDecelerateInterpolator()
        return mPicturePreviewAnimator
    }

    fun onBoardingStaggered (viewList : ArrayList<View>, standardHeight : Int, startDelay: Long) : AnimatorSet {

        var mAnimatorSet = AnimatorSet()

        for (v in viewList){
            val animator = ValueAnimator.ofFloat(-0.25f, 0f)
            animator.addUpdateListener { animator -> v.translationY = (animator.animatedValue as Float) * standardHeight}
            val alphaAnimator = AnimationUtil.alpha(v, 1f)
            mAnimatorSet.play(animator)
            mAnimatorSet.play(alphaAnimator)
        }

        mAnimatorSet.startDelay = startDelay

        return mAnimatorSet
    }

    fun translateYRelativeToHeightAnimator (view : View,
                                            from : Float = -1f,
                                            to : Float = 0f,
                                            interpolator: Interpolator = DecelerateInterpolator(),
                                            duration: Long = 300,
                                            startDelay: Long = 0): AnimatorSet {

        var mAnimatorSet = AnimatorSet()

        var translateYAnimator = ValueAnimator.ofFloat( from, to)
        translateYAnimator.addUpdateListener { animator ->
            view.translationY = (animator.animatedValue as Float) *  view.height
        }

        view.elevation = 1f
        mAnimatorSet.play(translateYAnimator)

        var alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)

        mAnimatorSet.play(alphaAnimator)

        mAnimatorSet.duration = duration
        mAnimatorSet.startDelay = startDelay
        mAnimatorSet.interpolator = interpolator
        return mAnimatorSet
    }

    fun stateIndicatorAnimator (selectedView : OnBoardingStateIndicator,
                                selectedValue : Int,
                                interpolator: Interpolator = DecelerateInterpolator(),
                                duration: Long = 300,
                                startDelay: Long = 0) : AnimatorSet {

        var mAnimatorSet = AnimatorSet()

        when (selectedValue){
            0 -> {
                selectedView.stepOnePaint.color = Color.parseColor("#c0392b")
                var selectState = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault/2)
                selectState.addUpdateListener { animator ->
                    selectedView.selectStepOne(animator.animatedValue as Float)
                }
                mAnimatorSet.play(selectState)

                selectedView.stepTwoPaint.color = Color.parseColor("#999999")
                var deselectState2 = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault)
                deselectState2.addUpdateListener { animator ->
                    selectedView.selectStepTwo(animator.animatedValue as Float)
                }
                mAnimatorSet.play(deselectState2)

                selectedView.stepThreePaint.color = Color.parseColor("#999999")
                var deselectState3 = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault)
                deselectState3.addUpdateListener { animator ->
                    selectedView.selectStepThree(animator.animatedValue as Float)
                }
                mAnimatorSet.play(deselectState3)
            }
            1 ->{
                selectedView.stepOnePaint.color = Color.parseColor("#999999")
                var selectState = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault)
                selectState.addUpdateListener { animator ->
                    selectedView.selectStepOne(animator.animatedValue as Float)
                }
                mAnimatorSet.play(selectState)

                selectedView.stepTwoPaint.color = Color.parseColor("#BF55EC")
                var deselectState2 = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault/2)
                deselectState2.addUpdateListener { animator ->
                    selectedView.selectStepTwo(animator.animatedValue as Float)
                }
                mAnimatorSet.play(deselectState2)

                selectedView.stepThreePaint.color = Color.parseColor("#999999")
                var deselectState3 = ValueAnimator.ofFloat(selectedView.heightDefault, selectedView.heightDefault)
                deselectState3.addUpdateListener { animator ->
                    selectedView.selectStepThree(animator.animatedValue as Float)
                }
                mAnimatorSet.play(deselectState3)
            }
        }

        mAnimatorSet.duration = duration
        mAnimatorSet.startDelay = startDelay
        mAnimatorSet.interpolator = interpolator
        return mAnimatorSet
    }

    fun nextOnBoarding(viewList : ArrayList<View>) : AnimatorSet {
        var mAnimatorSet = AnimatorSet()
        for (view in viewList){
            var mValueAnimator = ValueAnimator.ofFloat(0f, -20f)
            mValueAnimator.addUpdateListener { animator ->
                view.translationX = view.left * animator.animatedValue as Float
            }
            view.elevation = 1f
            mValueAnimator.startDelay = (viewList.indexOf(view) * 15).toLong()
            mAnimatorSet.play(mValueAnimator)
        }
        mAnimatorSet.duration = 300
        mAnimatorSet.interpolator = AccelerateInterpolator()
        return mAnimatorSet
    }

    fun backOnBoarding(viewList : ArrayList<View>) : AnimatorSet {
        var mAnimatorSet = AnimatorSet()
        for (view in viewList){
            var mValueAnimator = ValueAnimator.ofFloat(0f, +20f)
            mValueAnimator.addUpdateListener { animator ->
                view.translationX = view.right * animator.animatedValue as Float
            }
            mValueAnimator.startDelay = (viewList.indexOf(view) * 15).toLong()
            mAnimatorSet.play(mValueAnimator)
            view.elevation = 0f
        }
        mAnimatorSet.duration = 300
        mAnimatorSet.interpolator = AccelerateInterpolator()
        return mAnimatorSet
    }

    fun nextButtonColorAnimator(textView: TextView,
                                passed : Boolean) : ValueAnimator {

        var endColor = Color.parseColor("#DE000000")
        var startColor = Color.parseColor("#DE000000")

        if (passed) endColor = Color.parseColor("#e74c3c")

        var mAnimator = ValueAnimator.ofArgb(startColor, endColor)
            mAnimator.addUpdateListener { animator ->
            textView.setTextColor(animator.animatedValue as Int)
        }
        mAnimator.duration = 300
        mAnimator.interpolator = AccelerateDecelerateInterpolator()

        return mAnimator
    }

    fun resetAnimationPosition(viewList : ArrayList<View>){
        lateinit var objectAnimator : ObjectAnimator
        for (view in viewList){
            objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f)
            objectAnimator.duration = 0
            objectAnimator.start()

            objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
            objectAnimator.duration = 0
            objectAnimator.start()

            view.elevation = 0f
        }
    }

    // select gender animation
    fun selectGender (mUser : RendevouzUserModel,
                      maleImageView : ImageView,
                      femaleImageView : ImageView) : AnimatorSet{

        var mAnimatorSet = AnimatorSet()

        when (mUser.gender){
            "defaultUser" ->{

            }
            OnBoardingActivity().MALE_GENDER ->{

            }
            OnBoardingActivity().FEMALE_GENDER ->{

            }


        }

    }

    fun genderSelectionAnimator (genderView : ImageView,
                                 sleected : Boolean) : AnimatorSet {

    }




}