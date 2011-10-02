/*
 * Copyright (c) 2011 Hiroshi Okada (http://toycode.com/hiroshi/)
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *   1.The origin of this software must not be misrepresented; you must
 *     not claim that you wrote the original software. If you use this
 *     software in a product, an acknowledgment in the product
 *     documentation would be appreciated but is not required.
 *   
 *   2.Altered source versions must be plainly marked as such, and must
 *     not be misrepresented as being the original software.
 *   
 *   3.This notice may not be removed or altered from any source
 *     distribution.
 */

package com.toycode.pwmemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * The lock/unlock image button.
 * 
 * @author Hiroshi Okada
 */
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
