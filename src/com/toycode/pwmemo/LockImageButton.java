
package com.toycode.pwmemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class LockImageButton extends Button {

    public LockImageButton(Context context) {
        super(context);
    }

    public LockImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LockImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLock(boolean lock) {
        if (lock) {
            setBackgroundResource(R.drawable.lock_close);
        } else {
            setBackgroundResource(R.drawable.lock_open);
        }
    }

}
