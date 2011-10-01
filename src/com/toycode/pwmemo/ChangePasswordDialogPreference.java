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
import android.content.Intent;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Change password dialog 
 * 
 * @author Hiroshi Okada
 */
public class ChangePasswordDialogPreference extends EditTextPreference {
    private Context mContext = null;

    public ChangePasswordDialogPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mContext = context;
    }

    public ChangePasswordDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mContext = context;
    }

    public ChangePasswordDialogPreference(Context context) {
                this(context, null);
    }   

    /**
     * Decrypt the main password using the master password.
     * 
     * @param masterPassword 
     * @return True if successful to crack password
     */
    private boolean decryptMainPassword(String masterPassword) {
        if (TextUtils.isEmpty(masterPassword)) {
            return false;
        }
        PasswordManager pm = PasswordManager.getInstance(mContext);
        byte[] mainPasswd = pm.decryptMainPassword(masterPassword);
        return mainPasswd != null;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && (mContext != null)) {
            // can't decrypt == wrong password
            if (!decryptMainPassword(getEditText().getText().toString())) {
                App.GetApp(mContext).toastMessage(R.string.password_does_not_match);
                return;
            }
            Intent i = new Intent(mContext, UpdateMasterPasswordActivity.class);
            mContext.startActivity(i);
        }
    }
}
