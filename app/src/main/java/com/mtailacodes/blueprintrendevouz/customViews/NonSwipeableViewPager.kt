package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent



/**
 * Created by matthewtaila on 2/10/18.
 */
class NonSwipeableViewPager(context: Context?, attrs: AttributeSet?) : ViewPager(context!!, attrs) {

    var canSwipe: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.canSwipe) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.canSwipe) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    fun setPagingEnabled(enabled: Boolean) {
        this.canSwipe = enabled
    }
}