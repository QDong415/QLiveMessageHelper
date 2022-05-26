package com.dq.livemessagedemo.tool

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel.Companion.TYPE_COMING
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel.Companion.TYPE_MESSAGE

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的Message的昵称String
class LiveMessageTextSpanMessageStrategy: LiveMessageTextSpanStrategy {

    //用户写的文字消息
    private val chatColorSpan = ForegroundColorSpan(Color.parseColor("#ffffff"))
    //是系统通知
    private val notifyColorSpan = ForegroundColorSpan(Color.parseColor("#ff6600"))
    //是用户进入房间
    private val comingColorSpan = ForegroundColorSpan(Color.parseColor("#999999"))


    //否需要显示昵称String
    override fun spanAppend(model: LiveMessageIntrinsicModel, currentIndex: Int, totalSpannableString: SpannableStringBuilder): Int {
        var messageTotalLength = 0

        if (model.type == TYPE_COMING){
            //是用户进入房间
            val message = "来了"
            messageTotalLength = message.length
            totalSpannableString.append(message)
            totalSpannableString.setSpan(comingColorSpan, currentIndex, currentIndex + messageTotalLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        } else {
            model.message?.let {
                if (model.type == TYPE_MESSAGE){
                    //是用户写的文字消息
                    messageTotalLength = it.length
                    totalSpannableString.append(it)
                    totalSpannableString.setSpan(chatColorSpan, currentIndex, currentIndex + messageTotalLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                } else {
                    //是系统通知
                    messageTotalLength = it.length
                    totalSpannableString.append(it)
                    totalSpannableString.setSpan(notifyColorSpan, currentIndex, currentIndex + messageTotalLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        return messageTotalLength
    }
}