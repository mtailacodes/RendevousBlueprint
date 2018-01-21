package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.mtailacodes.blueprintrendevouz.MyApplication
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySearchSettingsBinding
import com.mtailacodes.blueprintrendevouz.fragments.SearchSettingsFragment
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import com.mtailacodes.blueprintrendevouz.viewpagerAdapter.SettingsViewPagerAdapter
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter



/**
 * Created by matthewtaila on 1/13/18.
 */
class SearchSettingsActivity: AppCompatActivity(), View.OnClickListener{



    private var mAnimationList : ArrayList<Animator> = ArrayList()
    private lateinit var mBinding: ActivitySearchSettingsBinding
    private lateinit var mSearchSettings : UserSearchSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_settings)
        mBinding.tvDone.setOnClickListener(this)
        getUserSettings()
        onEnterActivityAnimation()
    }

    override fun onResume() {
        super.onResume()
        (application as MyApplication)
                .bus()
                .toObservable()
                .subscribe { `object` ->
                    if (`object` is String) {
                        when (`object`.toString()) {
                            "User Search Settings Stored" ->{
                                finish()
                            }
                        }
                    }
                }
    }

    private fun getUserSettings() {
        mSearchSettings = intent.extras.get("SearchSettings") as UserSearchSettings
    }

    private fun onEnterActivityAnimation() {
        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.translateY(mBinding.tvSettingsTitle, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue),
                duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvSettingsTitle, duration = 700,
                startDelay = 200, alphaValue = 1f))
        mAnimationList.add(AnimationUtil.translateY(mBinding.tvDone, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue),
                duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvDone, duration = 700,
                startDelay = 200, alphaValue = 1f))

        mAnimationList.add(AnimationUtil.alpha(mBinding.vpSettingsViewpager, duration = 500,
                startDelay = 200, alphaValue = 1f))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)
        mAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        mAnimatorSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                setupSearchSettingsViewpager()
            }
        })
        mAnimatorSet.start()
    }

    private fun setupSearchSettingsViewpager() {
        var mViewPagerSmartTab = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add("Search Settings", SearchSettingsFragment::class.java)
                .add("Profile Settings", SearchSettingsFragment::class.java)
                .add("Notification Settings", SearchSettingsFragment::class.java)
                .create())

        mBinding.vpSettingsViewpager.adapter = mViewPagerSmartTab
        mBinding.vptSettingsTab.setViewPager(mBinding.vpSettingsViewpager)

        var mViewPagerAdapter = SettingsViewPagerAdapter(supportFragmentManager, mSearchSettings)
        mBinding.vpSettingsViewpager.adapter = mViewPagerAdapter
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_Done ->{
                (application as MyApplication)
                        .bus()
                        .send("SAVE SEARCH SETTINGS")
            }
        }
    }

    // todo update UserSearchSettings model to hold distance of search
    // todo add done button at top to save the searchSettings and hit the right database to store the search settings

}