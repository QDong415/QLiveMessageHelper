package com.dq.livemessagedemo.tool

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.widget.TextView
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel.Companion.TYPE_GIFT
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel.Companion.TYPE_MESSAGE

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的SpannableString的昵称String
class LiveMessageTextSpanNameStrategy: LiveMessageTextSpanStrategy {

    //用户昵称
    private val nameColorSpan = ForegroundColorSpan(Color.parseColor("#8CE7FF"))

    //否需要显示昵称String
    override fun spanAppend(model: LiveMessageIntrinsicModel, currentIndex: Int, totalSpannableString: SpannableStringBuilder): Int {
        model.name?.let {
            totalSpannableString.append(it)
            var nameTotalLength = it.length
            if (model.type == TYPE_MESSAGE){
                //是用户写的文字消息
                totalSpannableString.append("：")
                nameTotalLength += 1 //+1是那个冒号

            } else if (model.type == LiveMessageIntrinsicModel.TYPE_COMING){
                //是用户进入房间
                totalSpannableString.append(" ")
                nameTotalLength += 1 //+1是那个空格

            } else if (model.type == TYPE_GIFT){
                //是礼物
                totalSpannableString.append(" ")
                nameTotalLength += 1 //+1是那个空格
            }

            totalSpannableString.setSpan(nameColorSpan, currentIndex, currentIndex + nameTotalLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            return nameTotalLength
        }
        return 0
    }
}