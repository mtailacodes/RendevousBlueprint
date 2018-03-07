package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.mtailacodes.blueprintrendevouz.R


class CurveContainer : ConstraintLayout{

    var mainPath = Path()
    var shadowPath = Path()
    var xWidth = 0f
    var xHeight = 0f
    var biezerYValue = 75
    var biezerYDefaultVal2 = 75
    var sideControl = 0f
    var sideControlDefaultValue = 0.75f
    var sideControlMaxValue = 0.95f

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.showContainer)
        style = Paint.Style.FILL
    }

    private val mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG)).apply {
        color = ContextCompat.getColor(context, R.color.shadowGrey)
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.OUTER)
    }

    constructor(context: Context?) : super(context){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    open fun setBiezerY(y : Int){
        biezerYValue = y
        invalidate()
    }

    open fun setSideYValue(y : Float){
        sideControl = y
        invalidate()
    }

    private fun init() {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_SOFTWARE, mShadowPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        xWidth = measuredWidth.toFloat()
        xHeight = measuredHeight.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mainPath.reset()
        shadowPath.reset()
        canvas!!.drawColor(Color.TRANSPARENT)

         // curve for path and path shadow
        drawCurvedPath(mainPath)
        canvas!!.drawPath(mainPath, mainPaint)
        drawCurvedPath(shadowPath)
        canvas!!.drawPath(shadowPath, mShadowPaint)
    }

    private fun drawCurvedPath(path : Path) {
        path.moveTo(0f, 0f)
        path.lineTo(xWidth, 0f)
        path.lineTo(xWidth, xHeight * sideControl)
        path.quadTo(xWidth/2, (xHeight * sideControl) + biezerYValue, 0f, xHeight * sideControl)
        path.lineTo(0f, 0f)
    }
}