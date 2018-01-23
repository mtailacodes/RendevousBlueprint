package com.mtailacodes.blueprintrendevouz.viewpagerAdapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mtailacodes.blueprintrendevouz.fragments.PersonalSettingsFragment
import com.mtailacodes.blueprintrendevouz.fragments.SearchSettingsFragment
import com.mtailacodes.blueprintrendevouz.models.user.user.login.UserSearchSettings

/**
 * Created by matthewtaila on 1/20/18.
 */
class SettingsViewPagerAdapter: FragmentPagerAdapter{

    private lateinit var mSearchSettings : UserSearchSettings

    constructor(fm: FragmentManager, userSearchSettings: UserSearchSettings) : super(fm){
        mSearchSettings = userSearchSettings
    }


    override fun getItem(position: Int): Fragment {
        lateinit var mFragment : Fragment
        when(position){
            0 -> { mFragment = SearchSettingsFragment.newInstance(mSearchSettings) }
            1 -> { mFragment = PersonalSettingsFragment.newInstance() }
            2 -> { mFragment =  SearchSettingsFragment.newInstance(mSearchSettings) }
        }
        return mFragment
    }

    override fun getCount(): Int {
        return 3
    }


}