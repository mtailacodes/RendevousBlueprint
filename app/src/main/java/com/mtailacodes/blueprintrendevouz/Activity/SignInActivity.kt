package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.LoginActivityAnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySignInBinding
import android.content.res.ColorStateList
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AccelerateInterpolator

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
    private var emailInputPassed = false
    private var passwordInputPassed = false
    var emailLineCurrentColor  = R.color.black100
    var loginButtonHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        setEditFieldColor(mBinding.signInName, resources.getColor(R.color.black100), mBinding.emailLine)
        setEditFieldColor(mBinding.signInLayout, resources.getColor(R.color.black100), mBinding.passwordLine)

        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        setupEmailAddressFieldListener()
        setupPasswordFieldListener()
    }

    private fun setupPasswordFieldListener() {
        mBinding.etSignInPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()){
                    loginButtonHandled = false
                    setEditFieldColor(mBinding.signInLayout, resources.getColor(R.color.failedRed), mBinding.passwordLine)
                } else {
                    if (p0.toString().length >= 6){
                        passwordInputPassed = true
                        setEditFieldColor(mBinding.signInLayout, resources.getColor(R.color.green), mBinding.passwordLine)
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    } else if (p0.toString().isNotEmpty() && p0.toString().length < 6){
                        setEditFieldColor(mBinding.signInLayout, resources.getColor(R.color.failedRed), mBinding.passwordLine)
                        loginButtonHandled = false
                        passwordInputPassed = false
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun setupEmailAddressFieldListener() {
        mBinding.etSignInEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {
                    setEditFieldColor(mBinding.signInName, resources.getColor(R.color.failedRed), mBinding.emailLine)
                    loginButtonHandled = false
                } else {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                        emailInputPassed = true
                        setEditFieldColor(mBinding.signInName, resources.getColor(R.color.green), mBinding.emailLine)
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    } else {
                        emailInputPassed = false
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 != p2) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                        emailInputPassed = true
                        setEditFieldColor(mBinding.signInName, resources.getColor(R.color.green), mBinding.emailLine)
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    } else {
                        loginButtonHandled = false
                        emailInputPassed = false
                        setEditFieldColor(mBinding.signInName, resources.getColor(R.color.failedRed), mBinding.emailLine)
                        checkLogInInputs(emailInputPassed, passwordInputPassed)
                    }
                }
            }
        })
    }

    private fun setInputChangeLineColor(lineView : View, @ColorInt color : Int){

        val changeColor = ObjectAnimator.ofArgb(emailLineCurrentColor, color)
        changeColor.addUpdateListener { valueAnimator ->
            var value : Int = valueAnimator.animatedValue as Int
            lineView.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
        }
        changeColor.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                emailLineCurrentColor = color
            }
        })
        changeColor.duration = 150
        changeColor.interpolator = AccelerateInterpolator()
        changeColor.start()
    }

    fun checkLogInInputs (emailInput : Boolean, passwordInput : Boolean){

        var buttonEnabledColor  = resources.getColor(R.color.green)
        var buttonDisabledColor  = resources.getColor(R.color.showContainer)

        var disabledTextColor  = resources.getColor(R.color.black100)
        var enabledTextColor  = resources.getColor(R.color.showContainer)

        if (emailInput && passwordInput){
            var mAnimator = LoginActivityAnimationUtil.animateLoginButton(
                    mBinding.loginContainer, buttonDisabledColor, buttonEnabledColor,
                    mBinding.signInTextView, disabledTextColor, enabledTextColor, 600)
            if (!loginButtonHandled){
                mBinding.loginContainer.isEnabled
                mAnimator.start()
            }

            loginButtonHandled = true
        } else {
            var mAnimator = LoginActivityAnimationUtil.animateLoginButton(
                    mBinding.loginContainer, buttonEnabledColor, buttonDisabledColor,
                    mBinding.signInTextView, enabledTextColor, disabledTextColor)
            mAnimator.start()
            mBinding.loginContainer.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundLight = resources.getColor(R.color.showContainer)
        backgroundWDark = resources.getColor(R.color.hideContainer)
    }

    private fun setOnClickListeners() {
        mBinding.clSignUpContainer.setOnClickListener(this)
        mBinding.clSignInContainer.setOnClickListener(this)
        mBinding.loginContainer.setOnClickListener(this)

        // todo - for the SignintextView animation - include the spring animation
        mBinding.etSignInPassword.setOnFocusChangeListener { _, b ->
            val translateLogInButtonValue = (mBinding.loginContainer.top - mBinding.passwordLine.bottom) - mBinding.loginContainer.height/2
            if (b){
                LoginActivityAnimationUtil.translateLoginButton(mBinding.loginContainer, -(translateLogInButtonValue.toFloat()))
                editFieldFocus = true
            } else {
                LoginActivityAnimationUtil.translateLoginButton(mBinding.loginContainer, 0f)
                editFieldFocus = false
            }
        }

        mBinding.etSignInEmail.setOnFocusChangeListener { _, b ->
            val translateLogInButtonValue = (mBinding.loginContainer.top - mBinding.passwordLine.bottom) - mBinding.loginContainer.height/2
            if (b){
                LoginActivityAnimationUtil.translateLoginButton(mBinding.loginContainer, -(translateLogInButtonValue.toFloat()))
                editFieldFocus = true
            } else {
                LoginActivityAnimationUtil.translateLoginButton(mBinding.loginContainer, 0f)
                editFieldFocus = false
            }
        }

    }

    fun setEditFieldColor(textInputLayout: TextInputLayout, @ColorInt color : Int, viewLine : View){
        try {
            val fDefaultTextColor = TextInputLayout::class.java.getDeclaredField("mDefaultTextColor")
            fDefaultTextColor.isAccessible = true
            fDefaultTextColor.set(textInputLayout, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color)))

            val fFocusedTextColor = TextInputLayout::class.java.getDeclaredField("mFocusedTextColor")
            fFocusedTextColor.isAccessible = true
            fFocusedTextColor.set(textInputLayout, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(color)))
            setInputChangeLineColor(viewLine, color)
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
        val translationAnimatorSet = LoginActivityAnimationUtil.shiftContainer(
                showView = mBinding.clSignInContainer, hideView = mBinding.clSignUpContainer)
        val showMiddleSignInTitle = LoginActivityAnimationUtil.animateTextViewTitles(
                viewToShow = mBinding.tvMiddleSignUp, viewToHide = mBinding.tvMiddleSignUp)
        val colorAnimaation = LoginActivityAnimationUtil.animateContainerColors(
                viewToWhite = mBinding.clSignInContainer, viewToDark = mBinding.clSignUpContainer,
                darkColor = backgroundWDark, lightColor = backgroundLight)

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

    private fun hideKeyBoard(){
        editFieldFocus = false
        mBinding.signInTextView.animate().translationY(0f).start()
        val imm : InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

}


