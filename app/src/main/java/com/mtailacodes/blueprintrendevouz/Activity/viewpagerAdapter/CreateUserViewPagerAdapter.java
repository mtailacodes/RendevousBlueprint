package com.mtailacodes.blueprintrendevouz.Activity.viewpagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mtailacodes.blueprintrendevouz.fragments.CreateUserFirstFragment;
import com.mtailacodes.blueprintrendevouz.fragments.CreateUserSecondFragment;

/**
 * Created by matthewtaila on 12/2/17.
 */

public class CreateUserViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public CreateUserViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CreateUserFirstFragment.newInstance();
            case 1:
                return CreateUserSecondFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
