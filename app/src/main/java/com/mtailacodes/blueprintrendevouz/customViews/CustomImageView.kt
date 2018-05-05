package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View

/**
 * Created by matthewtaila on 3/6/18.
 */
class CustomImageView : CardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }


}