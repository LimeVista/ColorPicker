package me.limeice.gesture.standard;

import android.view.MotionEvent;


public interface OnDrag {

    /**
     * 拖拽事件
     *
     * @param event 触摸事件
     * @param dx 偏移 x 位移
     * @param dy 偏移 y 位移
     */
    void onDrag(MotionEvent event, float dx, float dy);
}
