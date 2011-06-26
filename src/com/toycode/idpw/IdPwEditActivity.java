package com.toycode.idpw;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IdPwEditActivity extends Activity implements OnClickListener {
	EditText mTitleEdit;
	Button mCopyTitleButton;
	EditText mUserEdit;
	Button mCopyUserId_button;
	EditText mPasswordEdit;
	Button mCopyPasswwordButton;
	EditText mMemoEdit;
	Button mCopyMemoButton;
	Button mOkButton;
	Button mEditButton;
	Long mId;
	SQLiteDatabase mDb;
	final String[] COLUMNS = { Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA };
	PasswordManager mPasswordManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		mPasswordManager = PasswordManager.getInstance(this);
		// アンロック状態でなければ終了
		if( mPasswordManager.isMainPasswordDecrypted() == false) {
			setResult( RESULT_CANCELED, new Intent());
			finish();
			return;
		}
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mId = extras.getLong(Const.COLUMN.ID);
			if (mId != null) {
				setTitle(R.string.edit);
				setContentView(R.layout.edit);
				mTitleEdit = (EditText) findViewById(R.id.title_textedit);
				mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
				mPasswordEdit = (EditText) findViewById(R.id.password_edittext);
				mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
				mOkButton = (Button) findViewById(R.id.ok_button);
				mOkButton.setOnClickListener(this);
				readFromDb(mId);
			} else {
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_button:
			updateDb(mId);
			mDb.close();
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.cancel_button:
			setResult( RESULT_CANCELED, new Intent());
			finish();
			break;
		}
	}
	
	private void readFromDb(Long id) {
		Cursor cursor = mDb.query(Const.TABLE.IDPW, COLUMNS, Const.COLUMN.ID
				+ " = " + id, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			mTitleEdit.setText(cursor.getString(0));
			byte [] cryptdata = cursor.getBlob(1);
			setCryptDataToFileds(cryptdata);
		}
		cursor.close();		
	}
	
	private String getStringFromJSONArray(JSONArray jsonArray) {
		try {
			return jsonArray.getString(0);
		} catch (JSONException e) {
			return "";
		}
	}

	private void setCryptDataToFileds(byte [] cryptdata) {
		if (cryptdata != null && cryptdata.length >= OpenSSLAES128CBCCrypt.BLOCK_LENGTH) {
			byte [] password = mPasswordManager.getDecryptedMainPassword();
			if (password != null) {
				byte [] bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(password, cryptdata);
				String stringData = new String(bytesData);
				try {
					JSONArray jsonArray = new JSONArray(stringData);
					mUserEdit.setText(getStringFromJSONArray(jsonArray));
					mPasswordEdit.setText(getStringFromJSONArray(jsonArray));
					mMemoEdit.setText(getStringFromJSONArray(jsonArray));
				} catch (JSONException e) {
					mUserEdit.setText("");
					mPasswordEdit.setText("");
					mMemoEdit.setText("");
				}
			} else {
				Toy.toastMessage(this, R.string.timeout);
				finish();
			}
		}
	}
	
	private void putCryptDataFromFileds(ContentValues values) {
		byte [] password = mPasswordManager.getDecryptedMainPassword();
		if (password == null) {
			Toy.toastMessage(this, R.string.timeout);
			return;
		} else {
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(mUserEdit.getText().toString());
			jsonArray.put(mPasswordEdit.getText().toString());
			jsonArray.put(mMemoEdit.getText().toString());
			byte [] bytesData = jsonArray.toString().getBytes();
			values.put(Const.COLUMN.CRIPTDATA,
					OpenSSLAES128CBCCrypt.INSTANCE.encrypt(password, bytesData));			
		}
	}
	
	private void updateDb(Long id) {
		String[] whereArgs = { id.toString() };
		ContentValues values = new ContentValues();
		values.put(Const.COLUMN.TITLE, mTitleEdit.getText().toString());
		putCryptDataFromFileds(values);
		mDb.update(Const.TABLE.IDPW, values, Const.COLUMN.ID + " = ?",
				whereArgs);
	}
}
