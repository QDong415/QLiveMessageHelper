package com.dq.livemessagedemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT_BOLD
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class DrawablePaintTextView : AppCompatTextView {

    private var mDrawableTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //连续set属性，也只触发1次onDraw
    public var drawableTextColor = 0
        set(value) {
            field = value
            invalidate()
        }

    public var drawableTextSize = 0
        set(value) {
            field = value
            invalidate()
        }

    public var drawableTextTypeFace: Typeface = DEFAULT_BOLD //Typeface.create("sans-serif-light", Typeface.BOLD);
        set(value) {
            field = value
            invalidate()
        }

    public var leftDrawableText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    public var rightDrawableText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr) {
        //抗锯齿标志
        mDrawableTextPaint.style = Paint.Style.FILL
        mDrawableTextPaint.setTextAlign(Paint.Align.CENTER)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mDrawableTextPaint.textSize = drawableTextSize.toFloat()
        mDrawableTextPaint.color = drawableTextColor
        mDrawableTextPaint.typeface = drawableTextTypeFace

        //drawableLeft 的文字
        val drawables = compoundDrawables
        if (drawables[0] != null && leftDrawableText != null){
            //有leftDrawable 且 有左文字
            canvas.drawText(
                leftDrawableText!!,
                (paddingLeft + drawables[0].intrinsicWidth / 2).toFloat(),
                height / 2 - (mDrawableTextPaint.descent() + mDrawableTextPaint.ascent()) / 2,
                mDrawableTextPaint
            )
        }

        //drawableRight 的文字
        if (drawables[2] != null && rightDrawableText != null){
            //有RightDrawable 且 有左文字
            canvas.drawText(
                rightDrawableText!!,
                (width - paddingRight - drawables[2].intrinsicWidth / 2).toFloat(),
                height / 2 - (mDrawableTextPaint.descent() + mDrawableTextPaint.ascent()) / 2,
                mDrawableTextPaint
            )
        }
    }
}
