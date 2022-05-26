package com.dq.livemessagedemo.tool

import android.text.style.ImageSpan
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dq.livemessage.QCenterAlignImageSpan
import com.dq.livemessage.ImageSpanCacheInstance
import com.dq.livemessagedemo.R
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.view.DrawablePaintTextView

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的第一个Tag（比如 "通知" "榜1" "房管"）
class LiveMessageImageSpanTitleStrategy : LiveMessageImageSpanStrategy {

    //计算是否需要头部Tag（比如 "通知" "榜1" "房管"）
    override fun imageSpan(model: LiveMessageIntrinsicModel, tagTextView: TextView): ImageSpan? {

        model.tips?.let {
            if (model.tips.isNullOrEmpty())
                return null

            //先看看缓存里有没有这个ImageSpan
            var imageSpan: ImageSpan? = ImageSpanCacheInstance.instance.getImageSpanFromCache(it)

            if (imageSpan != null) {
                //有缓存了
                return imageSpan
            }

            tagTextView.setTextColor(ContextCompat.getColor(tagTextView.context, android.R.color.white))
            tagTextView.setBackgroundResource(R.drawable.live_imagespan_red)
            tagTextView.setCompoundDrawables(null, null, null, null)
            tagTextView.compoundDrawablePadding = 5
            tagTextView.text = it

            //转类型，DrawablePaintTextView是我专门为了处理勋章这种而写的TextView
            tagTextView as DrawablePaintTextView
            tagTextView.leftDrawableText = ""

            val tagBitmap = convertViewToBitmap(tagTextView)

            return if (tagBitmap != null) {
//                imageSpan = ImageSpan(tagTextView.context , tagBitmap ,ImageSpan.ALIGN_CENTER)
                imageSpan = QCenterAlignImageSpan(tagTextView.context , tagBitmap)
                //缓存起来
                ImageSpanCacheInstance.instance.putImageSpanFromCache(it, imageSpan)
                imageSpan
            } else {
                null
            }
        }
        return null
    }

}