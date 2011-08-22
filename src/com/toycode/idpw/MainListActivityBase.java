
package com.toycode.idpw;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
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

/**
 * MainListActivityBase is a base class for main Activity that display items list.
 * 
 * @author hiroshi
 */
public abstract class MainListActivityBase extends ListActivity implements OnClickListener,
        OnItemClickListener, Observer {

    protected LockImageButton mLockImageButton;
    protected SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreference();
        setContentView(R.layout.list);        
        initButtons();
        initListView();
        mDb = (new IdPwDbOpenHelper(this)).getWritableDatabase();
        updateAdapter();
    }
       
    @Override
    protected void onResume() {
        super.onResume();
        if (TimeOutChecker.getInstance().isTimeOut()) {
            PasswordManager.getInstance(this).unDecrypt();
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
    
    /**
     * called when item clicked 
     * update last user access time.
     * if locked then show messge and return;
     * otherwise call itemClickNormalTask();
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        TimeOutChecker.getInstance().onUser();
        if (isLocked()) {
            Toy.toastMessage(this, R.string.locked_message);
        } else {
            itemClickNormalTask(position, id);
        }
    }
    
    @Override
    public void update(Observable observable, Object data) {
        if( TimeOutChecker.getInstance().isTimeOut()) {
            PasswordManager.getInstance(this).unDecrypt();
            updateLockImageButton();    
        }
    }  
    /**
     * LockImageButton が押された時の処理
     */
    protected void onLockImageButton() {
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
   protected void onAddButton() {
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
    protected void onExitButton() {
        PasswordManager.getInstance(this).unDecrypt();
        finish();
    }

    /**
     * normal task when item clicked
     */
    abstract protected void itemClickNormalTask(int position, long id);

    /**
     * create ContextMenu
     * @param menu
     * @param v
     * @param menuInfo
     * @param menuResId
     */
    void createContextMenuTask(ContextMenu menu, View v, ContextMenuInfo menuInfo, int menuResId) {
        TimeOutChecker.getInstance().onUser();
        //  If locked Show the toast message and do nothing.
        if (isLocked()) {
            Toy.toastMessage(this, R.string.locked_message);
            return;
        }

        // Create context nemu
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(menuResId, menu);
        
        Cursor cursor = getCursorFromMenuInfo(menuInfo);
        if (cursor != null) {
            menu.setHeaderTitle(cursor.getString(1));
        } else {
            Toy.debugLog(this, "bad cursor");
        }        
    }

    protected void updateAdapter() {
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
    
    protected Cursor getCursorFromMenuInfo(ContextMenuInfo menuInfo) {
        if (!(menuInfo instanceof AdapterContextMenuInfo)) {
            return null;
        }
        int position = ((AdapterContextMenuInfo) menuInfo).position;
        return (Cursor) (getListAdapter().getItem(position));
    }

    /**
     * read data spcified id
     */
    protected DbRw.Data getData(long id) {
        byte[] mainPasswod = PasswordManager.getInstance(this).getDecryptedMainPassword();
        if (mainPasswod == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return null;
        }
        DbRw dbrw = new DbRw(mDb, mainPasswod);
        return dbrw.getRecord(id);
    }

    protected void initPreference() {
        IdPwPreferenceActivity.initPreferences(this);
        TimeOutChecker.getInstance().setUseTimeOut(IdPwPreferenceActivity.getAutoLock(this));
        TimeOutChecker.getInstance().setTimeOutSec(IdPwPreferenceActivity.getAutoLockTime(this));
    }

    protected void initButtons() {
        mLockImageButton = (LockImageButton) findViewById(R.id.lock_image_button);
        updateLockImageButton();
        mLockImageButton.setOnClickListener(this);
        findViewById(R.id.add_button).setOnClickListener(this);
        findViewById(R.id.exit_button).setOnClickListener(this);
    }
    
    protected void initListView() {
        ListView listView = getListView();
        listView.setEmptyView(findViewById(R.id.EmptyTextView));
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }

    /**
     * start EditActivity via intent.
     */
    protected void startEditActivity(long id, int requestCode) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Const.COLUMN.ID, id);
        intent.putExtra(Const.REQUEST_TYPE.NAME, requestCode);
        startActivityForResult(intent, requestCode);
    }

    /**
     * return ture when lockd
     */
    protected boolean isLocked() {
        return ! (PasswordManager.getInstance(this).isMainPasswordDecrypted());
    }
   
    /**
     * 現在のマスターパスワードの状況に応じて LockImageButton　を変化させる
     */
    protected void updateLockImageButton() {
        mLockImageButton
                .setLock(PasswordManager.getInstance(this).isMainPasswordDecrypted() == false);
    }


}
