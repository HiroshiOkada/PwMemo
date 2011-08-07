
package com.toycode.idpw;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import java.util.Observable;
import java.util.Observer;

public class MainListActivity extends ListActivity implements OnClickListener,
        OnItemClickListener, Observer {

    LockImageButton mLockImageButton;
    SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        mLockImageButton = (LockImageButton) findViewById(R.id.lock_image_button);
        updateLockImageButton();
        mLockImageButton.setOnClickListener(this);

        findViewById(R.id.add_button).setOnClickListener(this);
        findViewById(R.id.exit_button).setOnClickListener(this);

        ListView listView = getListView();
        listView.setEmptyView(findViewById(R.id.EmptyTextView));
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

        mDb = (new IdPwDbOpenHelper(this)).getWritableDatabase();
        updateAdapter();

    }
       
    @Override
    protected void onResume() {
        super.onResume();
        if (TimeOutChecker.getInstance().isTimeOut()) {
            finish();
        }
        if (!PasswordManager.getInstance(this).isMainPasswordExist()) {
            Intent i = new Intent(this, DeclarMasterPasswordActivity.class);
            startActivityForResult(i, Const.REQUEST_TYPE.NEW);
        } else {
            mLockImageButton.setLock(isLocked());
            updateAdapter();
        }
        TimeOutChecker.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        TimeOutChecker.getInstance().deleteObserver(this);
        super.onPause();
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
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor,
                FROM, TO);
        setListAdapter(adapter);
    }

    /**
     * 各ボタンが押されたときの処理 ボタンに合わせたメソッドを呼び出す。
     */
    @Override
    public void onClick(View v) {
        TimeOutChecker.getInstance().onUser();
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
        if (isLocked()) {
            (new MasterPasswordInput(this) {
                public void onTureMasterPassword() {
                    updateLockImageButton();
                }
            }).Ask();
        } else {
            PasswordManager.getInstance(this).unDecrypt();
            updateLockImageButton();
        }
    }

    /**
     * AddButton が押された時の処理
     */
    private void onAddButton() {
        if (isLocked()) {
            Toy.toastMessage(this, R.string.locked_message);
        } else {
            ContentValues values = new ContentValues();
            values.put(Const.COLUMN.TITLE, "");
            long id = mDb.insert(Const.TABLE.IDPW, null, values);
            startEditActivity(id, Const.REQUEST_TYPE.NEW);
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
        TimeOutChecker.getInstance().onUser();
        if (isLocked()) {
            Toy.toastMessage(this, R.string.locked_message);
        } else {
            startEditActivity(getListAdapter().getItemId(position), Const.REQUEST_TYPE.EDIT);
        }
    }

    /**
     * オプションメニュー表示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TimeOutChecker.getInstance().onUser();
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * オプションメニュー項目選択時
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TimeOutChecker.getInstance().onUser();
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
                            startActivity(new Intent(MainListActivity.this, ExportActivity.class));
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
                            startActivity(new Intent(MainListActivity.this, ImportActivity.class));
                        }
                    }).Ask();
                }
               return true;
        }
        return false;
    }

    /**
     * Called when a list item long-pressed and context menu is about to be shown.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        TimeOutChecker.getInstance().onUser();
        //  If locked Show the toast message and do nothing.
        if (isLocked()) {
            Toy.toastMessage(this, R.string.locked_message);
            return;
        }

        // Create context nemu
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.list_item_menu, menu);
       
        // set menu title
        if (!(menuInfo instanceof AdapterContextMenuInfo)) {
            Toy.debugLog(this, "bad menuInfo");
            return;
        }
        int position = ((AdapterContextMenuInfo)menuInfo).position;
        Cursor cursor = (Cursor)(getListAdapter().getItem(position));
        if (cursor != null) {
            Toy.debugLog(this, "bad cursor");
        }
        menu.setHeaderTitle(cursor.getString(1));
     }

    /**
     * Called when a context menu item selected 
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TimeOutChecker.getInstance().onUser();

        //  If locked do nothing. (maybe never occur.)
        if (isLocked()) {
            Toy.debugLog(this, "onContextItemSelected and locked");
            return false;
        }
       
        // get Id and title
        ContextMenuInfo menuInfo = item.getMenuInfo();
        if (!(menuInfo instanceof AdapterContextMenuInfo)) {
            return false;
        }
        int position = ((AdapterContextMenuInfo)menuInfo).position;
        Cursor cursor = (Cursor)(getListAdapter().getItem(position));
        if (cursor == null) {
            Toy.debugLog(this, "bad cursor");
            return false;
        }
        Long id = cursor.getLong(0);
        String title = cursor.getString(1);
        DbRw.Data data;
        switch (item.getItemId()) {
            case R.id.copy_userid_menu_item:
                data = getData(id);
                if (data != null) {
                    Toy.copyTextToClipboard(this, data.getUserId());
                    Toy.toastMessage(this, R.string.copy_x, data.getUserId());
                }
                break;
            case R.id.copy_password_menu_item:
                data = getData(id);
                if (data != null) {
                    Toy.copyTextToClipboard(this, data.getPassword());
                    Toy.toastMessage(this, R.string.copy_x_password, title);
                }
                break;
            case R.id.edit_menu_item:
                startEditActivity(id, Const.REQUEST_TYPE.EDIT);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * read data spcified id
     */
    private DbRw.Data getData(long id) {
        byte[] mainPasswod = PasswordManager.getInstance(this).getDecryptedMainPassword();
        if (mainPasswod == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return null;
        }
        DbRw dbrw = new DbRw(mDb, mainPasswod);
        return dbrw.getRecord(id);
    }
    
    /**
     * start EditActivity via intent.
     */
    private void startEditActivity(long id, int requestCode) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Const.COLUMN.ID, id);
        startActivityForResult(intent, requestCode);
    }

    /**
     * return ture when lockd
     */
    private boolean isLocked() {
        return ! (PasswordManager.getInstance(this).isMainPasswordDecrypted());
    }
   
    /**
     * 現在のマスターパスワードの状況に応じて LockImageButton　を変化させる
     */
    private void updateLockImageButton() {
        mLockImageButton
                .setLock(PasswordManager.getInstance(this).isMainPasswordDecrypted() == false);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable == TimeOutChecker.getInstance()) {
            PasswordManager.getInstance(this).unDecrypt();
            updateLockImageButton();    
        }        
    }
}
