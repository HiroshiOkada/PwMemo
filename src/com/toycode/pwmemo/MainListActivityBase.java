/*
 * Copyright (c) 2011 Hiroshi Okada (http://toycode.com/hiroshi/)
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *   1.The origin of this software must not be misrepresented; you must
 *     not claim that you wrote the original software. If you use this
 *     software in a product, an acknowledgment in the product
 *     documentation would be appreciated but is not required.
 *   
 *   2.Altered source versions must be plainly marked as such, and must
 *     not be misrepresented as being the original software.
 *   
 *   3.This notice may not be removed or altered from any source
 *     distribution.
 */

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
 * @author Hiroshi Okada
 */
public abstract class MainListActivityBase extends ListActivity implements OnClickListener,
        OnItemClickListener, Observer {

    int mSavedPosition = -1;
    protected LockImageButton mLockImageButton;
    protected SQLiteDatabase mDb = null;
    protected App mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.debugLog("MainListActivityBase::onCreate");
        mApp = App.GetApp(this);
        initPreference();
        setContentView(R.layout.list);        
        initButtons();
        initListView();
        if (mDb != null && mDb.isOpen()){
            mDb.close();
        }
        mDb = (new PwMemoDbOpenHelper(this)).getWritableDatabase();
        updateAdapter();
        mSavedPosition = -1;
    }
       
    @Override
    protected void onResume() {
        super.onResume();
        if (mDb == null || !mDb.isOpen()){
            mDb = (new PwMemoDbOpenHelper(this)).getWritableDatabase();
        }
        App.debugLog("MainListActivityBase::onResume");
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
        App.debugLog("MainListActivityBase::onPause");
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
    
    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        App.debugLog("MainListActivityBase::onStart");
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        App.debugLog("MainListActivityBase::onStop");
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.debugLog("MainListActivityBase::onDestroy");
    }

    /**
     * called when item clicked 
     * update last user access time.
     * if locked then show message and return;
     * otherwise call itemClickNormalTask();
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        TimeOutChecker.getInstance().onUser();
        if (isLocked()) {
            final int position0 = position;
            final long id0 = id;
            (new MasterPasswordInput(this) {
                public void onTureMasterPassword() {
                    updateLockImageButton();
                    itemClickNormalTask(position0, id0);
                }
            }).Ask();
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
     * When AddButton pressed
     */
   protected void onAddButton() {
        if (isLocked()) {
            (new MasterPasswordInput(this) {
                public void onTureMasterPassword() {
                    ContentValues values = new ContentValues();
                    values.put(Const.COLUMN.TITLE, "");
                    long id = mDb.insert(Const.TABLE.PWMEMO, null, values);
                    startEditActivity(id, Const.REQUEST_TYPE.NEW);
                }
            }).Ask();
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
    void createContextMenuTask(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo, final int menuResId) {
        TimeOutChecker.getInstance().onUser();
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
        DbRw.Data data = dbrw.getRecord(id);
        dbrw.cleanup();
        return data;
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
