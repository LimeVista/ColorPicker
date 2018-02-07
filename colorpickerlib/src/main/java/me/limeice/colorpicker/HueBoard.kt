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
class HueBoard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaddingBoth: Int

    private var mGradientX: LinearGradient
    private var mGradientY: LinearGradient

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mPickerPaintSolid: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRect: RectF = RectF()

    private val mDotDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_dot)

    private val gesture: MiniGesture = MiniGesture(context)

    private var mComposeShader: ComposeShader

    @ColorInt var pickColor: Int = Color.RED

    private val oldSize = intArrayOf(0, 0)

    /**
     * 获得Picker按钮位置
     */
    var pickerPositionX: Float = 0f
        private set

    var pickerPositionY: Float = 0f

    /**
     * 颜色改变监听
     */
    var colorChangeListener: (Int) -> Unit = { _ -> }

    init {
        val small = resources.configuration.smallestScreenWidthDp < 600
        mPaddingBoth = if (small) dp2px(4f) else dp2px(6f)
        mPaint.isAntiAlias = true
        mPickerPaintSolid.style = Paint.Style.FILL
        mPickerPaintSolid.color = pickColor
        mGradientX = LinearGradient(0f, 0f, width.toFloat(), 0f, Color.TRANSPARENT, pickColor, Shader.TileMode.CLAMP)
        mGradientY = LinearGradient(0f, 0f, 0f, height.toFloat(), Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP)
        mComposeShader = ComposeShader(mGradientY, mGradientX, PorterDuff.Mode.MULTIPLY)
        mPaint.shader = mComposeShader
        gesture.setDrag { _, dx, dy ->
            pickerPositionX += dx
            pickerPositionY += dy
            proxyCheckTapAndDrag()
        }.setTap { e ->
                    pickerPositionX = e.x
                    pickerPositionY = e.y
                    proxyCheckTapAndDrag()
                }
        setPadding(0, 0, 0, 0)
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * 设置picker按钮位置
     */
    fun setPickerPosition(position: Float) = apply {
        pickerPositionX = position
        proxyCheckTapAndDrag()
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (oldSize[0] == width && oldSize[1] == height) return
        oldSize[0] = width
        oldSize[1] = height
        mGradientX = LinearGradient(0f, 0f, width.toFloat(), 0f, Color.TRANSPARENT, pickColor, Shader.TileMode.CLAMP)
        mGradientY = LinearGradient(0f, 0f, 0f, height.toFloat(), Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP)
        mComposeShader = ComposeShader(mGradientY, mGradientX, PorterDuff.Mode.MULTIPLY)
        mPaint.shader = mComposeShader
        mRect.set(0f, mPaddingBoth.toFloat(), width.toFloat(), height.toFloat() - mPaddingBoth)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = height / 2f
        val p = pickerPositionX.toInt()
        canvas.drawRoundRect(mRect, 36f, 36f, mPaint)
        // mDotDrawable.setBounds(p, 0, p + height, height)
        // mDotDrawable.draw(canvas)
        // canvas.drawCircle(pickerPositionX, r, r - mPaddingBoth, mPickerPaintSolid)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
            gesture.onTouchEvent(event) || super.onTouchEvent(event)

    private fun proxyCheckTapAndDrag() {
        if (pickerPositionX < 0)
            pickerPositionX = 0f
        else if (pickerPositionX > width)
            pickerPositionX = width.toFloat()
        pickColor = calcColor()
        colorChangeListener(pickColor)
        mPickerPaintSolid.color = pickColor
        invalidate()
    }

    @ColorInt
    private fun calcColor(): Int = Color.WHITE
}

