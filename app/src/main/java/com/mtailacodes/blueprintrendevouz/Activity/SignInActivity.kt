package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.hardware.input.InputManager
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.LoginActivityAnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySignInBinding
import android.content.res.ColorStateList
import java.lang.reflect.AccessibleObject.setAccessible
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher


/**
 * Created by matthewtaila on 12/19/17.
 */

class SignInActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var mBinding: ActivitySignInBinding

    // two container variables
    var signInContainerShown: Boolean = true;
    var signUpContainerShown: Boolean = false;
    var backgroundLight = 0
    var backgroundWDark = 0
    private var editFieldFocus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        setupEditTextListeners()
    }

    private fun setupEditTextListeners() {
        mBinding.etSignInPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("Passed", p0.toString())

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                var emailCredsPassed = android.util.Patterns.EMAIL_ADDRESS.matcher(p0).matches()
//                if (emailCredsPassed){
//                    Log.i("Passed", "passed")
//                    setEditFieldColor(mBinding.signInName, ContextCompat.getColor(baseContext, R.color.green))
//                }
                Log.d("Passed", "hijg")
            }

        })
    }


    override fun onResume() {
        super.onResume()
        backgroundLight = resources.getColor(R.color.showContainer)
        backgroundWDark = resources.getColor(R.color.hideContainer)
    }

    private fun setOnClickListeners() {
        mBinding.clSignUpContainer.setOnClickListener(this)
        mBinding.clSignInContainer.setOnClickListener(this)

        mBinding.etSignInPassword.setOnFocusChangeListener { view, b ->
            if (b){
                mBinding.signInTextView.animate().translationY(-350f).start()
                editFieldFocus = true
            } else {
                mBinding.signInTextView.animate().translationY(350f).start()
                editFieldFocus = false
            }
        }

//        mBinding.etSignInEmail.setOnFocusChangeListener { view, b ->
//            if (b){
//                mBinding.signInTextView.animate().translationY(-350f).start() // todo animate this to correct posiiton with bounce effect
//                editFieldFocus = true
//            } else {
//                mBinding.signInTextView.animate().translationY(350f).start() // todo animate this to correct posiiton with bounce effect
//                editFieldFocus = false
//            }
//        }
    }



    fun setEditFieldColor(textInputLayout: TextInputLayout, @ColorInt color : Int){
        try {
            val fDefaultTextColor = TextInputLayout::class.java.getDeclaredField("mDefaultTextColor")
            fDefaultTextColor.isAccessible = true
            fDefaultTextColor.set(textInputLayout, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color)))

            val fFocusedTextColor = TextInputLayout::class.java.getDeclaredField("mFocusedTextColor")
            fFocusedTextColor.isAccessible = true
            fFocusedTextColor.set(textInputLayout, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.cl_SignInContainer ->{
                if (editFieldFocus){
                    hideKeyBoard()
                }
                if (!signInContainerShown && signUpContainerShown){
                    showSignInContainerAnimation()
                } else return
            }
            R.id.cl_SignUpContainer ->{
                if (editFieldFocus){
                    hideKeyBoard()
                }
                if (signInContainerShown && !signUpContainerShown){
                    showSignUpContainerAnimation()
                } else return
            }
        }
    }

    private fun showSignInContainerAnimation() {
        val translationAnimatorSet = LoginActivityAnimationUtil.shiftContainer(showView = mBinding.clSignInContainer, hideView = mBinding.clSignUpContainer)
        val showMiddleSignInTitle = LoginActivityAnimationUtil.animateTextViewTitles(viewToShow = mBinding.tvMiddleSignUp, viewToHide = mBinding.tvMiddleSignUp)
        val colorAnimaation = LoginActivityAnimationUtil.animateContainerColors(viewToWhite = mBinding.clSignInContainer, viewToDark = mBinding.clSignUpContainer, darkColor = backgroundWDark, lightColor = backgroundLight)

        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.playTogether(showMiddleSignInTitle, translationAnimatorSet, colorAnimaation)
        finalAnimatorSet.duration = 150
        finalAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                signUpContainerShown = false
                signInContainerShown = true
            }
        })
        finalAnimatorSet.start()
    }

    private fun showSignUpContainerAnimation() {
        var calculatedXTranslation  = mBinding.signInGuideline.left - mBinding.signUpGuideline.left
        val translationAnimatorSet = LoginActivityAnimationUtil.shiftContainer(calculatedXTranslation.toFloat(), mBinding.clSignInContainer, mBinding.clSignUpContainer)
        val showMiddleSignUpTitle = LoginActivityAnimationUtil.animateTextViewTitles(viewToShow = mBinding.tvMiddleSignUp, viewToHide = mBinding.signInTextView)
        val colorAnimaation = LoginActivityAnimationUtil.animateContainerColors(viewToWhite = mBinding.clSignUpContainer, viewToDark = mBinding.clSignInContainer, darkColor = backgroundWDark, lightColor = backgroundLight)

        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.playTogether(translationAnimatorSet, showMiddleSignUpTitle, colorAnimaation)
        finalAnimatorSet.duration = 150
        finalAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalAnimatorSet.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                signUpContainerShown = true
                signInContainerShown = false
            }
        })
        finalAnimatorSet.start()
    }

    fun hideKeyBoard(){
        editFieldFocus = false
        mBinding.signInTextView.animate().translationY(0f).start()
        val imm : InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }



}


