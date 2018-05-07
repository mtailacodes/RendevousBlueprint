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
    override fun onFinishEditDialog(mUserModel: RendevouzUserModel, date: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mBinding : ActivityOnboardingBinding
    lateinit var mUser : RendevouzUserModel
    lateinit var mUserSearchSettings : UserSearchSettings
    var onBoardingStep : Int = 0
    var nextButtonAnimationHandled = false
    var stateIndicatorAnimationHandled = false

    val FEMALE_GENDER = "Female"
    val MALE_GENDER = "Male"
    var birthdateDialogueHandled = false

    var personalDetailsViewsList: ArrayList<View> = ArrayList()
    var searchSettingsViewsList: ArrayList<View> = ArrayList()
    var finishOnBoardingViewList: ArrayList<View> = ArrayList()

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

        generateViewsList()

        val bundle = intent.extras
        mUser = bundle.getParcelable("mUser")
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

    private fun generateViewsList() {
        personalDetailsViewsList.add(mBinding.tvPersonalDetailsTitle)
        personalDetailsViewsList.add(mBinding.tvPersonalDetailsIntro)
        personalDetailsViewsList.add(mBinding.tvGenderTitle)
        personalDetailsViewsList.add(mBinding.ivMaleSelection)
        personalDetailsViewsList.add(mBinding.ivFemaleSelection)
        personalDetailsViewsList.add(mBinding.tvUsernameTitle)
        personalDetailsViewsList.add(mBinding.etUsernameInput)
        personalDetailsViewsList.add(mBinding.vUsernameBackground)
        personalDetailsViewsList.add(mBinding.tvDOB)
        personalDetailsViewsList.add(mBinding.etUsernameInput)

        searchSettingsViewsList.add(mBinding.tvSearchSettingTitle)
        searchSettingsViewsList.add(mBinding.tvSearchSettingIntro)
        searchSettingsViewsList.add(mBinding.tvTheirGenderTitle)
        searchSettingsViewsList.add(mBinding.ivMaleSearchSetting)
        searchSettingsViewsList.add(mBinding.ivFemaleSearchSetting)
        searchSettingsViewsList.add(mBinding.ageRangeBar)
        searchSettingsViewsList.add(mBinding.clAgeRangeValueContainer)
        searchSettingsViewsList.add(mBinding.tvAgeRangeTitle)
        searchSettingsViewsList.add(mBinding.vAgeRangeBackground)

        finishOnBoardingViewList.add(mBinding.tvFinishOnboardingTitle)
        finishOnBoardingViewList.add(mBinding.tvFinishOnboardingIntro)
        finishOnBoardingViewList.add(mBinding.tvTakePictureIntro)
        finishOnBoardingViewList.add(mBinding.ivCameraOnboarding)
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
                if (mUser.gender != FEMALE_GENDER){
                    selectGender(FEMALE_GENDER)
                }
            }
            R.id.iv_maleSelection ->{
                if (mUser.gender != MALE_GENDER){
                    selectGender(MALE_GENDER)
                }
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
                        if (mUserSearchSettings.gender != "default user"){
//                            setSearchSettings()
                            onBoardingStep += 1
                            showFinalOnBoardingStep()
                        }
                    }
                }
            }
            R.id.tv_DOB ->{
                if (!birthdateDialogueHandled){
                    DatePickerFragment(mUser, this).show(fragmentManager, "DatePicker")
                    birthdateDialogueHandled = true
                }
            }
            R.id.tv_back ->{
                if (onBoardingStep == 2){
                    var animatorSet = AnimationUtil.backOnBoarding(finishOnBoardingViewList)
                    animatorSet.addListener(object : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            AnimationUtil.resetAnimationPosition(finishOnBoardingViewList)
                        }
                    })
                    animatorSet.start()

                }
                onBoardingStep -= 1
                updateOnBoardingStep(onBoardingStep)
            }
            R.id.iv_femaleSearchSetting ->{
                if (mUserSearchSettings.gender != FEMALE_GENDER){
                    selectSearchSettingsGender(FEMALE_GENDER)
                }
            }
            R.id.iv_maleSearchSetting ->{
                if (mUserSearchSettings.gender != MALE_GENDER) {
                    selectSearchSettingsGender(MALE_GENDER)
                }
            }
        }
    }

    private fun showFinalOnBoardingStep() {
        searchSettingsViewsList.forEach { it.setOnClickListener(null) }
        var animatorSet = AnimationUtil.nextOnBoarding(searchSettingsViewsList)
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                AnimationUtil.resetAnimationPosition(searchSettingsViewsList)
                animateFinishOnboarding()
            }
        })
        animatorSet.start()
    }

    private fun animateFinishOnboarding() {
        var mAnimationList : ArrayList<Animator> = ArrayList()
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvFinishOnboardingTitle, from = -0.5f))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvFinishOnboardingIntro, from = -0.75f, startDelay = 30))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvTakePictureIntro, from = -0.5f, startDelay = 60))
        mAnimationList.add(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.ivCameraOnboarding, from = -0.2f, startDelay = 60))
        mAnimationList.add(AnimationUtil.stateIndicatorAnimator(mBinding.stateIndicator, selectedValue = onBoardingStep, startDelay = 100, duration = 300))
        mAnimationList.add(AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, true, endingTextColor = Color.parseColor("#2D9A5B")))
        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.start()
    }

    private fun setSearchSettings() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun selectSearchSettingsGender(gender: String) {
        var genderSelectionAnimatorSet = AnimatorSet()

        when (gender){
            MALE_GENDER ->{
                val selectMaleAnimator = AnimationUtil.selectGender(mBinding.ivMaleSearchSetting)
                genderSelectionAnimatorSet.play(selectMaleAnimator)

                if (mUserSearchSettings.gender == FEMALE_GENDER){
                    val deselectFemaleAnimator = AnimationUtil.deselectGender(mBinding.ivFemaleSearchSetting)
                    genderSelectionAnimatorSet.play(deselectFemaleAnimator)
                }

                genderSelectionAnimatorSet.start()
                mUserSearchSettings.gender = MALE_GENDER
            }
            FEMALE_GENDER ->{
                val selectFemaleAnimator = AnimationUtil.selectGender(mBinding.ivFemaleSearchSetting)
                genderSelectionAnimatorSet.play(selectFemaleAnimator)

                if (mUserSearchSettings.gender == MALE_GENDER){
                    val deselectMaleAnimator = AnimationUtil.deselectGender(mBinding.ivMaleSearchSetting)
                    genderSelectionAnimatorSet.play(deselectMaleAnimator)
                }

                genderSelectionAnimatorSet.start()
                mUserSearchSettings.gender = FEMALE_GENDER
            }
        }

        checkComplete(onBoardingStep)

    }

    private fun updateOnBoardingStep(onBoardingStep: Int) {
        when (onBoardingStep){
            0 ->{
                var animatorSet = AnimationUtil.backOnBoarding(searchSettingsViewsList)
                animatorSet.play(AnimationUtil.translateYRelativeToHeightAnimator(mBinding.tvBack, from = 0f, to = 5f))
                animatorSet.play(AnimationUtil.stateIndicatorAnimator(mBinding.stateIndicator, onBoardingStep))

                animatorSet.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        AnimationUtil.resetAnimationPosition(searchSettingsViewsList)
                        animatePersonalDetailsContent()
                    }
                })
                animatorSet.start()
            }
            1 ->{ setPersonalDetails() }
        }
    }

    private fun setPersonalDetails() {
        personalDetailsViewsList.forEach { it.setOnClickListener(null) }
        var animatorSet = AnimationUtil.nextOnBoarding(personalDetailsViewsList)
        animatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                AnimationUtil.resetAnimationPosition(personalDetailsViewsList)
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

                    val selectMaleAnimator = AnimationUtil.selectGender(mBinding.ivMaleSelection)
                    genderSelectionAnimatorSet.play(selectMaleAnimator)

                    if (mUser.gender == FEMALE_GENDER){
                        val deselectFemaleAnimator = AnimationUtil.deselectGender(mBinding.ivFemaleSelection)
                        genderSelectionAnimatorSet.play(deselectFemaleAnimator)
                    }

                    genderSelectionAnimatorSet.start()
                    mUser.gender = MALE_GENDER
            }
            FEMALE_GENDER ->{

                    val selectFemaleAnimator = AnimationUtil.selectGender(mBinding.ivFemaleSelection)
                    genderSelectionAnimatorSet.play(selectFemaleAnimator)
                    if (mUser.gender == MALE_GENDER){
                        val deselectMaleAnimator = AnimationUtil.deselectGender(mBinding.ivMaleSelection)
                        genderSelectionAnimatorSet.play(deselectMaleAnimator)
                    }
                    genderSelectionAnimatorSet.start()
                    mUser.gender = FEMALE_GENDER

            }
            else ->{
                val deselectMaleAnimator = AnimationUtil.deselectGender(mBinding.ivMaleSelection)
                val deselectFemaleAnimator = AnimationUtil.deselectGender(mBinding.ivFemaleSelection)
                genderSelectionAnimatorSet.playTogether(deselectFemaleAnimator, deselectMaleAnimator)
                genderSelectionAnimatorSet.duration = 0
                genderSelectionAnimatorSet.start()
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


    override fun checkComplete(stage: Int) {
        lateinit var mNextButtonAnimator : ValueAnimator
        when (stage){
            0 ->{
                mNextButtonAnimator = if (checkPersonalDetailCredentials() == personalDetailSuccess){
                    AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, true)
                } else AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, false)
                mNextButtonAnimator.start()
            }
            1 ->{
                if (mUserSearchSettings.gender != "default user")
                    mNextButtonAnimator = AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, true, endingTextColor = Color.parseColor("#BF55EC"))
                else mNextButtonAnimator = AnimationUtil.nextButtonColorAnimator(mBinding.tvNext, false)
                mNextButtonAnimator.start()
            }
        }
    }

}