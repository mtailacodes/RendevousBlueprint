package com.mtailacodes.blueprintrendevouz.customViews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by matthewtaila on 12/13/17.
 * Created to override the touch events + prevents user from moving onto step two of creating a user
 * Prevent viewpager scrolling by setting @isPagingEnabled to false
 */

public class CreateUserViewpager extends ViewPager {

    private boolean isPagingEnabled = true;

    public CreateUserViewpager(Context context) {
        super(context);
    }

    public CreateUserViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }


}
