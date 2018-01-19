package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySearchSettingsBinding
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 1/13/18.
 */
class SearchSettingsActivity: AppCompatActivity(){

    private var mAnimationList : ArrayList<Animator> = ArrayList()
    private lateinit var mBinding: ActivitySearchSettingsBinding
    private lateinit var mSearchSettings : UserSearchSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_settings)
        setupRangeBar()
        getUserSettings()
        onEnterActivityAnimation()
    }

    private fun setupRangeBar() {
        mBinding.ageRangeBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            mBinding.tvMinAge.text = minValue.toString()
            mBinding.tvMaxAge.text = maxValue.toString()
        }
        mBinding.ageRangeBar.setMinStartValue(25f)
    }

    private fun getUserSettings() {
        mSearchSettings = intent.extras.get("SearchSettings") as UserSearchSettings
        setupSearchSettingsState()
    }

    private fun setupSearchSettingsState() {
        when (mSearchSettings.sexIntereset) {
            "Male" ->{
                mBinding.rbMale.isChecked = true
                mBinding.rbFemale.isChecked = false
            }
            "Female" ->{
                mBinding.rbMale.isChecked = false
                mBinding.rbFemale.isChecked = true
            }
        }

        // todo - setup the same for age range and distance range
    }

    private fun onEnterActivityAnimation() {
        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.translateY(mBinding.tvSettingsTitle, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue),
                duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvSettingsTitle, duration = 700, startDelay = 200,
                alphaValue = 1f))
        mAnimationList.add(AnimationUtil.alpha(mBinding.clGenderInterestContainer, duration = 600, startDelay = 200,
                alphaValue = 1f))
        mAnimationList.add(AnimationUtil.alpha(mBinding.clAgeRangeContainer, duration = 600, startDelay = 400,
                alphaValue = 1f))
        mAnimationList.add(AnimationUtil.alpha(mBinding.clDistanceRangeContainer, duration = 600, startDelay = 600,
                alphaValue = 1f))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        mAnimatorSet.start()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    // todo list
    // todo find another rangebar so you can set the age range values from the users stored settings
    // todo update UserSearchSettings model to hold distance of search
    // todo add done button at top to save the searchSettings and hit the right database to store the search settings

}