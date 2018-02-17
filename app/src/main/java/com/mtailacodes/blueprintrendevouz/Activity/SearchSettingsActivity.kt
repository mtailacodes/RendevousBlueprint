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
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySearchSettingsBinding
import com.mtailacodes.blueprintrendevouz.fragments.SearchSettingsFragment
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings
import com.mtailacodes.blueprintrendevouz.viewpagerAdapter.SettingsViewPagerAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter

/**
 * Created by matthewtaila on 1/13/18.
 */

class SearchSettingsActivity: AppCompatActivity(), View.OnClickListener{

    //
    private var landing = 0

    // search settings object
    private lateinit var mSearchSettings : UserSearchSettings

    // views
    private var mAnimationList : ArrayList<Animator> = ArrayList()
    private lateinit var mBinding: ActivitySearchSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_settings)

        mBinding.tvDone.setOnClickListener(this)

        getUserSettings() // from intent
        getViewpagerLandingPage() // from intent
        onEnterActivityAnimation() // animate the views on enter
    }

    override fun onResume() {
        super.onResume()

        (application as MyApplication) // Listening for published events from settings fragment to kill activity
            .bus()
            .toObservable()
            .subscribe { `object` ->
                if (`object` is String) {
                    when (`object`.toString()) {
                        "User Search Settings Stored" ->{ // need to refactor this workflow - too dependent on state listeners
                            finish()
                       }
                    }
               }
            }
    }

    private fun getUserSettings() {
        mSearchSettings = intent.extras.get(Constants().USER_SEARCH_SETTINGS_OBJECT) as UserSearchSettings
    }
    private fun getViewpagerLandingPage() {
        landing = intent.extras.get(Constants().SETTINGS_VIEWPAGER_LANDING) as Int
    }

    private fun onEnterActivityAnimation() {
        mAnimationList.clear()
        mAnimationList.add(AnimationUtil.translateY(mBinding.tvSettingsTitle, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue), duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvSettingsTitle, duration = 700, startDelay = 200, alphaValue = 1f))
        mAnimationList.add(AnimationUtil.translateY(mBinding.tvDone, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue), duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvDone, duration = 700, startDelay = 200, alphaValue = 1f))
        mAnimationList.add(AnimationUtil.alpha(mBinding.vpSettingsViewpager, duration = 500, startDelay = 200, alphaValue = 1f))

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

        // setup titles for tab layout
        var mViewPagerSmartTab = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add(Constants().SEARCH_TITLE, SearchSettingsFragment::class.java)
                .add(Constants().PROFILE_TITLE, SearchSettingsFragment::class.java)
                .add(Constants().NOTIFICATION_TITLE, SearchSettingsFragment::class.java)
                .create())

        mBinding.vpSettingsViewpager.adapter = mViewPagerSmartTab
        mBinding.vptSettingsTab.setViewPager(mBinding.vpSettingsViewpager)

        var mViewPagerAdapter = SettingsViewPagerAdapter(supportFragmentManager, mSearchSettings)
        mBinding.vpSettingsViewpager.adapter = mViewPagerAdapter
        mBinding.vpSettingsViewpager.currentItem = landing
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_Done ->{ // publishes event for the listening child fragments to update their corresponding endpoints
                (application as MyApplication)
                        .bus()
                        .send("SAVE SEARCH SETTINGS")
            }
        }
    }
}