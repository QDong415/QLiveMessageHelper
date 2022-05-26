package com.dq.livemessage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

//ImageSpan.ALIGN_CENTER 为 api29。为了兼容低版本android，我把源码复制过来了，到
class QCenterAlignImageSpan(context: Context, bitmap: Bitmap) : ImageSpan(context, bitmap) {

    private var mDrawableRef: WeakReference<Drawable>? = null

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b: Drawable = getCachedDrawable() ?: return
        canvas.save()

        val transY = top + (bottom - top) / 2 - b.bounds.height() / 2

        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }
 
    private fun getCachedDrawable(): Drawable? {
        val wr: WeakReference<Drawable>? = mDrawableRef
        var d: Drawable? = null
        if (wr != null) {
            d = wr.get()
        }
        if (d == null) {
            d = drawable
            mDrawableRef = WeakReference(d)
        }
        return d
    }
}