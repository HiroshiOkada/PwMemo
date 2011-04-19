package com.toycode.idpw;

import java.util.Date;

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

	SimpleCursorAdapter mAdapter;
	SQLiteDatabase mDb;
	private static final int REQUEST_EDIT = 1;
	private static final int REQUEST_NEW = 2;
	PasswordManager mPasswordManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ListView listView = getListView();
		listView.setEmptyView(findViewById(R.id.EmptyTextView));
		listView.setOnItemClickListener(this);

		mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
		updateAdapter();
		((Button) findViewById(R.id.AddButton)).setOnClickListener(this);
		((Button) findViewById(R.id.ExitButton)).setOnClickListener(this);
		mPasswordManager = PasswordManager.getInstance(this);
		if (! mPasswordManager.isMainPasswordExist()) {
			Intent i = new Intent(this, DeclarMasterPasswordActivity.class);
			startActivityForResult(i, REQUEST_NEW);
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
		case R.id.AddButton:
			ContentValues values = new ContentValues();
			values.put(Const.COLUMN.TITLE, android.R.string.untitled);
			long id = mDb.insert(Const.TABLE.IDPW, null, values);
			Intent intent = new Intent(this, IdPwEditActivity.class);
			intent.putExtra(Const.COLUMN.ID, id);
			startActivityForResult(intent, REQUEST_NEW);
			//updateAdapter();
			break;
		case R.id.ExitButton:
			PasswordManager.getInstance(this).unDecrypt();
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_NEW:
			if (resultCode == RESULT_OK) {
				updateAdapter();
			}
			break;
		case REQUEST_EDIT:
			updateAdapter();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(this, IdPwEditActivity.class);
		i.putExtra(Const.COLUMN.ID, mAdapter.getItemId(position));
		startActivityForResult(i, REQUEST_EDIT);
	}

}