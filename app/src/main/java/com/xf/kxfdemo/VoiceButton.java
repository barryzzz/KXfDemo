package com.xf.kxfdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author : lsl 408930131@qq.com
 * @version : v1.0
 * @description :
 * @date : 2018-04-03
 */
public class VoiceButton extends android.support.v7.widget.AppCompatButton {

    private OnTouchVoiceCallBack touchVoiceCallBack;

    public VoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (touchVoiceCallBack != null)
                    touchVoiceCallBack.onTouchDown();
                break;
            case MotionEvent.ACTION_UP:
                if (touchVoiceCallBack != null)
                    touchVoiceCallBack.onTouchUp();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setTouchVoiceCallBack(OnTouchVoiceCallBack touchVoiceCallBack) {
        this.touchVoiceCallBack = touchVoiceCallBack;
    }

    public interface OnTouchVoiceCallBack {
        void onTouchDown();

        void onTouchUp();
    }
}
