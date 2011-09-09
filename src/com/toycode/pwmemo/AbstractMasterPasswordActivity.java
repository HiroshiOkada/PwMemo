
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
 * 最初にマスターパスワードを設定するアクティビティ
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
     * 使用するレイアウトのリソースIDを返す
     * 
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * OKボタンが押された時の処理
     */
    protected abstract void onOkBtnClick();

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

    @Override
    public void afterTextChanged(Editable arg0) {
        mOkButton.setEnabled(inputVaildateion());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_button) {
            onOkBtnClick();
        }
    }

    protected boolean inputVaildateion() {
        String pw = mMasterPasswordEditText.getText().toString();
        String confirm = mConfirmationEditText.getText().toString();
        // 短すぎる
        if ((pw.length() > 0) &&
                (pw.length() == confirm.length()) &&
                (pw.length() < Const.MIN_PASSWORD_LEN)) {
            mApp.toastMessage(R.string.password_is_too_short);
            return false;
        }
        // 長さが一致しているが違う
        if ((pw.length() == confirm.length()) && !pw.equals(confirm)) {
            mApp.toastMessage(R.string.confirmation_password_does_not_match);
            return false;
        }
        // それ以外
        return (pw.length() >= Const.MIN_PASSWORD_LEN) && pw.equals(confirm);
    }
}
