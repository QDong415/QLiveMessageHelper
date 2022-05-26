package com.dq.livemessage

import android.text.style.ImageSpan
import android.util.SparseArray
import java.lang.StringBuilder

//单例，用来缓存直播间的标签图片，不用每次都创建ImageSpan
class ImageSpanCacheInstance {

    //用来缓存Key是String的tagImageSpan，一般用于"通知" ，"榜1"，"公告"
    private val textImageSpanHashMap: HashMap<String, ImageSpan> = HashMap()

    //用来缓存Key是纯数字的tagImageSpan，一般用于用户等级
    private val intImageSpanHashMap: SparseArray<ImageSpan> = SparseArray()

    //用来缓存Key是数字 + string 的tagImageSpan，一般用于勋章。这个建议退出直播间就清空，为了释放内存
    private val medalImageSpanHashMap: HashMap<String, ImageSpan> = HashMap()

    private constructor() {

    }

    fun getImageSpanFromCache(key: Int): ImageSpan? {
        return intImageSpanHashMap.get(key)
    }

    fun putImageSpanFromCache(key: Int, imageSpan: ImageSpan){
        intImageSpanHashMap.put(key, imageSpan)
    }

    fun getImageSpanFromCache(key: String): ImageSpan? {
        return textImageSpanHashMap.get(key)
    }

    fun putImageSpanFromCache(key: String, imageSpan: ImageSpan){
        textImageSpanHashMap.put(key, imageSpan)
    }

    fun getImageSpanFromCache(keyInt: Int, keyString: String): ImageSpan? {
        var key = StringBuilder()
        key.append(keyInt)
        key.append("_")
        key.append(keyString)
        return medalImageSpanHashMap[key.toString()]
    }

    fun putImageSpanFromCache(keyInt: Int, keyString: String, imageSpan: ImageSpan){
        var key = StringBuilder()
        key.append(keyInt)
        key.append("_")
        key.append(keyString)
        medalImageSpanHashMap[key.toString()] = imageSpan
    }

    //清空缓存。如果你不调用，会被爆内存泄漏；但是如果你调用了清空缓存，下次进入直播Activity会重新创建
    fun clearAll(){
        textImageSpanHashMap.clear()
        intImageSpanHashMap.clear()
        medalImageSpanHashMap.clear()
    }

    fun clearMedalCache(){
        medalImageSpanHashMap.clear()
    }

    companion object {

        val instance: ImageSpanCacheInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ImageSpanCacheInstance()
        }
    }


}