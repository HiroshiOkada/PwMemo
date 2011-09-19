
package com.toycode.pwmemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Activity that sets up a master password. 
 * 
 * @author hiroshi
 */
public abstract class AbstractMasterPasswordActivity extends Activity implements TextWatcher,
        OnClickListener {
    EditText mMasterPasswordEditText;
    EditText mConfirmationEditText;
    Button mOkButton;
    protected App mApp;

    /**
     * Return Resource ID of the layout.
     */
    protected abstract int getLayoutResID();

    /**
     * Called when OK Button Clicked.
     */
    protected abstract void onOkBtnClick();


    /**
     * Setup mApp and child views.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = App.GetApp(this);
        setContentView(getLayoutResID());
        mMasterPasswordEditText = (EditText) findViewById(R.id.master_password_edit_text);
        mConfirmationEditText = (EditText) findViewById(R.id.confirmation_edit_text);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mOkButton.setEnabled(false);
        mMasterPasswordEditText.addTextChangedListener(this);
        mConfirmationEditText.addTextChangedListener(this);
        mOkButton.setOnClickListener(this);
    }

    /**
     * Whenever a text is changed, validation of an input is performed.
     * The OK button is enabled/disabled based on the result.
     */
    @Override
    public void afterTextChanged(Editable arg0) {
        mOkButton.setEnabled(inputVaildateion());
    }

    /**
     * Does not use. (implements TextWatcher)
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    /**
     * Does not use. (implements TextWatcher)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Only the OK button is used.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_button) {
            onOkBtnClick();
        }
    }

    /**
     * Vaildate input.
     * When short, or same length and contents are different is notifyed.
     * 
     * @return
     */
    protected boolean inputVaildateion() {
        String pw = mMasterPasswordEditText.getText().toString();
        String confirm = mConfirmationEditText.getText().toString();
        // Too short
        if ((pw.length() > 0) &&
                (pw.length() == confirm.length()) &&
                (pw.length() < Const.MIN_PASSWORD_LEN)) {
            mApp.toastMessage(R.string.password_is_too_short);
            return false;
        }
        // Length match, but different contents.
        if ((pw.length() == confirm.length()) && !pw.equals(confirm)) {
            mApp.toastMessage(R.string.confirmation_password_does_not_match);
            return false;
        }
        // Otherwise
        return (pw.length() >= Const.MIN_PASSWORD_LEN) && pw.equals(confirm);
    }
}
