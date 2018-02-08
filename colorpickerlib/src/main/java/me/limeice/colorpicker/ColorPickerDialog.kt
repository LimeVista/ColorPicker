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
        private const val COUNT = 7
    }

    @SuppressLint("InflateParams")
    private val mContentView: View = LayoutInflater.from(context).inflate(R.layout.dlg_picker, null)

    private val mColorViews = mutableListOf<CircleColorView>()

    private val mCompareColorPanel = mContentView.findViewById<CompareColorPanel>(R.id.mCompareColorPanel)

    private val mHueBoard = mContentView.findViewById<HueBoard>(R.id.mHueBoard)

    private val mColorCard = mContentView.findViewById<ColorCard>(R.id.mColorCard)

    private val mHistoryLayout = mContentView.findViewById<LinearLayout>(R.id.mHistoryLayout)

    @ColorInt var defaultColor: Int = Color.BLUE

    private val size = context.resources.getDimensionPixelSize(R.dimen.dp_32)

    var callback: (color: Int) -> Unit = {}

    /**
     * if size > 7,delete!
     */
    val colorHistory = mutableListOf<Int>()

    init {
        setContentView(mContentView)
        mContentView.findViewById<ImageView>(R.id.mReset).setOnClickListener { _ -> changeColor(defaultColor) }
        mContentView.findViewById<View>(R.id.mDone).setOnClickListener {
            callback(mHueBoard.pickColor)
            dismiss()
        }
        mColorCard.colorChangeListener = { color ->
            mHueBoard.inPutColor = color
            mHueBoard.updateDrawable().update()
        }

        mHueBoard.colorChangeListener = { color ->
            mCompareColorPanel.dstColor = color
        }
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // colorHistory.add(0xFF4C5A79.toInt()) Test
    }

    fun openDialog() {
        if (isShowing)
            return
        if (colorHistory.size > COUNT) {
            var i = COUNT
            while (i < colorHistory.size) {
                colorHistory.removeAt(i)
                i++
            }
        }
        while (mColorViews.size < colorHistory.size) {
            val c = CircleColorView(context)
            mColorViews.add(c)
            c.setOnClickListener {
                c.checked = true
                mColorViews.forEach {
                    if (it.checked && it != c)
                        it.checked = false
                }
                changeColor(c.circleColor)
            }
        }
        window.setLayout((context.resources.displayMetrics.widthPixels * 0.85f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        show()
        for (i in colorHistory.indices) {
            mColorViews[i].setColor(colorHistory[i])
            mHistoryLayout.removeAllViews()
            val param = LinearLayout.LayoutParams(size, size)
            param.gravity = Gravity.CENTER_VERTICAL
            val margin = (size * 0.3f).toInt()
            val marginLeftRight = (size * 0.4f).toInt()
            param.setMargins(marginLeftRight, margin, marginLeftRight, margin)
            mHistoryLayout.addView(mColorViews[i], param)
        }
    }

    private fun changeColor(color: Int) {
        mColorCard.setPickColorMoveToPosition(color)
        mHueBoard.setPickColorMoveToPosition(color)
    }

    override fun dismiss() {
        if (isShowing)
            super.dismiss()
    }
}