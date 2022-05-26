# QLiveMessage

## 简介：

下拉拖拽关闭Activity，下拉返回Activity。仿大众点评、快手、小红书详情界面：可下滑关闭详情界面。
Drag down to close activity

## 安装体验：
![](https://upload-images.jianshu.io/upload_images/26002059-94273eadb7cf0295.png)

## 功能（优点）：
- ✅Demo包含`瀑布列表跳转到详情，带动画`+`详情可左滑进入个人主页`+`下拉拖拽关闭Activity`
- ✅到为了让Activity的xml布局层级最少，只需要把本库设置为最外层的RelativeLayout
- ✅仿大众点评：下拉过程中除了图片，别的部分随着下拉距离而半透明
- ✅仿快手：fling快速下滑也可触发关闭
- ✅详情界面可左滑进入个人主页，你可以自己实现懒加载
- ✅解决下拉返回ImageView闪一下问题
- ✅完美解耦，可轻松让你的任何Activity实现下拉关闭效果

## 作者说明：
- Android系统的Activity过场动画会让shareElementImageView.setAlpha(0)；然后回退动画结束再进行.setAlpha(1)<bar />
- 这样会导致一个问题：我们下拉返回的时候，由于弹回动画是我们自己做的。但是系统依然会再进行一遍.setAlpha(1)，导致回弹动画结束时候图片会闪一下。参考下面的第1个gif<bar />
- 为了解决"闪一下"的问题，我用这种方法把他提前设为.setAlpha(1)<bar />


## 效果gif图（Gif图有点卡，实际运行一点都不卡）：
![](https://upload-images.jianshu.io/upload_images/26002059-96c272f540bddb21.gif)
![](https://upload-images.jianshu.io/upload_images/26002059-da019a1de650eca8.gif)

## 导入
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.QDong415:QLiveMessage:v1.0.1'
	}
```

## 使用

```java
        QDragRelativeLayout contentLayout = findViewById(R.id.drag_layout);
        contentLayout.setOnLiveMessageListener(this);
        //传入列表的点击项目的ImageView的坐标
        contentLayout.setupFromImageView(fromX, fromY, fromWidth, fromHeight, transition_share_view);
```

```xml
    <declare-styleable name="QLiveMessage">
        <!-- 是否可以手势下拉，默认true -->
        <attr name="dragEnable" format="boolean" />
        <!-- 下拉距离占总height百分之多少就触发关闭，0 - 1之间，默认0.2 -->
        <attr name="closeYRatio" format="float" />
        <!-- 手指快速下滑也可以触发关闭，默认true -->
        <attr name="flingCloseEnable" format="boolean" />
        <!-- 手势下拉过程中，其他View根据滑动距离半透明，默认false -->
        <attr name="alphaWhenDragging" format="boolean" />
        <!-- 关闭动画耗时，默认450 -->
        <attr name="closeAnimationDuration" format="integer" />
        <!-- 下拉力度不够，反弹回正常状态动画耗时，默认200 -->
        <attr name="rollToNormalAnimationDuration" format="integer" />
    </declare-styleable>
```


## Author：DQ

有问题联系QQ：285275534, 285275534@qq.com