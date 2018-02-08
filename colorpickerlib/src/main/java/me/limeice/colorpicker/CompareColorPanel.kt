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

    @ColorInt var srcColor: Int = 0xFF4C5A79.toInt()
        set(value) {
            field = value
            srcShapeDrawable.paint.color = field
            postInvalidate()
        }

    @ColorInt var dstColor: Int = Color.GREEN
        set(value) {
            field = value
            dstShapeDrawable.paint.color = field
            postInvalidate()
        }

    private val srcShapeDrawable: ShapeDrawable
    private val dstShapeDrawable: ShapeDrawable
    private val srcOuterRadii = FloatArray(8)
    private val destOuterRadii = FloatArray(8)

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

    private fun fixOuterRadii() {
        val h = height / 2f
        srcOuterRadii[0] = h; srcOuterRadii[1] = h
        srcOuterRadii[6] = h; srcOuterRadii[7] = h
        destOuterRadii[2] = h; destOuterRadii[3] = h
        destOuterRadii[4] = h; destOuterRadii[5] = h
    }
}