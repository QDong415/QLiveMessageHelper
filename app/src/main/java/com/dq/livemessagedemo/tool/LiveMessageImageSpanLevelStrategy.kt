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

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的第二个Tag（等级）
class LiveMessageImageSpanLevelStrategy : LiveMessageImageSpanStrategy {

    //计算是否需要用户等级Tag
    override fun imageSpan(model: LiveMessageIntrinsicModel, tagTextView: TextView): ImageSpan? {
        model.level?.let {
            //先看看缓存里有没有这个ImageSpan
            var imageSpan: ImageSpan? = ImageSpanCacheInstance.instance.getImageSpanFromCache(it)

            if (imageSpan != null){
                //有缓存了
                return imageSpan
            }

            tagTextView.setTextColor(ContextCompat.getColor(tagTextView.context, android.R.color.white))
            tagTextView.setBackgroundResource(R.drawable.live_imagespan_level)
            val drawable: Drawable? = ContextCompat.getDrawable(tagTextView.context, R.mipmap.live_level_left)
            drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tagTextView.setCompoundDrawables(drawable, null, null, null)
            tagTextView.compoundDrawablePadding = 5
            tagTextView.text = it.toString()

            //转类型，DrawablePaintTextView是我专门为了处理勋章这种而写的TextView
            tagTextView as DrawablePaintTextView
            tagTextView.leftDrawableText = ""

            val tagBitmap = convertViewToBitmap(tagTextView)

            return if (tagBitmap != null){
                //ImageSpan.ALIGN_CENTER 为 api29。为了兼容低版本android，我把源码复制过来了，到
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