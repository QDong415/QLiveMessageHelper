# QLiveMessage

## 简介：

直播间聊天消息RecyclerView列表，直播间RecyclerView。采用3层缓存机制，一秒内收到几百条消息依然不卡顿。
ChatRoom in LiveRoom.Using the three kind of cache strategy and async in HandlerThread.Can carry many messages

## 安装体验：
![](https://upload-images.jianshu.io/upload_images/26002059-97c094d65ebedd5f.png)

## 功能（优点）：
- ✅采用3层缓存机制 + 异步解析ImageSpan。一秒内收到几百条消息依然不卡顿
- ✅可配置RecyclerView最短刷新时间间隔
- ✅可配置在短时间内如果收到太多消息可选择丢弃消息
- ✅根据缓冲区消息数量选择不同的ScrollToBottom速度
- ✅Demo中提供"底部还有XX条未读消息"的处理方式
- ✅Demo中提供弹幕中仿抖音的"粉丝团"标签处理方式
- ✅内存占用控制的很好，收到再多消息帧数也控制在55以上
- ✅完美解耦，无内存泄漏，接入很简单

## 具体技术说明：
- 第一层缓存：消息的标签ImageSpan缓存，比如"榜1" "等级" "粉丝团等级"<bar />
- 第二层缓存：收到消息时候，检查上次刷新时间。如果刚上次刚刷新不到0.n秒，那么就插入到缓冲区。0.n秒后将全部缓冲区显示出来再清空缓冲区<bar />
- 第三层缓存：model中的SpannableString。让用户手指滚动列表更顺畅，无需再次解析SpannableString<bar />
- 额外缓存：解析标签ImageSpan时候全程只采用一个TextView，避免每次都new，json解析也是如此

- 异步解析：解析完整的（3个）SpannableString在低性能手机上最慢需要20-40ms。会造成丢帧。本库采用HandlerThread解析<bar />
- 提供策略模式解析标签ImageSpan，拓展性极强

## 效果gif图（Gif图有点卡，实际运行一点都不卡）：
![](https://upload-images.jianshu.io/upload_images/26002059-536ebf49fa017bd3.gif)
![](https://upload-images.jianshu.io/upload_images/26002059-4809cca72bd1c551.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)

## 导入
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.QDong415:QLiveMessageHelper:v1.0'
	}
```

## 使用

```java

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

```


## Author：DQ

有问题联系QQ：285275534, 285275534@qq.com