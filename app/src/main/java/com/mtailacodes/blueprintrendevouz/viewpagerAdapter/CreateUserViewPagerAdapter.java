package com.mtailacodes.blueprintrendevouz.viewpagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseUser;
import com.mtailacodes.blueprintrendevouz.fragments.CreateUserFirstFragment;
import com.mtailacodes.blueprintrendevouz.fragments.CreateUserSecondFragment;

/**
 * Created by matthewtaila on 12/2/17.
 */

public class CreateUserViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;
    private FirebaseUser mFirebaseUser;

    public CreateUserViewPagerAdapter(FragmentManager fm, FirebaseUser currentUser) {
        super(fm);
        this.mFirebaseUser = currentUser;
    }

    public void setmFirebaseUser(FirebaseUser mFirebaseUser) {
        this.mFirebaseUser = mFirebaseUser;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CreateUserFirstFragment.newInstance(mFirebaseUser);
            case 1:
                return CreateUserSecondFragment.newInstance(mFirebaseUser);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
