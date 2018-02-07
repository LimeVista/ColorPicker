package me.limeice.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import me.limeice.gesture.MiniGesture

@Suppress("unused")
class ColorCard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        @ColorInt
        private val colors: IntArray = intArrayOf(0xFFFF0000.toInt(), 0xFFD100FF.toInt(), 0xFF0A00FF.toInt(),
                0xFF00D5FF.toInt(), 0xFF00FF32.toInt(), 0xFFF4FF02.toInt(), 0xFFFF0000.toInt())
    }

    private val mPaddingBoth: Int

    private val mLockHeight: Int

    private var mGradient: LinearGradient

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mPickerPaintSolid: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRect: RectF = RectF()

    private val mDotDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_dot)

    private val gesture: MiniGesture = MiniGesture(context)

    private val oldSize = intArrayOf(0, 0)

    /**
     * 获得Picker按钮位置
     */
    var pickerPosition: Float
        private set

    /**
     * 颜色改变监听
     */
    var colorChangeListener: (Int) -> Unit = { _ -> }

    /**
     * 获取颜色
     */
    @ColorInt var pickColor: Int = colors[0]
        private set

    init {
        val small = resources.configuration.smallestScreenWidthDp < 600
        mPaddingBoth = if (small) dp2px(4f) else dp2px(6f)
        mLockHeight = if (small) dp2px(20f) else dp2px(28f)
        pickerPosition = mLockHeight / 2f
        mPaint.isAntiAlias = true
        mPickerPaintSolid.style = Paint.Style.FILL
        mPickerPaintSolid.color = pickColor
        mGradient = LinearGradient(0f, 0f, width.toFloat() - mLockHeight, 0f, colors, null, Shader.TileMode.CLAMP)

        gesture.setDrag { _, dx, _ ->
            pickerPosition += dx
            proxyCheckTapAndDrag()
        }.setTap { e ->
                    pickerPosition = e.x
                    proxyCheckTapAndDrag()
                }
        setPadding(0, 0, 0, 0)
    }

    /**
     * 设置picker按钮位置
     */
    fun setPickerPosition(position: Float) = apply {
        pickerPosition = position
        proxyCheckTapAndDrag()
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (oldSize[0] == width && oldSize[1] == height) return
        oldSize[0] = width
        oldSize[1] = height
        mGradient = LinearGradient(0f, 0f, width.toFloat() - mLockHeight, 0f, colors, null, Shader.TileMode.CLAMP)
        mPaint.shader = mGradient
        mRect.set(mLockHeight / 2f, mPaddingBoth.toFloat(), width.toFloat() - mLockHeight / 2f, height.toFloat() - mPaddingBoth)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = height / 2f
        val p = (pickerPosition + 0.5f - mLockHeight / 2).toInt()
        canvas.drawRoundRect(mRect, r, r, mPaint)
        mDotDrawable.setBounds(p, 0, p + height, height)
        mDotDrawable.draw(canvas)
        canvas.drawCircle(pickerPosition, r, r - mPaddingBoth, mPickerPaintSolid)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, mLockHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
            gesture.onTouchEvent(event) || super.onTouchEvent(event)

    private fun proxyCheckTapAndDrag() {
        val h2 = mLockHeight / 2f
        if (pickerPosition < h2)
            pickerPosition = h2
        else if (pickerPosition > width - h2)
            pickerPosition = width - h2
        pickColor = calcColor()
        colorChangeListener(pickColor)
        mPickerPaintSolid.color = pickColor
        invalidate()
    }

    @ColorInt
    private fun calcColor(): Int {
        val h = mLockHeight / 2f
        if (pickerPosition <= h)
            return colors[0]
        if (pickerPosition >= width - h)
            return colors.last()
        val p = pickerPosition - h
        val range = (width - mLockHeight) / (colors.size - 1)
        val index = (p / range).toInt()
        val percent = (p % range) / range
        val c1 = colors[index]
        val c2 = colors[index + 1]
        return (0xFF shl 24) or
                ((c1.r() + (c2.r() - c1.r()) * percent).toInt() shl 16) or
                ((c1.g() + (c2.g() - c1.g()) * percent).toInt() shl 8) or
                (c1.b() + (c2.b() - c1.b()) * percent).toInt()
    }
}
