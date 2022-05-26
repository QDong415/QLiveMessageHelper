package com.dq.livemessagedemo.tool

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.model.LiveMessageModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okio.Buffer
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class SocketManager {

    //json解析
    private val jsonAdapter: JsonAdapter<LiveMessageModel> = Moshi.Builder().build().adapter<LiveMessageModel>(LiveMessageModel::class.java)
    private var jsonBuffer: Buffer = Buffer();

    var mConnectListener: ConnectListener? = null

    private val handler = MyHandler(this)

    private class MyHandler(context: SocketManager) : Handler(Looper.getMainLooper()) {

        private val reference: WeakReference<SocketManager> = WeakReference(context)

        override fun handleMessage(msg: Message) {
            val socketManager = reference.get() as SocketManager? ?: return
            when (msg.what) {
                1 -> {
                    socketManager.receivedLiveMessage()
                }
                2 -> {
                    socketManager.receivedLiveMessage()

                    //模拟每0.5秒推送来一条数据
                    socketManager.handler.sendEmptyMessageDelayed(2 , 500)
                }
            }
        }
    }

    private fun initFakeBeanJson(): String{
        val message = LiveMessageModel()
        message.userid = "123"
        message.name = "大鼓书"+abs(Random().nextInt() % 999)
        message.level = abs(Random().nextInt() % 50)
        message.medal = if (Random().nextInt() % 2 == 0)  "粉丝团" else "僵尸"

        //定好好9张图的名字前缀
        val sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.SSS")
        val time = sdf.format(Date())
        message.message = if (Random().nextInt() % 2 == 0)  ("我是大鼓书，这是我说的话，当前时间是$time") else "有问题加我QQ285275534"
        message.type = if (Random().nextInt() % 4 == 0) LiveMessageIntrinsicModel.TYPE_COMING else LiveMessageIntrinsicModel.TYPE_MESSAGE
        message.medalLevel = abs(Random().nextInt() % 90)

        if (Random().nextInt() % 3 == 0) {
            message.tips = "榜1"
        } else if (Random().nextInt() % 2 == 0) {
            message.tips = "VIP"
        } else {
            message.tips = null
        }

        return jsonAdapter.toJson(message)
    }

    //模拟收到直播间的《弹幕消息》，注意这里是弹幕消息。直播间其他消息注意区分：比如pk、主播下播
    fun receivedLiveMessage(){
        if (mConnectListener != null){
            //把刚收到的消息json回调给LiveActivity。
            // 本库作者DQ建议：其实这里用两种做法：
                //1，如果这个消息通道里只有"文字消息" 没有礼物。那么我建议回调原始json给Activity。因为这样可以视情况屏蔽过多的消息（压根就不解析json，减少内存和cpu损耗）
                //2，如果这个消息通道里有"礼物消息"，那么还是需要回调model，因为毕竟不适合丢弃礼物消息。然后再：if==文字消息 {视情况丢弃一部分}
                //本demo按照方案2来，毕竟绝大多数app也没那么高的并发。等你们用户量高到一定程度再升级为方案1

            val json = initFakeBeanJson();

            val messageModel = jsonAdapter.fromJson(jsonBuffer.writeUtf8(json))

            mConnectListener!!.onLiveMessageReceived(messageModel!!)
        }
    }

    //模拟一直收到消息
    fun receivedInfiniteLiveMessage(){
        handler.sendEmptyMessageDelayed(2 , 500)
    }

    //停止上面的模拟一直收到消息
    fun stopInfiniteLiveMessage(){
        handler.removeMessages(2)
    }

    interface ConnectListener {
        fun onLiveMessageReceived(liveMessageModel: LiveMessageModel)
    }
}