package com.mtailacodes.blueprintrendevouz.viewpagerAdapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mtailacodes.blueprintrendevouz.fragments.onboarding.OnBoardingAboutMe
import com.mtailacodes.blueprintrendevouz.fragments.onboarding.OnBoardingSearchSettings
import com.mtailacodes.blueprintrendevouz.fragments.onboarding.OnBoardingWelcomeFragment

/**
 * Created by matthewtaila on 2/10/18.
 */

class OnBoardingViewPagerAdapter: FragmentPagerAdapter {
    constructor(fm: FragmentManager?) : super(fm)

    override fun getItem(position: Int): Fragment {
        lateinit var mFragment : Fragment
        when(position){
            0 -> { mFragment = OnBoardingWelcomeFragment.newInstance() }
            1 -> { mFragment = OnBoardingAboutMe.newInstance() }
            2 -> { mFragment = OnBoardingSearchSettings.newInstance() }
        }
        return mFragment
    }

    override fun getCount(): Int {
        return 3
    }

}