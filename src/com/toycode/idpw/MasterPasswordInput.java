package com.toycode.idpw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ダイアログを表示し、マスターパスワードを入力してもらう
 * 機能を実現するクラス
 * 
 * Activity の中で次の用に呼び出す
 * MasterPasswordInput mpi = new MasterPasswordInput (this) {
 *   public void onTureMasterPassword() {
 *     正しいパスワードが入力された時の処理
 *   }
 * };
 * mpi.Ask()
 * 
 * @author hiroshi
 *
 */
public abstract class MasterPasswordInput {
	
	Activity mActivity;

	public MasterPasswordInput(Activity activity) {
		mActivity = activity;
	}

	/**
	 * ダイアログを表示し、マスターパスワードを入力してもらう
	 */
	public void Ask() {
		final EditText editText = new EditText(mActivity);
		
		editText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
				.setTitle(mActivity.getString(R.string.input_masterpassword))
				.setView(editText).setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								onPassword( editText.getText().toString());
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create();

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alertDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		alertDialog.show();
	}

	/**
	 * password が入力されたときの処理
	 */
	private void onPassword( String password) {
		
		PasswordManager passwordManager = PasswordManager.getInstance(mActivity);
		if( passwordManager.isMainPasswordExist()){
			if( passwordManager.decryptMainPassword(password) == null){
				Toast.makeText(mActivity, "Password not match", Toast.LENGTH_LONG).show();
				return;
			} else {
				onTureMasterPassword();
			}

		}else{
			passwordManager.createMainPassword(password);
			Toast.makeText(mActivity, "Set master password", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 正しいパスワードが入力された時に呼び出される
	 * @return
	 */
	abstract void onTureMasterPassword();
}
