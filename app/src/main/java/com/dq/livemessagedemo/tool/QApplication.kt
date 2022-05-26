package com.dq.livemessagedemo.tool

import android.app.Application
import android.util.Log
import com.didichuxing.doraemonkit.DoKit
import com.dq.livemessagedemo.model.LiveMessageIntrinsicModel
import com.dq.livemessagedemo.model.LiveMessageModel
import com.squareup.moshi.Moshi
import java.util.*

class QApplication : Application(){

    companion object {
        open lateinit var instance: QApplication
    }

    lateinit var socketManager: SocketManager
        private set

    //Tips: 手机开发者选项里设置为后台进程为2，然后进入后台，点微信，再回来app，结果：
    // 这时候重新走了这里的Application onCreate，但是MainVC的onCreate方法中，是从tag启动FM（saveBundle!=null),且tabRootFM里的子FM也是如此
    override fun onCreate() {
        super.onCreate()

        instance = this
        socketManager = SocketManager()
        DoKit.Builder(this).build()
    }

}