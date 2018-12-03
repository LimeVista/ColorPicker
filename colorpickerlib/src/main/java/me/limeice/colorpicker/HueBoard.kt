package me.limeice.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import me.limeice.gesture.MiniGesture


class HueBoard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /* 选色球大小 */
    private val mDotSize: Int

    private val mSmallDotSize: Int

    private val mPickerPaintSolid: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mDotDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_dot)!!

    private val gesture: MiniGesture = MiniGesture(context)

    private val oldSize = intArrayOf(0, 0)

    private val mPaintDrawable: PaintDrawable

    @ColorInt var inPutColor: Int = Color.RED

    /**
     * 获得Picker按钮位置
     */
    var pickerPositionX: Float = 0f
        private set

    var pickerPositionY: Float = 0f
        private set

    @ColorInt var pickColor: Int = Color.RED
        private set

    /**
     * 颜色改变监听
     */
    var colorChangeListener: (Int) -> Unit = { }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val small = resources.configuration.smallestScreenWidthDp < 600
        mDotSize = if (small) dp2px(32f) else dp2px(40f)
        mSmallDotSize = Math.round(mDotSize * 0.70f)
        mPickerPaintSolid.style = Paint.Style.FILL
        mPickerPaintSolid.color = pickColor
        setPadding(0, 0, 0, 0)
        mDotDrawable.setBounds(-mDotSize, -mDotSize, mDotSize, mDotSize)
        mPaintDrawable = PaintDrawable()
        initDrawable()
        registerEvent()
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
        mPaintDrawable.setBounds(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        mPaintDrawable.draw(canvas)
        canvas.translate(pickerPositionX, pickerPositionY)
        mDotDrawable.draw(canvas)
        canvas.drawCircle(0f, 0f, mSmallDotSize.toFloat(), mPickerPaintSolid)
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            pickerPositionX = event.x
            pickerPositionY = event.y
            proxyCheckTapAndDrag()
        }
        return gesture.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun initDrawable() {
        mPaintDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                val x = LinearGradient(0f, 0f, width.toFloat(), 0f, Color.WHITE, inPutColor, Shader.TileMode.CLAMP)
                val y = LinearGradient(0f, 0f, 0f, height.toFloat(), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
                return ComposeShader(x, y, PorterDuff.Mode.MULTIPLY)
            }
        }
        mPaintDrawable.shape = RectShape()
    }

    /* 更新画布 */
    fun updateDrawable() = apply {
        initDrawable()
    }

    /* 注册按钮 */
    private fun registerEvent() {
        gesture.setDrag { _, dx, dy ->
            pickerPositionX += dx
            pickerPositionY += dy
            proxyCheckTapAndDrag()
        }.setTap { e ->
            pickerPositionX = e.x
            pickerPositionY = e.y
            proxyCheckTapAndDrag()
        }
    }

    /* 点击、拖动事件范围检查 */
    private fun proxyCheckTapAndDrag() {
        pickerPositionX = when {
            pickerPositionX < 0 -> 0f
            pickerPositionX > width -> width.toFloat()
            else -> pickerPositionX
        }

        pickerPositionY = when {
            pickerPositionY < 0 -> 0f
            pickerPositionY > height -> height.toFloat()
            else -> pickerPositionY
        }

        pickColor = calcColor()
        colorChangeListener(pickColor)
        mPickerPaintSolid.color = pickColor
        invalidate()
    }

    /**
     * 刷新
     */
    fun update(): Unit = proxyCheckTapAndDrag()

    /**
     * 设置坐标位置
     * @param x 横坐标
     * @param y 纵坐标
     */
    fun setPosition(x: Float, y: Float) {
        pickerPositionX = x
        pickerPositionY = y
        proxyCheckTapAndDrag()
    }

    /**
     * 设置选色器颜色并且移动到指定颜色位置
     * @param pickColor 颜色
     */
    fun setPickColorMoveToPosition(@ColorInt pickColor: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(pickColor, hsv)
        inPutColor = Color.HSVToColor(floatArrayOf(hsv[0], 1f, 1f))
        this.pickColor = pickColor
        pickerPositionX = width * hsv[1]
        pickerPositionY = height * (1 - hsv[2])
        mPickerPaintSolid.color = pickColor
        colorChangeListener(pickColor)
        initDrawable()
        invalidate()
    }

    /* 平方 */
    private fun Float.square() = this * this

    private infix fun MotionEvent.isMove(r: Int): Boolean = (pickerPositionX - x).square() + (pickerPositionY - y).square() <= (r * 2f).square()

    @ColorInt
    private fun calcColor(): Int {
        val gray = (0XFF - pickerPositionY / height * 0XFF).toInt()
        val grayColor: Int = (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
        val hintPercent = pickerPositionX / width
        val hintColor: Int = (0xFF shl 24) or
                (0xFF + ((inPutColor.r() - 0XFF) * hintPercent).toInt() shl 16) or
                (0xFF + ((inPutColor.g() - 0XFF) * hintPercent).toInt() shl 8) or
                (0xFF + (inPutColor.b() - 0XFF) * hintPercent).toInt()
        return grayColor multiply hintColor // 正片叠底
    }

    @Deprecated("Use calcColor()")
    @ColorInt
    private fun calcColorHSV(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(inPutColor, hsv)
        hsv[1] = pickerPositionX / width
        hsv[2] = 1 - (pickerPositionY / height)
        return Color.HSVToColor(hsv)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = getViewSize(dp2px(16f), widthMeasureSpec)
        val h = (w * 0.8f).toInt()
        setMeasuredDimension(w, h)
    }

    private fun getViewSize(defaultSize: Int, measureSpec: Int): Int {
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        return when (mode) {
            View.MeasureSpec.UNSPECIFIED -> defaultSize //如果没有指定大小，就设置为默认大小
            View.MeasureSpec.EXACTLY -> size // 如果测量模式是最大取值为size,我们将大小取最大值,你也可以取其他值
            View.MeasureSpec.AT_MOST -> size //如果是固定的大小，那就不要去改变它
            else -> defaultSize
        }
    }
}

