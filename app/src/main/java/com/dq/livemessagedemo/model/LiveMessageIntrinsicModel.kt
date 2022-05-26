package com.dq.livemessagedemo.model

open class LiveMessageIntrinsicModel : Object() {

    companion object {
        val TYPE_SYSTEM: Int = 1//系统通知
        val TYPE_COMING: Int = 2//xxx来了
        val TYPE_MESSAGE: Int = 3//文字信息
        val TYPE_LIKE: Int = 4//点赞
        val TYPE_FOLLOW: Int = 5//关注了主播
        val TYPE_GIFT: Int = 6//送礼
    }

    var type: Int = 0

    //发言人信息
    var userid: String? = null
    var name: String? = null
    var avatar: String? = null
    var level: Int = 0 //用户等级
    var medal: String? = null //用户勋章
    var medalLevel: Int = 0  //用户勋章等级

    //发言人消息
    var message: String? = null

    //时间戳
    var createTime: Int = 0

    //榜1、通知
    var tips: String? = null

    //如果type == 送礼物，那么就要有下面的字段
    var giftid: String? = null
    var giftName: String? = null
    var giftGifUrl: String? = null//礼物全屏动画图片
    var giftThumbUrl: String? = null//礼物小图片
    var giftCount: Int = 1 //礼物数量

}