package com.mtailacodes.blueprintrendevouz.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide.init
import com.mtailacodes.blueprintrendevouz.R

/**
 * Created by matthewtaila on 3/21/18.
 */
class SelectGenderImageView : ImageView {

    var selectPaint = Paint()
    var rightSidePath = Path()
    var bottomSidePath = Path()
    var topSidePath = Path()
    var leftSidePath = Path()
    var mHeight = 0f
    var mWidth = 0f
    var controlTop = 0f
    var controlBottom = 0f
    var controlLeft = 0f
    var controlRight = 0f
    var viewSelected = false
    var strokeWidth = 2f

    constructor(context: Context?) : super(context){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    fun init() {
        selectPaint.style = Paint.Style.STROKE
        selectPaint.strokeWidth = strokeWidth
        selectPaint.color = resources.getColor(R.color.failedRed)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = measuredHeight.toFloat()
        mWidth = measuredWidth.toFloat()
        controlRight = mHeight
        controlLeft = mHeight
        controlBottom = mWidth
        controlTop = mWidth
    }

    open fun handleTop(top : Float){
        topSidePath.reset()
        controlTop = top
        invalidate()
    }

    open fun handleBottom(bottom : Float){
        bottomSidePath.reset()
        controlBottom = bottom
        invalidate()
    }

    open fun handleRight(right : Float){
        rightSidePath.reset()
        controlRight = right
        invalidate()
    }

    open fun handleLeft(left : Float){
        leftSidePath.reset()
        controlLeft = left
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (viewSelected){
            // bottom right to top right
            rightSidePath.moveTo(mWidth - strokeWidth, mHeight)
            rightSidePath.lineTo(mWidth - strokeWidth, controlRight)

            // bottom right to bottom left
            bottomSidePath.moveTo(mWidth, mHeight - strokeWidth)
            bottomSidePath.lineTo(controlBottom, mHeight - strokeWidth)

            // bottom left to top left
            leftSidePath.moveTo(0f, mHeight)
            leftSidePath.lineTo(0f, controlLeft)

            // top right to top left
            topSidePath.moveTo(mWidth, 0f)
            topSidePath.lineTo(mWidth, 0f)

            canvas.drawPath(rightSidePath, selectPaint)
            canvas.drawPath(leftSidePath, selectPaint)
            canvas.drawPath(topSidePath, selectPaint)
            canvas.drawPath(bottomSidePath, selectPaint)
        }

    }
}