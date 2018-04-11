package me.limeice.colorpicker

import android.content.Context
import android.support.annotation.ColorInt
import android.view.View

/**
 * dp 转 px
 *
 * @param dpValue dp 值
 * @return px 值
 */
fun View.dp2px(dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * px 转 dp
 *
 * @param pxValue px 值
 * @return dp 值
 */
fun View.px2dp(pxValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * dp 转 px
 *
 * @param dpValue dp 值
 * @return px 值
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * px 转 dp
 *
 * @param pxValue px 值
 * @return dp 值
 */
fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

@ColorInt
fun Int.r(): Int = (this and 0x00FF0000) ushr 16

@ColorInt
fun Int.g(): Int = (this and 0x0000FF00) ushr 8

@ColorInt
fun Int.b(): Int = this and 0x000000FF


/**
 * 正片叠底
 */
@ColorInt
infix fun Int.multiply(color: Int): Int {
    return (0xFF shl 24) or
            ((r() * color.r() / 0xFF) shl 16) or
            ((g() * color.g() / 0xFF) shl 8) or
            (b() * color.b() / 0xFF)
}