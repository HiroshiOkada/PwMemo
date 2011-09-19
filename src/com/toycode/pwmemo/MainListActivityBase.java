
package com.toycode.pwmemo;

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

    int mSavedPosition = -1;
    protected LockImageButton mLockImageButton;
    protected SQLiteDatabase mDb;
    protected App mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = App.GetApp(this);
        initPreference();
        setContentView(R.layout.list);        
        initButtons();
        initListView();
        mDb = (new PwMemoDbOpenHelper(this)).getWritableDatabase();
        updateAdapter();
        mSavedPosition = -1;
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
        if (mSavedPosition != -1) {
            if (mSavedPosition >= getListAdapter().getCount()) {
                mSavedPosition = getListAdapter().getCount()-1;
            }
            getListView().setSelection(mSavedPosition);
            getListView().clearFocus();
        }
        TimeOutChecker.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        mSavedPosition = getListView().getFirstVisiblePosition();
        TimeOutChecker.getInstance().deleteObserver(this);
        super.onPause();
    }

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
            mApp.toastMessage(R.string.locked_message);
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
     * When LockImageButton pressed
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
     * WHen AddButton pressed
     */
   protected void onAddButton() {
        if (isLocked()) {
            mApp.toastMessage(R.string.locked_message);
        } else {
            ContentValues values = new ContentValues();
            values.put(Const.COLUMN.TITLE, "");
            long id = mDb.insert(Const.TABLE.PWMEMO, null, values);
            startEditActivity(id, Const.REQUEST_TYPE.NEW);
        }
    }

    /**
     * When ExitButton pressed
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
            mApp.toastMessage(R.string.locked_message);
            return;
        }

        // Create context nemu
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(menuResId, menu);
        
        Cursor cursor = getCursorFromMenuInfo(menuInfo);
        if (cursor != null) {
            menu.setHeaderTitle(cursor.getString(1));
        } else {
            App.debugLog(this, "bad cursor");
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
                Const.TABLE.PWMEMO,    // table 
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
        PwMemoPreferenceActivity.initPreferences(this);
        TimeOutChecker.getInstance().setUseTimeOut(PwMemoPreferenceActivity.getAutoLock(this));
        TimeOutChecker.getInstance().setTimeOutSec(PwMemoPreferenceActivity.getAutoLockTime(this));
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
     * Change LockImageButton according to mainpassword decrypted or not.
     */
    protected void updateLockImageButton() {
        mLockImageButton
                .setLock(PasswordManager.getInstance(this).isMainPasswordDecrypted() == false);
    }
}
