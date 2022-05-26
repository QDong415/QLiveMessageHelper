package com.dq.livemessage

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class LiveMessageRecyclerHelper<T> {

    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    //主要消息列表，显示在Adapter中的
    val list: MutableList<T> = arrayListOf()

    //缓冲区的消息列表
    val willInsertList: MutableList<T> = arrayListOf()

    //聊天列表，由Activity传过来
    private lateinit var recyclerView: RecyclerView

    //未读提示TipsView，由Activity传过来，可为null
    private var unreadTipsView: View? = null

    //曾经读过的最大的Position
    private var readedMaxPosition = 0

    //正在滚动到底部的动画中
    private var isScrollingToBottom = false

    //测试
    private var lastTempTime = System.currentTimeMillis()

    //最后刷新时间
    private var lastRefreshTime: Long = 0

    //0.6秒刷新一次
    var diffRefreshDuration: Long = 600

    //刷新RecyclerView
    private val HANDLER_MESSAGE_REFRESH = 1

    //插入到临时缓存区
    private val HANDLER_INSERT_AND_DELAY = 2

    //通知Activity还有几条未读消息："底部还有XX条消息"
    var messageRecyclerHelperListener: LiveMessageRecyclerHelperListener<T>? = null

    //主线程的handler
    private val mMainHandler: MainHandler<T> = MainHandler(this);

    private class MainHandler<T>(helper: LiveMessageRecyclerHelper<T>) : Handler(Looper.getMainLooper()) {

        private val reference: WeakReference<LiveMessageRecyclerHelper<T>> = WeakReference(helper)

        override fun handleMessage(msg: Message) {

            val helper = reference.get() as LiveMessageRecyclerHelper<T>? ?: return

            when (msg.what) {

                helper.HANDLER_MESSAGE_REFRESH -> {
                    //刚刚短时间内（0.n秒）出现了>1条消息，那么最后一条消息会触发到这里。我们需要和list.addAll

                    //willInsertList一定有值
                    helper.list.addAll(helper.willInsertList)

                    helper.notifyAndScrollToBottom(helper.willInsertList.size)
                    //释放内存
                    helper.willInsertList.clear()
                }

                helper.HANDLER_INSERT_AND_DELAY -> {
                    var model: T = msg.obj as T
                    helper.insertItemAndDelayRefresh(model)
                }
            }
        }
    }

    @Volatile
    private var mServiceLooper: Looper? = null

    //子线程的handler
    @Volatile
    private var mAsyncHandler: AsyncHandler<T>? = null

    private class AsyncHandler<T>(helper:  LiveMessageRecyclerHelper<T>, looper: Looper) : Handler(looper) {

        private val reference: WeakReference<LiveMessageRecyclerHelper<T>> = WeakReference(helper)

        override fun handleMessage(msg: Message) {

            val helper = reference.get() as LiveMessageRecyclerHelper<T>? ?: return

            when (msg.what) {
                1 -> {
                    var model: T = msg.obj as T

                    helper.lastTempTime = System.currentTimeMillis()

                    helper.messageRecyclerHelperListener?.asyncParseSpannableString(model)

                    val mainMsg = helper.mMainHandler.obtainMessage()
                    mainMsg.what = helper.HANDLER_INSERT_AND_DELAY
                    mainMsg.obj = model
                    helper.mMainHandler.sendMessage(mainMsg)
                }
            }
        }
    }

    open fun prepareAsyncParseSpannable(t: T){
        val msg = mAsyncHandler!!.obtainMessage()
        msg.what = 1
        msg.obj = t
        mAsyncHandler!!.sendMessage(msg)
    }

    private fun initThread() {
        val thread = HandlerThread("ParseSpannable")
        thread.start()

        mServiceLooper = thread.looper
        mAsyncHandler = AsyncHandler(this, mServiceLooper!!)
    }

    //由Activity\Fragment 传入RecyclerView
    open fun setRecyclerView(recyclerView: RecyclerView){
        initThread()
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //这个回调触发不频繁
                //手指拨动（无论是否发生滚动）newState ：1 -> 2 -> 0 (停下来)
                //代码滚动 newState ：2 -> 0 (停下来)
                //滚动过程中用手指再滚 newState ：2 -> 1（手指摸）-> 2 -> 0 (停下来)
                //滚动过程中用手指按住不然他滚了 newState ：2 -> 1（手指摸）-> 0 (停下来)

                lastTempTime = System.currentTimeMillis()
                //获取最后一个可见view的位置

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //停下来了
                    isScrollingToBottom = false

                    if (isBottom(recyclerView)) {//停在了底部
                        unreadTipsView?.visibility = View.GONE
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //用手指去触摸了
                    isScrollingToBottom = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
        })
    }

    //由Activity\Fragment 传入unreadTipsView
    open fun setUnreadTipsView(unreadTipsView: View) {
        this.unreadTipsView = unreadTipsView
    }

    //限定主线程调用，可以高频调用
    open fun insertItemAndDelayRefresh(item: T) {

        //距离上次刷新RecyclerView的时间差
        var diff = System.currentTimeMillis() - lastRefreshTime

        //移除等待队列中的刷新任务
        mMainHandler.removeMessages(HANDLER_MESSAGE_REFRESH)

        if (diff >= diffRefreshDuration){
            //0.6秒内没触发过刷新，就立即刷新

            if (willInsertList.isEmpty()){
                //当前并没有在缓存中的消息，就直接插入到主list
                list.add(item)

                notifyAndScrollToBottom(1)
            } else {
                //当前还有在缓存中的消息，add到主list
                list.addAll(willInsertList)
                list.add(item)

                notifyAndScrollToBottom(willInsertList.size + 1)//+1是加上最新的item
                //释放内存
                willInsertList.clear()
            }

        } else {
            //0.6秒内刚刚触发了刷新，就先缓存起来，然后延后再list.add()和刷新
            willInsertList.add(item)

            mMainHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH, diffRefreshDuration)
        }
    }

    open fun scrollToBottom(){
        isScrollingToBottom = true
        recyclerView.layoutManager?.scrollToPosition(recyclerView.adapter!!.itemCount - 1)
    }

    open fun onAdapterItemViewAttached(adapterPosition: Int){
        if (readedMaxPosition < adapterPosition) {
            readedMaxPosition = adapterPosition
        }

        unreadTipsView?.let {
            if (it.visibility == View.VISIBLE) {
                //计算还有几条消息未读，然后回调给Activity
                val unreadCount = list.size - (readedMaxPosition + 1)
                if (unreadCount <= 0){
                    it.visibility = View.GONE
                }
                messageRecyclerHelperListener?.unreadMessageCountUpdate(unreadCount)
            }
        }
    }

    open fun destroy() {
        mServiceLooper?.let {
            it.quit()
        }
    }

    //notify + 滚到底部。调用该方法前，请先list.add()
    private fun notifyAndScrollToBottom(newCount: Int) {

        recyclerView.adapter!!.notifyItemRangeInserted(list.size - newCount , newCount)

        lastTempTime = System.currentTimeMillis()

        if (isBottom(recyclerView) || isScrollingToBottom){
            //现在就在底部，那么就要滚到底部

            //如果SmoothScroller设置为全局变量（不是每次都new），那么speed就是固定死的 无法动态调整。且RecyclerView源码还总是打log.e警告
            val scroller: AdjustLinearSmoothScroller = AdjustLinearSmoothScroller(mContext)
            //根据刚刚插入了几条item决定滚动速度，约小约快。这个很难做到完美，因为Item高度不同需要有不同的速度
            when (newCount) {
                1 -> scroller.setDuration(300f)
                2 -> scroller.setDuration(250f)
                3 -> scroller.setDuration(200f)
                4 -> scroller.setDuration(100f)
                5 -> scroller.setDuration(50f)
                else -> scroller.setDuration(25f)//25f是系统的smoothScrollToPosition的速度
            }
            scroller.targetPosition = recyclerView.adapter!!.itemCount - 1
            recyclerView.layoutManager!!.startSmoothScroll(scroller)

            isScrollingToBottom = true

            //这时候scroller.isRunning == true。但是如果我在滚动ing的时候，在别的地方（比如onClick）：scroller.isRunning 一直都 == false，原因不明

            lastTempTime = System.currentTimeMillis()

        } else {
            //现在不在底部，显示"更多"
            unreadTipsView?.visibility = View.VISIBLE

            //计算还有几条消息未读，然后回调给Activity
            val unreadCount = list.size - (readedMaxPosition + 1)
            messageRecyclerHelperListener?.unreadMessageCountUpdate(unreadCount)
        }

        //记录最后刷新时间，避免极短时间内重复insert和滚动
        lastRefreshTime = System.currentTimeMillis()
    }

    private fun isBottom(recyclerView: RecyclerView): Boolean {
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()
    }

    interface LiveMessageRecyclerHelperListener<T> {

        //计算还有几条消息未读，然后回调给Activity
        fun unreadMessageCountUpdate(unreadCount: Int)

        //让Activity 异步的解析SpannableString
        fun asyncParseSpannableString(model: T)
    }
}