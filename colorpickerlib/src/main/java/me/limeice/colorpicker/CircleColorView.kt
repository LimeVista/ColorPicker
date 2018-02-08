package me.limeice.colorpicker

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View

class CircleColorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private val bigCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val midCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val smallCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val penWidth: Int = dp2px(1.5f)

    @ColorInt
    var circleColor: Int = Color.GREEN

    var checked: Boolean = false
        set(value) {
            if (value == field) return
            field = value
            invalidate()
            if (value)
                zoomIn(this, 200)
            else
                zoomOut(this, 200)
        }

    init {
        smallCirclePaint.isAntiAlias = true
        smallCirclePaint.strokeWidth = penWidth - 1f
        smallCirclePaint.style = Paint.Style.STROKE
        smallCirclePaint.color = Color.WHITE

        midCirclePaint.color = circleColor
        midCirclePaint.style = Paint.Style.FILL

        bigCirclePaint.isAntiAlias = true
        bigCirclePaint.strokeWidth = penWidth.toFloat()
        bigCirclePaint.style = Paint.Style.STROKE

        calcColor()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = (width shr 1).toFloat()
        val h = (height shr 1).toFloat()
        canvas.drawCircle(w, h, w - penWidth, midCirclePaint)
        canvas.drawCircle(w, h, (width - penWidth shr 1).toFloat(), bigCirclePaint)
        if (checked) canvas.drawCircle(w, h, w - penWidth * 1.5f, smallCirclePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var w = getViewSize(dp2px(16f), widthMeasureSpec)
        var h = getViewSize(dp2px(16f), heightMeasureSpec)
        if (w < h) h = w else w = h
        setMeasuredDimension(w, h)
    }


    private fun getViewSize(defaultSize: Int, measureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        return when (mode) {
            View.MeasureSpec.UNSPECIFIED -> defaultSize //如果没有指定大小，就设置为默认大小
            View.MeasureSpec.AT_MOST -> size//如果测量模式是最大取值为size
            View.MeasureSpec.EXACTLY -> size //如果是固定的大小，那就不要去改变它
            else -> defaultSize
        }
    }

    /**
     * 计算边框颜色
     */
    private fun calcColor() {
        var r = circleColor and 0x00FF0000 shr 16
        r = if (r > 10) r - 10 else 0
        var g = circleColor and 0x0000FF00 shr 8
        g = if (g > 20) g - 20 else 0
        var b = circleColor and 0xFF
        b = if (b > 10) b - 10 else 0
        bigCirclePaint.color = 0xFF shl 24 or (r shl 16) or (g shl 8) or b
    }

    private fun zoomIn(v: View, duration: Int) {
        ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.25f)
                .setDuration(duration.toLong())
                .start()
        ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 1.25f)
                .setDuration(duration.toLong())
                .start()

    }

    private fun zoomOut(v: View, duration: Int) {
        ObjectAnimator.ofFloat(v, "scaleX", 1.25f, 1.0f)
                .setDuration(duration.toLong())
                .start()
        ObjectAnimator.ofFloat(v, "scaleY", 1.25f, 1.0f)
                .setDuration(duration.toLong())
                .start()
    }

    fun setColor(@ColorInt colorInt: Int) {
        if (circleColor == colorInt) return
        circleColor = colorInt
        midCirclePaint.color = circleColor
        calcColor()
        invalidate()
    }
}
