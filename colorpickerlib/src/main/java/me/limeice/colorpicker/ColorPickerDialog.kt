package me.limeice.colorpicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout


class ColorPickerDialog @JvmOverloads constructor(context: Context, @StyleRes themeResId: Int = 0)
    : Dialog(context, themeResId) {

    companion object {

        /* 历史颜色展示最大个数 */
        private const val COUNT = 7

    }

    /* 主布局 */
    @SuppressLint("InflateParams")
    private val mContentView: View = LayoutInflater.from(context).inflate(R.layout.dlg_picker, null)

    /* 历史记录颜色展示 */
    private val mColorViews = mutableListOf<CircleColorView>()

    /* 颜色对比面板 */
    private val mCompareColorPanel = mContentView.findViewById<CompareColorPanel>(R.id.mCompareColorPanel)

    /* Hue色彩选择板 */
    private val mHueBoard = mContentView.findViewById<HueBoard>(R.id.mHueBoard)

    /* 标准HSV色卡 */
    private val mColorCard = mContentView.findViewById<ColorCard>(R.id.mColorCard)

    /* 历史颜色布局器 */
    private val mHistoryLayout = mContentView.findViewById<LinearLayout>(R.id.mHistoryLayout)

    /* 默认颜色 */
    @ColorInt private var defaultColor: Int = Color.BLUE

    /* 小球大小 */
    private val size = context.resources.getDimensionPixelSize(R.dimen.dp_32)

    /* 回调 */
    var callback: (color: Int) -> Unit = {}

    /**
     * if size > 7,delete!
     */
    var colorHistory = mutableListOf<Int>()

    /**
     * 吸管（取色器）点击事件
     * @param click 点击事件
     */
    fun colorStrawOnClick(click: (v: View) -> Unit) {
        mColorStraw.setOnClickListener(click)
        mColorStraw.visibility = View.VISIBLE
    }

    /**
     * 吸管
     */
    val mColorStraw: ImageView

    init {
        setContentView(mContentView)
        mContentView.findViewById<ImageView>(R.id.mReset).setOnClickListener { changeColor(defaultColor) }
        mContentView.findViewById<View>(R.id.mDone).setOnClickListener {
            mHistoryLayout.removeAllViews()
            clearRepeatFormHistory(mHueBoard.pickColor)
            cutLast()
            callback(mHueBoard.pickColor)
            dismiss()
        }
        mColorCard.colorChangeListener = { color ->
            mHueBoard.inPutColor = color
            mHueBoard.updateDrawable().update()
        }

        mColorStraw = mContentView.findViewById(R.id.mColorStraw)
        mHueBoard.colorChangeListener = { color ->
            mCompareColorPanel.dstColor = color
        }
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 打开颜色选择对话框
     * @param defaultColor 默认颜色
     */
    fun openDialog(@ColorInt defaultColor: Int) {
        if (isShowing)
            return
        this.defaultColor = defaultColor
        cutLast()
        while (mColorViews.size < colorHistory.size) {
            val c = CircleColorView(context)
            mColorViews.add(c)
            c.setOnClickListener {
                c.checked = true
                mColorViews.forEach { v ->
                    if (v.checked && v != c)
                        v.checked = false
                }
                changeColor(c.circleColor)
            }
        }
        window?.setLayout((context.resources.displayMetrics.widthPixels * 0.85f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        show()
        mHistoryLayout.removeAllViews()
        for (i in colorHistory.indices) {
            mColorViews[i].setColor(colorHistory[i])
            val param = LinearLayout.LayoutParams(size, size)
            param.gravity = Gravity.CENTER_VERTICAL
            val margin = (size * 0.3f).toInt()
            val marginLeftRight = (size * 0.15f).toInt()
            param.setMargins(marginLeftRight, margin, marginLeftRight, margin)
            mHistoryLayout.addView(mColorViews[i], param)
        }
        mContentView.post {
            if (colorHistory.size > 0) {
                mCompareColorPanel.srcColor = colorHistory[0]
                changeColor(colorHistory[0])
            } else {
                mCompareColorPanel.srcColor = defaultColor
                changeColor(defaultColor)
            }
        }
    }

    /**
     * 打开颜色选择对话框
     * @param defaultColor 默认颜色
     * @param changeColor  当前显示颜色
     */
    fun openDialog(@ColorInt defaultColor: Int, @ColorInt changeColor: Int) {
        if (isShowing)
            return
        this.defaultColor = defaultColor
        cutLast()
        while (mColorViews.size < colorHistory.size) {
            val c = CircleColorView(context)
            mColorViews.add(c)
            c.setOnClickListener {
                c.checked = true
                mColorViews.forEach { v ->
                    if (v.checked && v != c)
                        v.checked = false
                }
                changeColor(c.circleColor)
            }
        }
        window?.setLayout((context.resources.displayMetrics.widthPixels * 0.85f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        show()
        mHistoryLayout.removeAllViews()
        for (i in colorHistory.indices) {
            mColorViews[i].setColor(colorHistory[i])
            val param = LinearLayout.LayoutParams(size, size)
            param.gravity = Gravity.CENTER_VERTICAL
            val margin = (size * 0.3f).toInt()
            val marginLeftRight = (size * 0.15f).toInt()
            param.setMargins(marginLeftRight, margin, marginLeftRight, margin)
            mHistoryLayout.addView(mColorViews[i], param)
        }
        mContentView.post {
            mCompareColorPanel.srcColor = changeColor
            changeColor(changeColor)
        }
    }

    /* 改变颜色 */
    private fun changeColor(color: Int) {
        mColorCard.setPickColorMoveToPosition(color)
        mHueBoard.setPickColorMoveToPosition(color)
    }

    override fun dismiss() {
        if (isShowing)
            super.dismiss()
    }

    /* 移除最后一个颜色 */
    private fun cutLast() {
        var i = COUNT - 1
        while (i < colorHistory.size) {
            colorHistory.removeAt(i)
            i++
        }
    }

    /**
     * 添加新[color]颜色，并去除与之重复
     */
    private fun clearRepeatFormHistory(color: Int) {
        var i = 0
        while (i < colorHistory.size) {
            if (color == colorHistory[i]) {
                colorHistory.removeAt(i)
                i--
            }
            i++
        }
        colorHistory.add(0, color)
    }
}