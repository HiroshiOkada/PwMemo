package com.toycode.idpw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class IdPwEditActivity extends Activity implements OnClickListener {
	ImageView mLockImageView;
	EditText mTitleEdit;
	EditText mUserEdit;
	CopyablePasswordEditText mPasswordEdit;
	EditText mMemoEdit;
	Button mSaveButton;
	boolean mLockState = true;
	boolean mIsDecypted = false;
	Long mId;
	SQLiteDatabase mDb;
	byte [] mCriptData = null;
	final String[] COLUMNS = { Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA };
	PasswordManager mPasswordManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Edit");
		setContentView(R.layout.edit);

		mLockImageView = (ImageView) findViewById(R.id.LockImageView);
		mTitleEdit = (EditText) findViewById(R.id.TitleEditText);
		mUserEdit = (EditText) findViewById(R.id.UserEditText);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.PasswordEditText);
		mMemoEdit = (EditText) findViewById(R.id.MemoEditText);
		mSaveButton = (Button) findViewById(R.id.SaveButton);
		
		mLockState = true;
		mIsDecypted = false;
		mLockImageView.setImageResource(R.drawable.keylock);
		showHideCryptField( false);
		
		mLockImageView.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);

		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mId = extras.getLong(Const.COLUMN.ID);
			if (mId != null) {
				readFromDb(mId);
			}
		}
		
		mPasswordManager = PasswordManager.getInstance(this);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.LockImageView:
			if (mLockState) {
				if( !mPasswordManager.isMainPasswordDecrypted()){
					askMasterPassword();
				} else {
					decrypt();
					mLockState = false;
					mLockImageView.setImageResource(R.drawable.keyunlock);
					showHideCryptField( true);					
				}
			} else {
				mLockState = true;
				mLockImageView.setImageResource(R.drawable.keylock);
				showHideCryptField( false);
			}
			break;
		case R.id.SaveButton:
			if( mIsDecypted) {
				encrypt();
			}
			updateDb(mId);
			mDb.close();
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
	}

	private void readFromDb(Long id) {
		Cursor cursor = mDb.query(Const.TABLE.IDPW, COLUMNS, 
			
				Const.COLUMN.ID + " = " + id, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			mTitleEdit.setText(cursor.getString(0));
			mCriptData = cursor.getBlob(1);
		}
		cursor.close();
	}

	private void updateDb(Long id) {
		String[] whereArgs = { id.toString() };
		ContentValues values = new ContentValues();
		values.put(Const.COLUMN.TITLE, mTitleEdit.getText().toString());
		values.put(Const.COLUMN.CRIPTDATA, mCriptData);
		mDb.update(Const.TABLE.IDPW, values, Const.COLUMN.ID + " = ?",
				whereArgs);
	}
	
	
	/**
	 * master password 入力用のダイアログを表示
	 */
	private void askMasterPassword() {
		final EditText editText = new EditText(this);
		editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		final AlertDialog alertDialog =
			new AlertDialog.Builder(this)
				.setTitle(getString(R.string.input_masterpassword))
				.setView(editText)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						onPassword( editText.getText().toString());
					}					
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create();
		
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus){
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		alertDialog.show();
	}

	/**
	 * password が入力されたときの処理
	 */
	private void onPassword( String password) {
		if( mPasswordManager.isMainPasswordExist()){
			if( mPasswordManager.decryptMainPassword(password) == null){
				Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show();
				return;
			}
			decrypt();
			mLockState = false;
			mLockImageView.setImageResource(R.drawable.keyunlock);
			showHideCryptField( true);

		}else{
			mPasswordManager.createMainPassword(password);
			mLockImageView.setImageResource(R.drawable.keyunlock);
			Toast.makeText(this, "Set master password", Toast.LENGTH_LONG).show();
			
			mLockState = false;
			mLockImageView.setImageResource(R.drawable.keyunlock);
			showHideCryptField( true);
		}
	}
	
	/**
	 * 暗号フィールドを見せたり、隠したりする
	 */
	private void showHideCryptField( boolean isVisible){
		int visiblety = EditText.INVISIBLE;
		if ( isVisible){
			visiblety = EditText.VISIBLE;
		}
		mUserEdit.setVisibility(visiblety);
		mPasswordEdit.setVisibility(visiblety);
		mMemoEdit.setVisibility(visiblety);
	}

	/**
	 * 暗号データを解読してテキストエディタにセットする
	 */
	private void decrypt(){
		if( mCriptData == null || mCriptData.length == 0){
			mUserEdit.setText("");
			mPasswordEdit.setText("");
			mMemoEdit.setText("");
		}else if( mPasswordManager.isMainPasswordDecrypted()){
			byte [] mainPassword = mPasswordManager.getDecryptedMainPassword();
			byte [] data = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(mainPassword, mCriptData);
			String [] data3 = new String( data).split("\\n",3);
			if( data3.length > 0){
				mUserEdit.setText(data3[0]);
			}
			if( data3.length > 1){
				mPasswordEdit.setText(data3[1]);
			}
			if( data3.length > 0){
				mMemoEdit.setText(data3[2]);
			}			
		}
		mIsDecypted = true;
	}
	
	/**
	 * テキストエディタにあるデータを暗号化する
	 */
	private void encrypt() {
		String data = mUserEdit.getText().toString() + "\n"
				+ mPasswordEdit.getText().toString() + "\n"
				+ mMemoEdit.getText().toString() ;
		byte [] mainPassword = mPasswordManager.getDecryptedMainPassword();
		mCriptData = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(mainPassword, data.getBytes());
	}
}
