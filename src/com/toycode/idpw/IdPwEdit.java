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
import android.widget.ImageView;

public class IdPwEdit extends Activity implements OnClickListener {
	ImageView mLockImageView;
	EditText mTitleEdit;
	EditText mUserEdit;
	CopyablePasswordEditText mPasswordEdit;
	EditText mMemoEdit;
	Button mSaveButton;
	boolean mLockState = true;
	Long mId;
	SQLiteDatabase mDb;
	final String[] COLUMNS = { Const.COLUMN.TITLE,
			Const.COLUMN.USER, Const.COLUMN.PASSWORD,
			Const.COLUMN.MEMO};

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
		
		mLockImageView.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);

		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mId = extras.getLong(Const.COLUMN.ID);
			if (mId != null) {
				readFromDb( mId);
			}
			setTitle("Edit" + mId);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.LockImageView:
			mLockState = !mLockState;
			if (mLockState) {
				mLockImageView.setImageResource(R.drawable.keylock);
			} else {
				mLockImageView.setImageResource(R.drawable.keyunlock);
			}
			break;
		case R.id.SaveButton:
			updateDb( mId);
			mDb.close();
			Intent intent = new Intent();                                                                                                                  
			setResult( RESULT_OK, intent);  
			finish();
			break;
		}
	}
	
	private void readFromDb(Long id) {
		Cursor cursor = mDb.query(Const.TABLE.IDPW, 
				COLUMNS, 
				Const.COLUMN.ID + " = " + id, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			mTitleEdit.setText(cursor.getString(0));
			String user = cursor.getString(1);
			if (user == null) {
				user = "";
			}
			mUserEdit.setText(user);
			String password = cursor.getString(2);
			if (password == null) {
				password = "";
			}
			mPasswordEdit.setText(password);
			String memo = cursor.getString(3);
			if (memo == null) {
				memo = "";
			}
			mMemoEdit.setText(memo);
		}
		cursor.close();
	}	

	private void updateDb(Long id) {
		String[] whereArgs = { id.toString() };
		ContentValues values = new ContentValues();
		values.put(Const.COLUMN.TITLE, mTitleEdit.getText().toString());
		values.put(Const.COLUMN.USER, mUserEdit.getText().toString());
		values.put(Const.COLUMN.PASSWORD, mPasswordEdit.getText().toString());
		values.put(Const.COLUMN.MEMO, mMemoEdit.getText().toString());
		mDb.update(Const.TABLE.IDPW, values, Const.COLUMN.ID + " = ?", whereArgs);
	}
}
