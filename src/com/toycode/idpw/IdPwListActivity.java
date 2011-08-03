
package com.toycode.idpw;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class IdPwListActivity extends ListActivity implements OnClickListener,
        OnItemClickListener {

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

        mLockImageButton = (LockImageButton) findViewById(R.id.lock_image_button);
        updateLockImageButton();
        mLockImageButton.setOnClickListener(this);

        mAddButton = (Button) findViewById(R.id.add_button);
        mAddButton.setOnClickListener(this);

        mExitButton = (Button) findViewById(R.id.exit_button);
        mExitButton.setOnClickListener(this);

        mListView = getListView();
        mListView.setEmptyView(findViewById(R.id.EmptyTextView));
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        mDb = (new IdPwDbOpenHelper(this)).getWritableDatabase();
        updateAdapter();

    }
       
    @Override
    protected void onResume() {
        super.onResume();
        if (!PasswordManager.getInstance(this).isMainPasswordExist()) {
            Intent i = new Intent(this, DeclarMasterPasswordActivity.class);
            startActivityForResult(i, Const.REQUEST_TYPE.NEW);
        } else {
            mLockImageButton
                    .setLock(PasswordManager.getInstance(this).isMainPasswordDecrypted() == false);
            updateAdapter();
        }
    }

    private void updateAdapter() {
        final String[] COLUMNS = {
                Const.COLUMN.ID, Const.COLUMN.TITLE
        };
        final String[] FROM = {
            Const.COLUMN.TITLE
        };
        final int[] TO = {
            R.id.RowTitleTextView
        };
        Cursor cursor = mDb.query(
                Const.TABLE.IDPW,    // table 
                COLUMNS,             // columns
                null,               // selection
                null,               // selectionArgs
                null,               // groupBy
                null,               // having
                Const.COLUMN.TITLE + " COLLATE NOCASE"    // orderBy
                );
        mAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor,
                FROM, TO);
        setListAdapter(mAdapter);
    }

    /**
     * 各ボタンが押されたときの処理 ボタンに合わせたメソッドを呼び出す。
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted()) {
            PasswordManager.getInstance(this).unDecrypt();
            updateLockImageButton();
        } else {
            (new MasterPasswordInput(this) {
                public void onTureMasterPassword() {
                    updateLockImageButton();
                }
            }).Ask();
        }
    }

    /**
     * AddButton が押された時の処理
     */
    private void onAddButton() {
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted()) {
            ContentValues values = new ContentValues();
            values.put(Const.COLUMN.TITLE, "");
            long id = mDb.insert(Const.TABLE.IDPW, null, values);
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(Const.COLUMN.ID, id);
            startActivityForResult(intent, Const.REQUEST_TYPE.NEW);
        } else {
            Toy.toastMessage(this, R.string.locked_message);
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
     * アイテムがクリックされた時の処理 Unlock 状態なら編集画面に遷移、そうでなければ、lock されている旨を表示
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted()) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(Const.COLUMN.ID, mAdapter.getItemId(position));
            startActivityForResult(intent, Const.REQUEST_TYPE.EDIT);
        } else {
            Toy.toastMessage(this, R.string.locked_message);
        }
    }

    /**
     * オプションメニュー表示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * オプションメニュー項目選択時
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_menu_item:
                startActivity(new Intent(this, IdPwPreferenceActivity.class));
                return true;
            case R.id.export_menu_item:
                if (PasswordManager.getInstance(this).isMainPasswordDecrypted()) {
                    startActivity(new Intent(this, ExportActivity.class));
                } else {
                    (new MasterPasswordInput(this) {
                        public void onTureMasterPassword() {
                            updateLockImageButton();
                            startActivity(new Intent(IdPwListActivity.this, ExportActivity.class));
                        }
                    }).Ask();
                }
                return true;
            case R.id.import_menu_item:
                if (PasswordManager.getInstance(this).isMainPasswordDecrypted()) {
                    startActivity(new Intent(this, ImportActivity.class));
                } else {
                    (new MasterPasswordInput(this) {
                        public void onTureMasterPassword() {
                            updateLockImageButton();
                            startActivity(new Intent(IdPwListActivity.this, ImportActivity.class));
                        }
                    }).Ask();
                }
               return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.list_item_menu, menu);
        if (!(menuInfo instanceof AdapterContextMenuInfo)) {
            return;
        }
        View tv = ((AdapterContextMenuInfo)menuInfo).targetView;
        if (tv instanceof ViewGroup) {
            View cv = ((ViewGroup)tv).getChildAt(0);
            if (cv instanceof TextView) {
                menu.setHeaderTitle(((TextView) cv).getText());
            }
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuInfo menuInfo = item.getMenuInfo();
        if (!(menuInfo instanceof AdapterContextMenuInfo)) {
            return false;
        }
        return super.onContextItemSelected(item);
    }
   
    /**
     * 現在のマスターパスワードの状況に応じて LockImageButton　を変化させる
     */
    private void updateLockImageButton() {
        mLockImageButton
                .setLock(PasswordManager.getInstance(this).isMainPasswordDecrypted() == false);
    }

    

 
}
