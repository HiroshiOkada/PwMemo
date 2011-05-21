package com.toycode.idpw;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
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
		updateLockImageButton();
		mLockImageButton.setOnClickListener(this);

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
		mLockImageButton.setLock( mPasswordManager.isMainPasswordDecrypted() == false);			
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

	/**
	 * 各ボタンが押されたときの処理
	 * ボタンに合わせたメソッドを呼び出す。
	 */
	@Override
	public void onClick(View v) {
		switch( v.getId()){
		case R.id.lock_image_button:
			onLockImageButton();
			break;
		case R.id.add_button:
			onAddButton();
			break;
		case R.id.exit_button:
			onExitButton();
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
		case Const.REQUEST_TYPE.READ:
			if (resultCode == RESULT_OK) {
				updateAdapter();
			}
			break;
		}
	}
	/**
	 * LockImageButton が押された時の処理
	 */
	private void onLockImageButton() {
		if( mPasswordManager.isMainPasswordDecrypted()){
			Log.d(getClass().getName(), "MainPasswordDecrypted");
			mPasswordManager.unDecrypt();
			updateLockImageButton();
		} else {
			Log.d(getClass().getName(), "ask MainPassword");
			MasterPasswordInput mpi = new MasterPasswordInput (this) {
				public void onTureMasterPassword() {
					updateLockImageButton();
				 }
			};
			mpi.Ask();
		}
	}
	
	/**
	 * AddButton が押された時の処理
	 */
	private void onAddButton() {
		if( mPasswordManager.isMainPasswordDecrypted() ){
			ContentValues values = new ContentValues();
			values.put(Const.COLUMN.TITLE, android.R.string.untitled);
			long id = mDb.insert(Const.TABLE.IDPW, null, values);
			Intent intent = new Intent(this, IdPwEditActivity.class);
			intent.putExtra(Const.COLUMN.ID, id);
			intent.putExtra(Const.REQUEST_TYPE.NAME, Const.REQUEST_TYPE.NEW);
			startActivityForResult(intent, Const.REQUEST_TYPE.NEW);
		} else {
			toastMessage(R.string.locked_message);
		}
	}

	/**
	 * ExitButto が押された時の処理
	 */
	private void onExitButton() {
		PasswordManager.getInstance(this).unDecrypt();
		finish();
	}

	/**
	 * アイテムがクリックされた時の処理
	 * 
	 * Unlock 状態なら編集画面に Read モードで遷移
	 * そうでなければ、lock されている旨を表示
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if( mPasswordManager.isMainPasswordDecrypted() ){
			Intent intent = new Intent(this, IdPwEditActivity.class);
			intent.putExtra(Const.COLUMN.ID, mAdapter.getItemId(position));
			intent.putExtra(Const.REQUEST_TYPE.NAME, Const.REQUEST_TYPE.READ);
			startActivityForResult(intent, Const.REQUEST_TYPE.READ);
		} else {
			toastMessage(R.string.locked_message);
		}
	}
	
    /** 
     * メッセージをトーストにして表示
     */
    private void toastMessage( int message_id){
    	String message = getString(message_id);
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();   
    }
    
    /**
     * 現在のマスターパスワードの状況に応じて LockImageButton　を変化させる
     */
    private void updateLockImageButton() {
		mLockImageButton.setLock( mPasswordManager.isMainPasswordDecrypted() == false);						   	
    }
}