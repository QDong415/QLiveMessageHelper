package com.dq.livemessagedemo.model

import android.text.SpannableStringBuilder
import android.util.Log

class LiveMessageModel : LiveMessageIntrinsicModel() {

    //Transient表示不参与序列化，json解析库也不解析该字段
    @Transient
    var spannableString : SpannableStringBuilder? = null

}