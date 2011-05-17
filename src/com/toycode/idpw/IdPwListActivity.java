package com.toycode.idpw;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class IdPwListActivity extends ListActivity implements OnClickListener,
		OnItemClickListener {

	PasswordManager mPasswordManager;
	LockImageButton mLockImageButton;
	Button mAddButton;
	Button mExitButton;
	ListView mListView;
	SimpleCursorAdapter mAdapter;
	SQLiteDatabase mDb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		mPasswordManager = PasswordManager.getInstance(this);

		mLockImageButton = (LockImageButton)findViewById(R.id.lock_image_button);
		mLockImageButton.setLock( mPasswordManager.isMainPasswordDecrypted() == false);

		mAddButton = (Button) findViewById(R.id.add_button);
		mAddButton.setOnClickListener(this);

		mExitButton = (Button) findViewById(R.id.exit_button);
		mExitButton.setOnClickListener(this);
		
		mListView = getListView();
		mListView.setEmptyView(findViewById(R.id.EmptyTextView));
		mListView.setOnItemClickListener(this);

		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		updateAdapter();
		
		if (! mPasswordManager.isMainPasswordExist()) {
			Intent i = new Intent(this, DeclarMasterPasswordActivity.class);
			startActivityForResult(i, Const.REQUEST_TYPE.NEW);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if( mPasswordManager.isMainPasswordDecrypted()){
			
		}
	}
	

	private void updateAdapter() {
		final String[] COLUMNS = { Const.COLUMN.ID, Const.COLUMN.TITLE };
		final String[] FROM = { Const.COLUMN.TITLE };
		final int[] TO = { R.id.RowTitleTextView };
		Cursor cursor = mDb.query(Const.TABLE.IDPW, COLUMNS, null, null, null,
				null, Const.COLUMN.TITLE + " ASC");
		mAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor,
				FROM, TO);
		setListAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch( v.getId()){
		case R.id.add_button:
			ContentValues values = new ContentValues();
			values.put(Const.COLUMN.TITLE, android.R.string.untitled);
			long id = mDb.insert(Const.TABLE.IDPW, null, values);
			Intent intent = new Intent(this, IdPwEditActivity.class);
			intent.putExtra(Const.COLUMN.ID, id);
			startActivityForResult(intent, Const.REQUEST_TYPE.NEW);
			//updateAdapter();
			break;
		case R.id.exit_button:
			PasswordManager.getInstance(this).unDecrypt();
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Const.REQUEST_TYPE.NEW:
			if (resultCode == RESULT_OK) {
				updateAdapter();
			}
			break;
		case Const.REQUEST_TYPE.EDIT:
			updateAdapter();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(this, IdPwEditActivity.class);
		i.putExtra(Const.COLUMN.ID, mAdapter.getItemId(position));
		startActivityForResult(i, Const.REQUEST_TYPE.VIEW);
	}




}