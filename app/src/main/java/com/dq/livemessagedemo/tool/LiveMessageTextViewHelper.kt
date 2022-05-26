package com.dq.livemessagedemo.tool

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.view.DrawablePaintTextView

class LiveMessageTextViewHelper {

    private var tagTextView : DrawablePaintTextView

    private var context: Context

    lateinit var imageSpanStrategyList: MutableList<LiveMessageImageSpanStrategy>
    lateinit var textSpanStrategyList: MutableList<LiveMessageTextSpanStrategy>

    //标签的高度，单位像素。0表示WRAP_CONTENT。但是建议你设置值，加快运行效率
    private var tagHeightPx: Int = 0

    constructor(context: Context) {
        this.context = context
        tagTextView = DrawablePaintTextView(context)
        tagTextView.setSingleLine()
        tagTextView.setPadding(dipToPx(6f).toInt(),0,dipToPx(6f).toInt(),0)
        tagTextView.gravity = Gravity.CENTER_VERTICAL
        tagTextView.textSize = 10f
        //固定一下tag高度
        this.tagHeightPx = dipToPx(14f).toInt()
        tagTextView.height = tagHeightPx

//        spanTitleStrategy.设置高度

        //如果不加这一句，会导致textView无法复用。只能每次都new
        tagTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    //这个方法仅限于主线程调用
    fun displaySpannableString(model: LiveMessageIntrinsicModel) : SpannableStringBuilder {

        val totalSpannableString = SpannableStringBuilder()
        var currentIndex = 0
        //拼接头部的几个tagImageSpan
        for (imageSpanStrategy in imageSpanStrategyList) {
            val imageSpan = imageSpanStrategy.imageSpan(model, tagTextView)
            currentIndex += imageSpanStrategy.spanAppend(imageSpan, currentIndex, totalSpannableString)
        }
        //拼接昵称+文本
        for (textSpanStrategy in textSpanStrategyList) {
            currentIndex += textSpanStrategy.spanAppend(model, currentIndex, totalSpannableString)
        }

        return totalSpannableString
    }

    private fun dipToPx(dip: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            context.getResources().getDisplayMetrics()
        )
    }

}