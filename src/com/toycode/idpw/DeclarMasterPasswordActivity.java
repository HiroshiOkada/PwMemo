package com.toycode.idpw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 最初にマスターパスワードを設定するアクティビティ
 * @author hiroshi
 */
public class DeclarMasterPasswordActivity extends Activity implements TextWatcher, OnClickListener {
	EditText mMasterPasswordEditText;
    EditText mConfirmationEditText;
	Button mOkButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.declar_master_password);
		mMasterPasswordEditText = (EditText)findViewById( R.id.MasterPasswordEditText);
		mConfirmationEditText = (EditText)findViewById( R.id.ConfirmationEditText);
		mOkButton = (Button)findViewById( R.id.OkButton);
		mOkButton.setEnabled( false);

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
		if ( v.getId() == R.id.OkButton){
			if( inputVaildateion()){
				PasswordManager.getInstance(this).createMainPassword(mMasterPasswordEditText.getText().toString());
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
	
	private boolean inputVaildateion() {
		String pw = mMasterPasswordEditText.getText().toString();
		String confirm = mConfirmationEditText.getText().toString();
		return (pw.length() >= Const.MIN_PASSWORD_LEN) && pw.equals(confirm);
	}

}
