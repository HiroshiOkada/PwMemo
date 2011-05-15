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

public class IdPwEditActivity extends Activity implements OnClickListener {
	EditText mTitleEdit;
	EditText mUserEdit;
	CopyablePasswordEditText mPasswordEdit;
	EditText mMemoEdit;
	Button mAddUpdateButton;
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

		mTitleEdit = (EditText) findViewById(R.id.title_textedit);
		mUserEdit = (EditText) findViewById(R.id.user_id_edittext);
		mPasswordEdit = (CopyablePasswordEditText) findViewById(R.id.password_edittext);
		mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
		mAddUpdateButton = (Button) findViewById(R.id.add_update_button);
				
		mAddUpdateButton.setOnClickListener(this);

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
