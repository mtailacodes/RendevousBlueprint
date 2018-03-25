package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide.init

/**
 * Created by matthewtaila on 3/22/18.
 */

class OnBoardingStateIndicator : View {

    var stepOnePaint = Paint()
    var stepTwoPaint = Paint()
    var stepThreePaint = Paint()
    var stepOne = Path()
    var stepTwo = Path()
    var stepThree = Path()
    var stepOneHeight = 0f
    var stepTwoHeight = 0f
    var stepThreeHeight = 0f
    var heightDefault = 0f
    var strokePath = 10f
    var mWidth = 0f
    var mHeight = 0f



    constructor(context: Context?) : super(context){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()

        stepOneHeight = mHeight/2
        stepTwoHeight = mHeight/2
        stepThreeHeight = mHeight/2
        heightDefault = mHeight/2
    }

    private fun init() {
        stepOnePaint.strokeWidth = strokePath
        stepOnePaint.style = Paint.Style.STROKE
        stepOnePaint.strokeCap = Paint.Cap.ROUND
        stepOnePaint.color = Color.parseColor("#999999")

        stepTwoPaint.strokeWidth = strokePath
        stepTwoPaint.style = Paint.Style.STROKE
        stepTwoPaint.strokeCap = Paint.Cap.ROUND
        stepTwoPaint.color = Color.parseColor("#999999")

        stepThreePaint.strokeWidth = strokePath
        stepThreePaint.style = Paint.Style.STROKE
        stepThreePaint.strokeCap = Paint.Cap.ROUND
        stepThreePaint.color = Color.parseColor("#999999")

    }

    open fun selectStepOne(height : Float){
        stepOneHeight = height
        invalidate()
    }

    open fun selectStepTwo(height : Float){
        stepTwoHeight = height
        invalidate()
    }

    open fun selectStepThree(height : Float){
        stepThreeHeight = height
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        stepOne.reset()
        stepTwo.reset()
        stepThree.reset()

        stepOne.moveTo(0f + strokePath/2, mHeight)
        stepOne.lineTo(0f+ strokePath/2, stepOneHeight)

        stepTwo.moveTo(mWidth/2, mHeight)
        stepTwo.lineTo(mWidth/2, stepTwoHeight)

        stepThree.moveTo(mWidth - strokePath/2, mHeight)
        stepThree.lineTo(mWidth - strokePath/2, stepThreeHeight)

        canvas.drawPath(stepOne, stepOnePaint)
        canvas.drawPath(stepTwo, stepTwoPaint)
        canvas.drawPath(stepThree, stepThreePaint)

        canvas.restore()
    }
}