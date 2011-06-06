package com.toycode.idpw;

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
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
