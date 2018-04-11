package me.limeice.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View

/**
 * CompareColorPanel
 * Created by Lime on 2018/12/8.
 */
class CompareColorPanel @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 源颜色
     */
    @ColorInt var srcColor: Int = 0xFF4C5A79.toInt()
        set(value) {
            field = value
            srcShapeDrawable.paint.color = field
            postInvalidate()
        }

    /**
     * 对比颜色
     */
    @ColorInt var dstColor: Int = Color.GREEN
        set(value) {
            field = value
            dstShapeDrawable.paint.color = field
            postInvalidate()
        }
    /**
     * 源颜色生成圆角矩形
     */
    private val srcShapeDrawable: ShapeDrawable

    /**
     * 目标颜色生成圆角矩形
     */
    private val dstShapeDrawable: ShapeDrawable

    /**
     * An array of 8 radius values, for the inner roundrect.
     * The first two floats are for the top-left corner
     *(remaining pairs correspond clockwise).
     */
    private val srcOuterRadii = FloatArray(8)       // 圆角数组

    /**
     * An array of 8 radius values, for the inner roundrect.
     * The first two floats are for the top-left corner
     *(remaining pairs correspond clockwise).
     */
    private val destOuterRadii = FloatArray(8)      // 圆角数组

    init {
        srcShapeDrawable = ShapeDrawable(RoundRectShape(srcOuterRadii, null, null))
        dstShapeDrawable = ShapeDrawable(RoundRectShape(destOuterRadii, null, null))
        srcShapeDrawable.paint.let {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.color = srcColor
        }
        dstShapeDrawable.paint.let {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.color = dstColor
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        fixOuterRadii()
        srcShapeDrawable.setBounds(0, 0, width shr 1, height)
        dstShapeDrawable.setBounds(width shr 1, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        srcShapeDrawable.draw(canvas)
        dstShapeDrawable.draw(canvas)
    }

    /**
     * 修复圆角矩形显示效果
     */
    private fun fixOuterRadii() {
        val h = height / 2f
        srcOuterRadii[0] = h; srcOuterRadii[1] = h
        srcOuterRadii[6] = h; srcOuterRadii[7] = h
        destOuterRadii[2] = h; destOuterRadii[3] = h
        destOuterRadii[4] = h; destOuterRadii[5] = h
    }
}