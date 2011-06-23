package com.toycode.idpw;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IdPwEditActivity extends Activity implements OnClickListener {
	EditText mTitleEdit;
	Button mCopyTitleButton;
	EditText mUserEdit;
	Button mCopyUserId_button;
	CopyablePasswordEditText mPasswordEdit;
	Button mCopyPasswwordButton;
	EditText mMemoEdit;
	Button mCopyMemoButton;
	Button mAddUpdateButton;
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
				switch (extras.getInt(Const.REQUEST_TYPE.NAME)) {
				case Const.REQUEST_TYPE.READ:
					setUpReadViews(true);
					readFromDb(mId);
					return;
				case Const.REQUEST_TYPE.NEW:
					setUpNewViews(true);
					readFromDb(mId);
					return;
				case Const.REQUEST_TYPE.EDIT:
					setUpEditViews(true);
					readFromDb(mId);
					return;
				}
			}
		}
	}

	/**
	 * Read モードで使用する Views を設定する
	 */
	private void setUpReadViews( boolean isOnCreate) {
		setTitle(R.string.view_str);
		setContentView(isOnCreate, R.layout.read);
		mTitleEdit = (EditText) findViewById(R.id.title_textedit);
		mCopyTitleButton = (Button) findViewById(R.id.copy_title_button);
		mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
		mCopyUserId_button = (Button) findViewById(R.id.copy_user_id_button);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.password_edittext);
		mCopyPasswwordButton = (Button) findViewById(R.id.copy_passwword_button);
		mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
		mCopyMemoButton = (Button) findViewById(R.id.copy_memo_button);
		mEditButton = (Button) findViewById(R.id.edit_button);
		mEditButton.setOnClickListener(this);
	}

	/**
	 * New モードで使用する Views を設定する
	 */
	private void setUpNewViews( boolean isOnCreate) {
		setTitle(R.string.new_str);
		setContentView(isOnCreate, R.layout.edit);
		mTitleEdit = (EditText) findViewById(R.id.title_textedit);
		mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.password_edittext);
		mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
		mAddUpdateButton = (Button) findViewById(R.id.add_update_button);
		mAddUpdateButton.setOnClickListener(this);
		mAddUpdateButton.setText(R.string.add);
	}

	/**
	 * Edit モードで使用する Views を設定する
	 */
	private void setUpEditViews( boolean isOnCreate) {
		setTitle(R.string.edit);
		setContentView( isOnCreate, R.layout.edit);
		mTitleEdit = (EditText) findViewById(R.id.title_textedit);
		mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.password_edittext);
		mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
		mAddUpdateButton = (Button) findViewById(R.id.add_update_button);
		mAddUpdateButton.setOnClickListener(this);
		mAddUpdateButton.setText(R.string.update);
	}
	
	/**
	 * 指定した View を setContentView する
	 * onCreate 以外の時も対応
	 */
	public void setContentView( boolean isOnCreate, int layoutID) {
		if( isOnCreate ){
			setContentView( layoutID);
		} else {
			LayoutInflater layoutInflater = getLayoutInflater();
			View contentView = layoutInflater.inflate(layoutID, null, false);
			setContentView(contentView);
		}		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_update_button:
			updateDb(mId);
			mDb.close();
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.cancel_button:
			setResult( RESULT_CANCELED, new Intent());
			finish();
			break;
		case R.id.edit_button:
			setContentView( false, R.id.edit_button);
			break;
		}
	}
	
	private void readFromDb(Long id) {
		Cursor cursor = mDb.query(Const.TABLE.IDPW, COLUMNS, Const.COLUMN.ID
				+ " = " + id, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Toy.debugLog(this, "TITLE=" + cursor.getString(0));
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
		if (cryptdata != null && cryptdata.length < OpenSSLAES128CBCCrypt.BLOCK_LENGTH) {
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
