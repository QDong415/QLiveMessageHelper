package com.dq.livemessagedemo

import android.content.Context
import android.graphics.Rect
import android.os.*
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dq.livemessage.ImageSpanCacheInstance
import com.dq.livemessage.LiveMessageRecyclerHelper
import com.dq.livemessagedemo.model.LiveMessageModel
import com.dq.livemessagedemo.tool.*
import java.lang.ref.WeakReference

class LiveActivity : AppCompatActivity(), SocketManager.ConnectListener {

    //View
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: LiveMessageAdapter
    private lateinit var unreadTipsTextView: TextView

    //辅助类
    private val liveMessageRecyclerHelper: LiveMessageRecyclerHelper<LiveMessageModel> by lazy {
        LiveMessageRecyclerHelper<LiveMessageModel>(this)
    }

    //直播间 - 左下角公屏RV - 其中一条Item消息 - TextView的解析策略
    private val liveMessageTextViewHelper: LiveMessageTextViewHelper by lazy {
        val liveMessageTextViewHelper = LiveMessageTextViewHelper(this)
        liveMessageTextViewHelper.imageSpanStrategyList = arrayListOf()
        liveMessageTextViewHelper.imageSpanStrategyList.apply {
            //解析textview头tag的策略
            add(LiveMessageImageSpanTitleStrategy())
            //解析用户等级的策略
            add(LiveMessageImageSpanLevelStrategy())
            //解析勋章的策略
            add(LiveMessageImageSpanMedalStrategy())
        }

        liveMessageTextViewHelper.textSpanStrategyList = arrayListOf()
        liveMessageTextViewHelper.textSpanStrategyList.apply {
            //解析用户昵称的策略
            add(LiveMessageTextSpanNameStrategy())
            //解析消息体的策略
            add(LiveMessageTextSpanMessageStrategy())
        }
        liveMessageTextViewHelper
    }

    private lateinit var socketManager: SocketManager

    //测试用的。1秒内有几条新消息来
    private var messageCountInOneSecond = 0

    //测试用的。缓存区最多消息数量。实际开发时候你不需要把这个设置为全局变量
    private var maxCacheCount = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        socketManager = QApplication.instance.socketManager
        socketManager.mConnectListener = this

        initView()
    }

    private fun initView() {

        recyclerView = findViewById(R.id.recycler_view)
        unreadTipsTextView = findViewById(R.id.unread_tv)

        //设置RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State ) {
                outRect.bottom = dip2px(7f)
            }
        })

        //RecyclerView创建Adapter
        mAdapter = LiveMessageAdapter(this, liveMessageRecyclerHelper.list)
        recyclerView.adapter = mAdapter
        //本库作者DQ：为了感知到"底部还有XX条消息"，希望你能在Adapter里实现这个方法，并回调给我的库
        mAdapter.onItemViewAttachedListener = object: LiveMessageAdapter.OnItemViewAttachedListener {
            override fun onItemViewAttached(adapterPosition: Int) {
                liveMessageRecyclerHelper.onAdapterItemViewAttached(adapterPosition)
            }
        }

        //给我们的库 liveMessageRecyclerHelper，绑定recyclerView
        liveMessageRecyclerHelper.setRecyclerView(recyclerView)
        liveMessageRecyclerHelper.setUnreadTipsView(unreadTipsTextView)
        liveMessageRecyclerHelper.messageRecyclerHelperListener = object: LiveMessageRecyclerHelper.LiveMessageRecyclerHelperListener<LiveMessageModel> {
            override fun unreadMessageCountUpdate(unreadCount: Int) {
                unreadTipsTextView.setText("还有"+unreadCount +"条未读")
            }

            override fun asyncParseSpannableString(model: LiveMessageModel) {
                //这一步是在子线程中
                model.spannableString = liveMessageTextViewHelper.displaySpannableString(model)
            }
        }

        //RecyclerView最短刷新时间间隔（0秒 - 2.0秒）
        liveMessageRecyclerHelper.diffRefreshDuration = (intent.getFloatExtra("minRefreshTime",0.6f) * 1000).toLong()
        //缓存区最多消息数量。实际开发时候你不需要把这个设置为全局变量
        maxCacheCount = intent.getIntExtra("maxCacheCount", 5)

        findViewById<View>(R.id.insert_tv).setOnClickListener {
            //模拟来一条消息
            socketManager.receivedLiveMessage()
        }

        findViewById<View>(R.id.insert_infinite_tv).setOnClickListener {
            //模拟无限来消息
            messageCountInOneSecond += 2;
            it as TextView
            it.text = "自动每秒插入"+messageCountInOneSecond+"条ing"
            socketManager.receivedInfiniteLiveMessage()
        }

        findViewById<View>(R.id.stop_tv).setOnClickListener {
            messageCountInOneSecond = 0
            findViewById<TextView>(R.id.insert_infinite_tv).text = "自动每秒插入"+2+"条"
            socketManager.stopInfiniteLiveMessage()
        }

        unreadTipsTextView.setOnClickListener { v ->
            //点击"底部还有N条消息"
            v.visibility = View.GONE

            liveMessageRecyclerHelper.scrollToBottom()
        }
    }

    //收到服务器推送来的新消息
    override fun onLiveMessageReceived(liveMessageModel: LiveMessageModel) {

        Log.e("dq","AC 最原始收到消息")

        if (liveMessageRecyclerHelper.willInsertList.count() > maxCacheCount){
            //缓冲区满了，丢弃这条消息。你也可以判断一下if == 礼物消息 就不丢弃
            Log.e("dq","缓冲区满了，丢弃掉")
        } else {
            //准备异步拼接SpannableString。调用完这个方法，会触发asyncParseSpannableString回调
            liveMessageRecyclerHelper.prepareAsyncParseSpannable(liveMessageModel)
        }
    }

    fun dip2px(dpValue: Float): Int {
        val scale: Float = getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    //FM.onPause -> AC.onPause -> FM.onStop -> AC.onStop -> FM.onDestroyView -> FM.onDestroy -> FM.onDetach -> AC.onDestroy
    override fun onDestroy() {
        super.onDestroy()
        socketManager.mConnectListener = null
        liveMessageRecyclerHelper.destroy()
        ImageSpanCacheInstance.instance.clearAll()
    }

}