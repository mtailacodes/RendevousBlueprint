package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import com.bumptech.glide.Glide
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.customViews.DatePickerFragment
import com.mtailacodes.blueprintrendevouz.databinding.ActivityOnboardingBinding
import com.mtailacodes.blueprintrendevouz.interfaces.OnBoardingListener
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import java.util.*

/**
 * Created by matthewtaila on 3/14/18.
 */
class OnBoardingActivity : AppCompatActivity(), View.OnClickListener,
        DatePickerFragment.EditNameDialogListener, OnBoardingListener{

    lateinit var mBinding : ActivityOnboardingBinding
    lateinit var mUser : RendevouzUserModel
    lateinit var mUserSearchSettings : UserSearchSettings
    var onBoardingStep : Int = 0
    var nextButtonAnimationHandled = false
    var stateIndicatorAnimationHandled = false

    val FEMALE_GENDER = "Female"
    val MALE_GENDER = "Male"
    var birthdateDialogueHandled = false

    var mPersonalDetailsViewList : ArrayList<View> = ArrayList()

    // custom transition activity variables
    var mLeftDelta : Int = 0
    var mTopDelta : Int = 0
    var mWidthScale : Float = 0f
    var mHeightScale : Float = 0f

    // credential constants
    val Non_Gender = 101
    val No_DOB_Set = 102
    val Too_Young = 103
    val No_Username = 104
    val personalDetailSuccess = 105


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        mUserSearchSettings = UserSearchSettings()

        // get extras from bundle to:
        // 1. instantiate user object
        val bundle = intent.extras
        mUser = bundle.getParcelable("mUser")
        // 2. handle transition animation
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

        var cornerRadius = ValueAnimator.ofFloat( 1f)
        cornerRadius.addUpdateListener { animator -> container.radius = animator.animatedValue as Float }

        var mAnimationList : ArrayList<Animator> = ArrayList()
        mAnimationList.add(ObjectAnimator.ofFloat(container, View.SCALE_Y, 1f))
        mAnimationList.add(ObjectAnimator.ofFloat(container, View.SCALE_X, 1f))
        mAnimationList.add(ObjectAnimator.ofFloat(container, View.TRANSLATION_X, 0f))
        mAnimationList.add(ObjectAnimator.ofFloat(container, View.TRANSLATION_Y, 0f))
        mAnimationList.add(ObjectAnimator.ofFloat(container, View.ALPHA, 1f, 1f))
        mAnimationList.add(cornerRadius)

        var finalTransitionAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        finalTransitionAnimatorSet.duration = 200
        finalTransitionAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        finalTransitionAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animatePersonalDetailsContent()
            }
        })
        finalTransitionAnimatorSet.start()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.iv_femaleSelection ->{
                selectGender(FEMALE_GENDER)
            }
            R.id.iv_maleSelection ->{
                selectGender(MALE_GENDER)
            }
            R.id.tv_next ->{
                when (onBoardingStep){
                    0 ->{
                        if (checkPersonalDetailCredentials() == personalDetailSuccess){
                            onBoardingStep =+1
                            updateOnBoardingStep(onBoardingStep)
                        } else handleError(checkPersonalDetailCredentials())
                    }
                    1 ->{
                        // todo check search settings credentials
                    }
                }

            }
            R.id.tv_DOB ->{
                if (!birthdateDialogueHandled){
                    DatePickerFragment(mBinding.tvDOB, mUser, this).show(fragmentManager, "DatePicker")
                    birthdateDialogueHandled = true
                }
            }
            R.id.tv_back ->{
                onBoardingStep -= 1
                updateOnBoardingStep(onBoardingStep)
            }
            R.id.iv_femaleSearchSetting ->{
                selectSearchSettingsGender(FEMALE_GENDER)
            }
            R.id.iv_maleSearchSetting ->{
                selectSearchSettingsGender(MALE_GENDER)
            }
        }
    }

    private fun selectSearchSettingsGender(gender: String) {
        var genderSelectionAnimatorSet = AnimatorSet()

        when (gender){
            MALE_GENDER ->{
                if (!mBinding.ivMaleSearchSetting.viewSelected){

                    mBinding.ivMaleSearchSetting.viewSelected = true

                    val elevationAnimator = ValueAnimator.ofFloat(1f, 10f)
                    elevationAnimator.addUpdateListener { animator ->
                        mBinding.ivMaleSearchSetting.elevation = animator.animatedValue as Float
                    }
                    elevationAnimator.duration = 300
                    elevationAnimator.interpolator = OvershootInterpolator(12f)
                    genderSelectionAnimatorSet.play(elevationAnimator)

                    if (mBinding.ivFemaleSearchSetting.viewSelected){
                        val elevationAnimator = ValueAnimator.ofFloat(10f, 1f)
                        elevationAnimator.addUpdateListener { animator ->
                            mBinding.ivFemaleSearchSetting.elevation = animator.animatedValue as Float
                        }
                        elevationAnimator.duration = 300
                        elevationAnimator.interpolator = AccelerateInterpolator()
                        genderSelectionAnimatorSet.play(elevationAnimator)
                        mBinding.ivFemaleSearchSetting.viewSelected = false
                    }

                    genderSelectionAnimatorSet.start()
                    mUserSearchSettings.gender = MALE_GENDER
                }
            }
            FEMALE_GENDER ->{

                if (!mBinding.ivFemaleSearchSetting.viewSelected){

                    mBinding.ivFemaleSearchSetting.viewSelected = true
                    val elevationAnimator = ValueAnimator.ofFloat(1f, 10f)
                    elevationAnimator.addUpdateListener { animator ->
                        mBinding.ivFemaleSearchSetting.elevation = animator.animatedValue as Float
                    }
                    elevationAnimator.duration = 300
                    elevationAnimator.interpolator = OvershootInterpolator(12f)
                    genderSelectionAnimatorSet.play(elevationAnimator)

                    if (mBinding.ivMaleSearchSetting.viewSelected){
                        val elevationAnimator = ValueAnimator.ofFloat(10f, 1f)
                        elevationAnimator.addUpdateListener { animator ->
                            mBinding.ivMaleSearchSetting.elevation = animator.animatedValue as Float
                        }
                        elevationAnimator.duration = 300
                        elevationAnimator.interpolator = AccelerateInterpolator()
                        genderSelectionAnimatorSet.play(elevationAnimator)
                        mBinding.ivMaleSearchSetting.viewSelected = false
                    }

                    genderSelectionAnimatorSet.start()
                    mUserSearchSettings.gender = FEMALE_GENDER
                }
            }
        }
    }

    private fun updateOnBoardingStep(onBoardingStep: Int) {
        when (onBoardingStep){
            0 ->{
                mPersonalDetailsViewList.clear()

                mPersonalDetailsViewList.add(mBinding.tvSearchSettingTitle)
                mPersonalDetailsViewList.add(mBinding.tvSearchSettingIntro)
                mPersonalDetailsViewList.add(mBinding.tvTheirGenderTitle)
                mPersonalDetailsViewList.add(mBinding.ivMaleSearchSetting)
                mPersonalDetailsViewList.add(mBinding.ivFemaleSearchSetting)
                mPersonalDetailsViewList.add(mBinding.ageRangeBar)
                mPersonalDetailsViewList.add(mBinding.clAgeRangeValueContainer)
                mPersonalDetailsViewList.add(mBinding.tvAgeRangeTitle)
                mPersonalDetailsViewList.add(mBinding.vAgeRangeBackground)

                var animatorSet = AnimationUtil.backOnBoarding(mPersonalDetailsViewList)

                animatorSet.play(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvBack, from = 0f, to = 5f))

                animatorSet.play(AnimationUtil.stateIndicatorAnimator(mBinding.stateIndicator, onBoardingStep))

                animatorSet.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        AnimationUtil.resetAnimationPosition(mPersonalDetailsViewList)
                        animatePersonalDetailsContent()
                    }
                })
                animatorSet.start()
            }
            1 ->{ setPersonalDetails() }
        }
    }

    private fun setPersonalDetails() {
        mPersonalDetailsViewList.clear()

        mPersonalDetailsViewList.add(mBinding.tvPersonalDetailsTitle)
        mPersonalDetailsViewList.add(mBinding.tvPersonalDetailsIntro)
        mPersonalDetailsViewList.add(mBinding.tvGenderTitle)
        mPersonalDetailsViewList.add(mBinding.ivMaleSelection)
        mPersonalDetailsViewList.add(mBinding.ivFemaleSelection)
        mPersonalDetailsViewList.add(mBinding.tvUsernameTitle)
        mPersonalDetailsViewList.add(mBinding.etUsernameInput)
        mPersonalDetailsViewList.add(mBinding.vUsernameBackground)
        mPersonalDetailsViewList.add(mBinding.tvDOB)
        mPersonalDetailsViewList.add(mBinding.etUsernameInput)

        mPersonalDetailsViewList
                .filter { it.id != R.id.tv_next }
                .forEach { it.setOnClickListener(null) }

        var animatorSet = AnimationUtil.nextOnBoarding(mPersonalDetailsViewList)
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                AnimationUtil.resetAnimationPosition(mPersonalDetailsViewList)
                animateSearchSettingsContent()
            }
        })
        animatorSet.start()
    }

    private fun animateSearchSettingsContent() {
        var mAnimationList : ArrayList<Animator> = ArrayList()
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvSearchSettingTitle, from = -0.5f))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvSearchSettingIntro, from = -0.75f, startDelay = 30))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvTheirGenderTitle, from = -0.5f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ivMaleSearchSetting, from = -0.2f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ivFemaleSearchSetting, from = -0.2f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvAgeRangeTitle, from = -1.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.vAgeRangeBackground, from = -0.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.clAgeRangeValueContainer, from = -0.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ageRangeBar, from = -1.0f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvBack, from = 4f, startDelay = 100, duration = 600))
        mAnimationList.add(AnimationUtil.stateIndicatorAnimator(mBinding.stateIndicator, selectedValue = onBoardingStep, startDelay = 100, duration = 300))
        mAnimationList.add(AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, false))
        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                mBinding.ageRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                    mBinding.minValue.text = minValue.toString()
                    mBinding.maxValue.text = maxValue.toString()
                }
            }
        })
        setSearchSettingsViewListeners()
        mAnimatorSet.start()
    }

    private fun setSearchSettingsViewListeners() {
        mBinding.ivMaleSearchSetting.setOnClickListener(this)
        mBinding.ivFemaleSearchSetting.setOnClickListener(this)
        mBinding.tvBack.setOnClickListener(this)
    }

    private fun handleError(credentialValue: Int) {
        // todo asdsad
    }

    private fun checkPersonalDetailCredentials(): Int {
        if (mUser.gender == "defaultUser"){
            return Non_Gender
        } else if (mBinding.etUsernameInput.text.isEmpty()){
            return No_Username
        } else {
            return checkBirthday()
        }
    }

    private fun checkBirthday(): Int {
        val userBday = GregorianCalendar(mUser.birthYear, mUser.birthMonth, mUser.birthDay)
        val today  = GregorianCalendar()
        today.add(Calendar.YEAR, -18)
        return if (today.before(userBday)){
            Too_Young
        } else {
            if (mUser.birthDay > 0 && mUser.birthMonth > 0 && mUser.birthYear > 0){
                personalDetailSuccess
            } else No_DOB_Set
        }
    }

    private fun selectGender(gender: String) {

        var genderSelectionAnimatorSet = AnimatorSet()

        when (gender){
            MALE_GENDER ->{
                if (!mBinding.ivMaleSelection.viewSelected){

                    mBinding.ivMaleSelection.viewSelected = true

                    val elevationAnimator = ValueAnimator.ofFloat(1f, 10f)
                    elevationAnimator.addUpdateListener { animator ->
                        mBinding.ivMaleSelection.elevation = animator.animatedValue as Float
                    }
                    elevationAnimator.duration = 300
                    elevationAnimator.interpolator = OvershootInterpolator(12f)
                    genderSelectionAnimatorSet.play(elevationAnimator)

                    if (mBinding.ivFemaleSelection.viewSelected){
                        val elevationAnimator = ValueAnimator.ofFloat(10f, 1f)
                        elevationAnimator.addUpdateListener { animator ->
                            mBinding.ivFemaleSelection.elevation = animator.animatedValue as Float
                        }
                        elevationAnimator.duration = 300
                        elevationAnimator.interpolator = AccelerateInterpolator()
                        genderSelectionAnimatorSet.play(elevationAnimator)
                        mBinding.ivFemaleSelection.viewSelected = false
                    }

                    genderSelectionAnimatorSet.start()

                    mUser.gender = MALE_GENDER
                }
            }
            FEMALE_GENDER ->{

                if (!mBinding.ivFemaleSelection.viewSelected){

                    mBinding.ivFemaleSelection.viewSelected = true
                    val elevationAnimator = ValueAnimator.ofFloat(1f, 10f)
                    elevationAnimator.addUpdateListener { animator ->
                        mBinding.ivFemaleSelection.elevation = animator.animatedValue as Float
                    }
                    elevationAnimator.duration = 300
                    elevationAnimator.interpolator = OvershootInterpolator(12f)
                    genderSelectionAnimatorSet.play(elevationAnimator)

                    if (mBinding.ivMaleSelection.viewSelected){
                        val elevationAnimator = ValueAnimator.ofFloat(10f, 1f)
                        elevationAnimator.addUpdateListener { animator ->
                            mBinding.ivMaleSelection.elevation = animator.animatedValue as Float
                        }
                        elevationAnimator.duration = 300
                        elevationAnimator.interpolator = AccelerateInterpolator()
                        genderSelectionAnimatorSet.play(elevationAnimator)
                        mBinding.ivMaleSelection.viewSelected = false
                    }

                    genderSelectionAnimatorSet.start()
                    mUser.gender = FEMALE_GENDER
                }
            }
            else ->{

                val female = ValueAnimator.ofFloat(10f, 1f)
                female.addUpdateListener { animator ->
                    mBinding.ivFemaleSelection.elevation = animator.animatedValue as Float
                }
                female.duration = 300
                female.interpolator = AccelerateInterpolator()
                genderSelectionAnimatorSet.play(female)
                mBinding.ivFemaleSelection.viewSelected = false


                val elevationAnimator = ValueAnimator.ofFloat(10f, 1f)
                elevationAnimator.addUpdateListener { animator ->
                    mBinding.ivMaleSelection.elevation = animator.animatedValue as Float
                }
                elevationAnimator.duration = 300
                elevationAnimator.interpolator = AccelerateInterpolator()
                genderSelectionAnimatorSet.play(elevationAnimator)
                mBinding.ivMaleSelection.viewSelected = false
            }
        }
        checkComplete(onBoardingStep)
    }

    private fun animatePersonalDetailsContent() {

        Glide.with(this).load(R.drawable.girl).into(mBinding.ivFemaleSelection)
        Glide.with(this).load(R.drawable.male).into(mBinding.ivMaleSelection)

        var mAnimationList : ArrayList<Animator> = ArrayList()
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvPersonalDetailsTitle, from = -0.5f))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvPersonalDetailsIntro, from = -0.75f, startDelay = 30))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvGenderTitle, from = -0.5f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ivMaleSelection, from = -0.2f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ivFemaleSelection, from = -0.2f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvUsernameTitle, from = -1.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.vUsernameBackground, from = -0.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.etUsernameInput, from = -0.5f, startDelay = 90))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvDOB, from = -0.5f, startDelay = 120))

        if (!nextButtonAnimationHandled) {
            mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvNext, from = 4f, startDelay = 200, duration = 600))
        }

        if (!stateIndicatorAnimationHandled) {
            mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.stateIndicator, from = 4f, startDelay = 200, duration = 600))
            mAnimationList.add(AnimationUtil.stateIndicatorAnimator(mBinding.stateIndicator, selectedValue = onBoardingStep, startDelay = 600, duration = 600))
        }

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        setPersonalDetailsViewsClickListeners()
        mAnimatorSet.start()
        nextButtonAnimationHandled = true
        stateIndicatorAnimationHandled = true

    }

    private fun setPersonalDetailsViewsClickListeners() {
        mBinding.ivFemaleSelection.setOnClickListener(this)
        mBinding.ivMaleSelection.setOnClickListener(this)
        mBinding.tvDOB.setOnClickListener(this)
        mBinding.tvNext.setOnClickListener(this)
        mBinding.etUsernameInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                return
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkComplete(onBoardingStep)
            }
        })
    }

    override fun onFinishEditDialog(mUserModel: RendevouzUserModel) {
        mUser = mUserModel
        mBinding.tvDOB.setTextColor( Color.parseColor("#DE000000"))
        checkComplete(onBoardingStep)
        birthdateDialogueHandled = false
    }

    override fun checkComplete(stage: Int) {
        lateinit var mNextButtonAnimator : ValueAnimator
        when (stage){
            0 ->{
                mNextButtonAnimator = if (checkPersonalDetailCredentials() == personalDetailSuccess){
                    AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, true)
                } else AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, false)
                mNextButtonAnimator.start()
            }
        }
    }

}