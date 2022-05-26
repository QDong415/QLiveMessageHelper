package com.dq.livemessagedemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dq.livemessagedemo.R
import com.dq.livemessagedemo.model.LiveMessageModel

class LiveMessageAdapter(val context: Context, val list: List<LiveMessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    //viewType分别为item以及空view
    private val VIEW_TYPE_ITEM: Int = 0

    //item上的控件的点击事件
    private var onItemClickListener: OnItemClickListener? = null

    //本库作者DQ：为了感知到"底部还有XX条消息"，希望你能在Adapter里实现这个方法，并回调给我的库
    var onItemViewAttachedListener: OnItemViewAttachedListener? = null

    //itemview数量
    override fun getItemCount(): Int {
        //如果不为0，按正常的流程跑
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    //创建ViewHolder并绑定上itemview
    //本库作者DQ：我发现如果很快速频繁的调用insertNotify，每次插入的又很多，就会系统就会触发onCreateViewHolder。推测可能是Rv来不及回收holder就需要用，就create一个新的
    //所以我不建议你把最短刷新间隔调的太低，我感觉0.6就是个不错的数字
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //在这里根据不同的viewType进行引入不同的布局
        val view: View = mInflater.inflate(R.layout.listitem_live_chat, parent, false)
        val viewHolder = TopicViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(viewHolder.itemView.tag as Int);
        }
        return viewHolder
    }

    //ViewHolder的view控件设置数据
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopicViewHolder) {
            holder.itemView.tag = position
            holder.name_tv.text = list.get(position).spannableString
//            holder.name_tv.text = list.get(position).message dqerror
        }
    }

    //本库作者DQ：为了感知到"底部还有XX条消息"，希望你能在Adapter里实现这个方法，并回调给我的库
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        onItemViewAttachedListener?.let {
            it.onItemViewAttached(holder.adapterPosition)
        }
    }

    //kotlin 内部类默认是static ,前面加上inner为非静态
    //自定义的RecyclerView.ViewHolder，构造函数需要传入View参数。相当于java的构造函数第一句的super(view);
    class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name_tv: TextView = view as TextView // view.findViewById(R.id.name_tv)

//        init {
//            name_tv.setTextColor(view.resources.getColor(R.color.sky_color))
//        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    //本库作者DQ：为了感知到"底部还有XX条消息"，希望你能在Adapter里实现这个方法，并回调给我的库
    interface OnItemViewAttachedListener {
        fun onItemViewAttached(adapterPosition: Int)
    }

}