
package com.toycode.idpw;

public class UpdateMasterPasswordActivity extends
        AbstractMasterPasswordActivity {

    @Override
    protected int getLayoutResID() {
        return R.layout.updae_master_password;
    }

    @Override
    protected void onOkBtnClick() {
        if (inputVaildateion()) {
            PasswordManager pm = PasswordManager.getInstance(this);
            String newMasterPassword = mMasterPasswordEditText.getText().toString();
            if (pm.changeMasterPassword(newMasterPassword) == true) {
                finish();
            } else {
                Toy.toastMessage(this, R.string.password_change_failed);
            }
        }
    }
}
