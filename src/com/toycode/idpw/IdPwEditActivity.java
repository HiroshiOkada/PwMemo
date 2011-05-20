package com.toycode.idpw;

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

//1. システムサービスを取得する
// LayoutInflater LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//2. アクティビティから取得する
// LayoutInflater LayoutInflater = this.getLayoutInflater();
//3. LayoutInflaterの静的メソッドを使う
// LayoutInflater inflator = LayoutInflater.from(this); // 1.と同じ
//参考コード contentview の切り替え
//LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//View testView = layoutInflater.inflate(R.layout.test, null, false);
//setContentView(testView);
//Button sub_button = (Button)testView.findViewById(R.id.button2);

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
	byte[] mCriptData = null;
	final String[] COLUMNS = { Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA };
	PasswordManager mPasswordManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		mPasswordManager = PasswordManager.getInstance(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mId = extras.getLong(Const.COLUMN.ID);
			if (mId != null) {
				readFromDb(mId);
				switch (extras.getInt(Const.REQUEST_TYPE.NAME)) {
				case Const.REQUEST_TYPE.READ:
					setUpReadViews();
					return;
				case Const.REQUEST_TYPE.NEW:
					setUpNewViews();
					return;
				case Const.REQUEST_TYPE.EDIT:
					setUpEditViews();
					return;
				}
			}
		}

	}

	/**
	 * Read モードで使用する Views を設定する
	 */
	private void setUpReadViews() {
		setTitle(R.string.view);
		setContentView(R.layout.read);
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
	private void setUpNewViews() {
		setTitle(R.string.new_str);
		setContentView(R.layout.edit);
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
	private void setUpEditViews() {
		setTitle(R.string.edit);
		setContentView(R.layout.edit);
		mTitleEdit = (EditText) findViewById(R.id.title_textedit);
		mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.password_edittext);
		mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
		mAddUpdateButton = (Button) findViewById(R.id.add_update_button);
		mAddUpdateButton.setOnClickListener(this);
		mAddUpdateButton.setText(R.string.update);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_update_button:
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

}
