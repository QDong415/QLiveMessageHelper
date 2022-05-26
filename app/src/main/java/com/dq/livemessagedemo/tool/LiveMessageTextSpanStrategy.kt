package com.dq.livemessagedemo.tool

import android.text.SpannableStringBuilder
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel

//直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的String部分：比如 XXX: XXXXX
interface LiveMessageTextSpanStrategy {

    //String拼接方式
    fun spanAppend(model: LiveMessageIntrinsicModel, currentIndex: Int, totalSpannableString: SpannableStringBuilder): Int
}