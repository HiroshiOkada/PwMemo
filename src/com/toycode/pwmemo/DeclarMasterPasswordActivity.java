
package com.toycode.pwmemo;

import android.content.Intent;

public class DeclarMasterPasswordActivity extends
        AbstractMasterPasswordActivity {

    protected int getLayoutResID() {
        return R.layout.declar_master_password;
    }

    @Override
    protected void onOkBtnClick() {
        if (inputVaildateion()) {
            PasswordManager pm = PasswordManager.getInstance(this);
            pm.createMainPassword(mMasterPasswordEditText.getText().toString());
            TimeOutChecker.getInstance().onUser();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
