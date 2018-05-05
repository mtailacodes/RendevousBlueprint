package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import android.util.AttributeSet
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 1/17/18.
 */
class CustomSeekbar : CrystalRangeSeekbar{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getLeftDrawable(typedArray: TypedArray): Drawable? {
        return getDrawable(context, R.drawable.create_user_application_button)
    }
}