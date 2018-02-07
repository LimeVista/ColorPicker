package me.limeice.colorpicker

import android.app.Activity
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
fun Activity.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * px 转 dp
 *
 * @param pxValue px 值
 * @return dp 值
 */
fun Activity.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

@ColorInt
fun Int.r(): Int = (this and 0x00FF0000) ushr 16

@ColorInt
fun Int.g(): Int = (this and 0x0000FF00) ushr 8

@ColorInt
fun Int.b(): Int = this and 0x000000FF