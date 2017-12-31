package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.databinding.adapters.NumberPickerBindingAdapter.setValue
import android.location.Location
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.Util.LoginActivityAnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySignInBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by matthewtaila on 12/19/17.
 */

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    // tags
    private final var TAG_EMAIL = "Email Address"
    private final var TAG_PASSWORD = "Password"
    private var ACTIVE_USERS = "Active Users"
    private final var CREATE_USER_FAILED = "Create User Failed"
    lateinit var mBinding: ActivitySignInBinding

    // two container variables
    var signInContainerShown: Boolean = true;
    var signUpContainerShown: Boolean = false;
    var backgroundLight = 0
    var backgroundWDark = 0
    private var editFieldFocus = false
    private var emailInputPassed = false
    private var passwordInputPassed = false
    private var createUserEmailPassed = false
    private var createUserPasswordPassed = false

    var emailLineCurrentColor  = R.color.black100
    var loginButtonHandled = false
    var signUpButtonHandled = false

    // firebase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        setEditFieldColor(mBinding.signInName, resources.getColor(R.color.black100), mBinding.emailLine)
        setEditFieldColor(mBinding.signInLayout, resources.getColor(R.color.black100), mBinding.passwordLine)
        setEditFieldColor(mBinding.createUserEmailInputContainer, resources.getColor(R.color.black100), mBinding.passwordLine)
        setEditFieldColor(mBinding.createUserPasswordInputContainer, resources.getColor(R.color.black100), mBinding.passwordLine)
        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        setupEmailAddressFieldListener()
        setupPasswordFieldListener()
        setupCreateUserEmailInputListener()
        setupCreateUserPasswordInputListener()
    }

    private fun setupCreateUserPasswordInputListener() {
        mBinding.etCreateUserPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()){
                    signUpButtonHandled = false
                    setEditFieldColor(mBinding.createUserPasswordInputContainer, resources.getColor(R.color.failedRed), mBinding.createUserPasswordLine)
                } else {
                    if (p0.toString().length >= 6){
                        createUserPasswordPassed = true
                        setEditFieldColor(mBinding.createUserPasswordInputContainer, resources.getColor(R.color.green), mBinding.createUserPasswordLine)
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    } else if (p0.toString().isNotEmpty() && p0.toString().length < 6){
                        createUserPasswordPassed = false
                        setEditFieldColor(mBinding.createUserPasswordInputContainer, resources.getColor(R.color.failedRed), mBinding.createUserPasswordLine)
                        signUpButtonHandled = false
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun setupCreateUserEmailInputListener() {
        mBinding.etCreateUserEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isEmpty()) {
                    setEditFieldColor(mBinding.createUserEmailInputContainer, resources.getColor(R.color.failedRed), mBinding.createUserEmailLine)
                    signUpButtonHandled = false
                } else {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                        createUserEmailPassed = true
                        setEditFieldColor(mBinding.createUserEmailInputContainer, resources.getColor(R.color.green), mBinding.createUserEmailLine)
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    } else {
                        createUserEmailPassed = false
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 != p2) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                        createUserEmailPassed = true
                        setEditFieldColor(mBinding.createUserEmailInputContainer, resources.getColor(R.color.green), mBinding.createUserEmailLine)
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    } else {
                        signUpButtonHandled = false
                        createUserEmailPassed = false
                        setEditFieldColor(mBinding.createUserEmailInputContainer, resources.getColor(R.color.failedRed), mBinding.createUserEmailLine)
                        checkCreateUserInput(createUserEmailPassed, createUserPasswordPassed)
                    }
                }
            }
        })
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

    private fun checkCreateUserInput(createUserEmailPassed: Boolean, createUserPasswordPassed: Boolean) {
        var buttonEnabledColor  = resources.getColor(R.color.green)
        var buttonDisabledColor  = resources.getColor(R.color.showContainer)

        var disabledTextColor  = resources.getColor(R.color.black100)
        var enabledTextColor  = resources.getColor(R.color.showContainer)

        if (createUserEmailPassed && createUserPasswordPassed){
            var mAnimator = LoginActivityAnimationUtil.animateLoginButton(
                    mBinding.createUserContainer, buttonDisabledColor, buttonEnabledColor,
                    mBinding.tvMiddleSignUp, disabledTextColor, enabledTextColor, 600)
            if (!signUpButtonHandled){
                mBinding.createUserContainer.isEnabled
                mAnimator.start()
            }
            signUpButtonHandled = true
        } else {
            var mAnimator = LoginActivityAnimationUtil.animateLoginButton(
                    mBinding.createUserContainer, buttonEnabledColor, buttonDisabledColor,
                    mBinding.tvMiddleSignUp, enabledTextColor, disabledTextColor)
            mAnimator.start()
            mBinding.createUserContainer.isEnabled = false
        }
    }

    fun checkLogInInputs (emailInput : Boolean, passwordInput : Boolean){

        var buttonEnabledColor  = resources.getColor(R.color.green)
        var buttonDisabledColor  = resources.getColor(R.color.showContainer)

        var disabledTextColor  = resources.getColor(R.color.black100)
        var enabledTextColor  = resources.getColor(R.color.showContainer)

        if (emailInput && passwordInput){
            mBinding.tvMiddleSignUp.isEnabled
            var mAnimator = LoginActivityAnimationUtil.animateLoginButton(
                    mBinding.loginContainer, buttonDisabledColor, buttonEnabledColor,
                    mBinding.signInTextView, disabledTextColor, enabledTextColor, 600)
            if (!loginButtonHandled){
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
        mBinding.tvMiddleSignUp.setOnClickListener(this)

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

        mBinding.etCreateUserEmailAddress.setOnFocusChangeListener { _, b ->
            val translateLogInButtonValue = (mBinding.createUserContainer.top - mBinding.createUserPasswordLine.bottom) - mBinding.createUserContainer.height/2
            if (b){
                LoginActivityAnimationUtil.translateLoginButton(mBinding.createUserContainer, -(translateLogInButtonValue.toFloat()))
                editFieldFocus = true
            } else {
                LoginActivityAnimationUtil.translateLoginButton(mBinding.createUserContainer, 0f)
                editFieldFocus = false
            }
        }

        mBinding.etCreateUserPassword.setOnFocusChangeListener { _, b ->
            val translateLogInButtonValue = (mBinding.createUserContainer.top - mBinding.createUserPasswordLine.bottom) - mBinding.createUserContainer.height/2
            if (b){
                LoginActivityAnimationUtil.translateLoginButton(mBinding.createUserContainer, -(translateLogInButtonValue.toFloat()))
                editFieldFocus = true
            } else {
                LoginActivityAnimationUtil.translateLoginButton(mBinding.createUserContainer, 0f)
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
            R.id.tv_MiddleSignUp ->{
                createUserFromEmailAndPassword()
            }
        }
    }

    private fun createUserFromEmailAndPassword() {
        val testTom = RxUserUtil()
        val single = testTom.createUserWithEmailAndPassword(email = mBinding.etCreateUserEmailAddress.text.toString(),
                password = mBinding.etCreateUserPassword.text.toString())
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleCreateUserSuccess()
                }, { throwable ->
                    Log.d("SignInActivity", "Create user failed: ${throwable.message}")
                })

    }

    private fun handleCreateUserSuccess() {
        var mNewUser = RendevouzUserModel()
        var mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mNewUser.UuID = mFirebaseUser!!.uid
        mNewUser.emailAddress = mBinding.etCreateUserEmailAddress.text.toString()
        // todo - need to include username too

        val mFirebaseBaseDatabase = FirebaseDatabase.getInstance()
        val mFirebaseReference = mFirebaseBaseDatabase.reference.child(ACTIVE_USERS).
                push()
        val pushID = mFirebaseReference.key
        mNewUser.pushID= pushID

        mFirebaseBaseDatabase.reference.child(ACTIVE_USERS).child(pushID).
                setValue(mNewUser.UuID).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                val intent =  Intent(this, MapSearchActivity::class.java)
                intent.putExtra(Constants.RENDEVOUZ_USER_MODEL_BUNDLE, mNewUser)
                startActivity(intent)
            } else {
            }
        }
    }

    private fun createUser() {
        var emailAddress = mBinding.etCreateUserEmailAddress.text.toString()
        var password = mBinding.etCreateUserPassword.text.toString()

        var mNewUser = RendevouzUserModel()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener {
            task: Task<AuthResult> ->
            if (task.isSuccessful){
                var mFirebaseUser = FirebaseAuth.getInstance().currentUser
                mNewUser.UuID = mFirebaseUser!!.uid
                mNewUser.emailAddress = emailAddress

                // todo start new activity
            } else {
                Toast.makeText(applicationContext, "Create user failed", Toast.LENGTH_SHORT).show()
                Log.d(CREATE_USER_FAILED, task.exception.toString())
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


