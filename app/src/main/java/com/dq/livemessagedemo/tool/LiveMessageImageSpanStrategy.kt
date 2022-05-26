package com.dq.livemessagedemo.tool

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel

interface LiveMessageImageSpanStrategy {

    fun imageSpan(model: LiveMessageIntrinsicModel, tagTextView: TextView): ImageSpan?

    //拼接
    fun spanAppend(imageSpan: CharacterStyle?, currentIndex: Int, totalSpannableString: SpannableStringBuilder): Int {
        imageSpan?.let {
            totalSpannableString.append(" ")//设置1个空字符串来占坑
            totalSpannableString.setSpan(imageSpan, currentIndex, currentIndex + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            totalSpannableString.append(" ") //这个是拼接好imageSpan后，拼接空格
            return 2 //这个2 = 被替换那个空字符串的长度 + 空格
        }
        return 0
    }

    //TagTextView的高度
    fun tagHeightPx(): Int {
        return 0
    }

    fun convertViewToBitmap(view: View): Bitmap? {
        //不加下面两句，会报错：width and height must be > 0
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            if (tagHeightPx() == 0) View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED) else View.MeasureSpec.makeMeasureSpec(tagHeightPx(), View.MeasureSpec.EXACTLY))

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}