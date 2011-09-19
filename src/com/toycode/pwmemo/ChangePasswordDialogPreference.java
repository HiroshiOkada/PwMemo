
package com.toycode.pwmemo;

import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;

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
        super(context);
        getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mContext = context;
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
