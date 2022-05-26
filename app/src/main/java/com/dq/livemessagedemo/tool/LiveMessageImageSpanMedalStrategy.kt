package com.dq.livemessagedemo.tool

import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dq.livemessage.QCenterAlignImageSpan
import com.dq.livemessage.ImageSpanCacheInstance
import com.dq.livemessagedemo.R
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.view.DrawablePaintTextView

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的第3个Tag（勋章）特点是：左边是小icon+勋章等级 右边是勋章为文字
class LiveMessageImageSpanMedalStrategy : LiveMessageImageSpanStrategy {

    //计算是否需要勋章Tag
    override fun imageSpan(model: LiveMessageIntrinsicModel, tagTextView: TextView): ImageSpan? {
        model.medal?.let {
            //先看看缓存里有没有这个ImageSpan
            var imageSpan: ImageSpan? = ImageSpanCacheInstance.instance.getImageSpanFromCache(model.medalLevel, it)

            if (imageSpan != null){
                //有缓存了
                return imageSpan
            }

            tagTextView.setTextColor(ContextCompat.getColor(tagTextView.context, android.R.color.white))
            tagTextView.setBackgroundResource(R.drawable.live_imagespan_medal)
            val drawable: Drawable? = ContextCompat.getDrawable(tagTextView.context, R.mipmap.live_medal_left)
            drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tagTextView.setCompoundDrawables(drawable, null, null, null)
            tagTextView.compoundDrawablePadding = 5
            tagTextView.text = it

            //转类型，DrawablePaintTextView是我专门为了处理勋章这种而写的TextView
            tagTextView as DrawablePaintTextView
            tagTextView.drawableTextColor = 0xffffffff.toInt()
            tagTextView.drawableTextSize = 20 //DimensionUtil.dpToPx(this, 10)
            tagTextView.leftDrawableText = model.medalLevel.toString()

            val tagBitmap = convertViewToBitmap(tagTextView)

            if (tagBitmap != null){
//                imageSpan = ImageSpan(tagTextView.context , tagBitmap ,ImageSpan.ALIGN_CENTER)
                imageSpan = QCenterAlignImageSpan(tagTextView.context , tagBitmap)
                //缓存起来
                ImageSpanCacheInstance.instance.putImageSpanFromCache(model.medalLevel, it, imageSpan)
                return imageSpan
            } else {
                return null
            }
        }
        return null
    }

}