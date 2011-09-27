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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class InterceptListActivity extends MainListActivityBase {
    
    private static final String REPLACE_KEY = "replace_key";

    String mSearchString = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchString = getIntent().getStringExtra(REPLACE_KEY);
        if (mSearchString != null) {
            setTitle( getTitle() + " " + mSearchString);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.add_button).setVisibility(View.GONE);
        if (isLocked()) {
            (new MasterPasswordInput(this) {
                public void onTureMasterPassword() {
                    updateLockImageButton();
                }
            }).Ask();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        TimeOutChecker.getInstance().onUser();
        if (isLocked()) {
            mApp.toastMessage(R.string.locked_message);
        } else {
            this.itemClickNormalTask(position, id);
        }
    }

    /**
     * Called when a list item long-pressed and context menu is about to be shown.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        createContextMenuTask(menu, v, menuInfo, R.menu.list_item_mushroom_menu);
     }    
    
    /**
     * Called when a context menu item selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TimeOutChecker.getInstance().onUser();

        // If locked do nothing. (maybe never occur.)
        if (isLocked()) {
            App.debugLog(this, "onContextItemSelected and locked");
            return false;
        }

        Cursor cursor = getCursorFromMenuInfo(item.getMenuInfo());
        if (cursor == null) {
            App.debugLog(this, "bad cursor");
            return false;
        }
        Long id = cursor.getLong(0);
        DbRw.Data data = getData(id);
        if (data == null) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.insert_userid_menu_item:
                finishWithResult(data.getUserId());
                break;
            case R.id.insert_password_menu_item:
                finishWithResult(data.getPassword());
                break;
            default:
                return false;
        }
        return true;
    }
    
    /**
     * No AddButton
     */
   protected void onAddButton() {
    }
   
   /**
    * ExitButton
    * return "" string as result
    */
   protected void onExitButton() {
       PasswordManager.getInstance(this).unDecrypt();
       finishWithResult("");
   }
   
    @Override
    protected void itemClickNormalTask(int position, long id) {
        String title = "";
        Cursor cursor = (Cursor) (getListAdapter().getItem(position));
        if (cursor != null) {
            title = cursor.getString(1);
        } else {
            App.debugLog(this, "bad cursor");
        }

        DbRw.Data data = getData(id);
        if (data != null) {
            mApp.toastMessage(R.string.copy_x_password, title);
            finishWithResult(data.getPassword());
        }
    }
 
    @Override    
    protected void updateAdapter() {
        if (App.isEmptyCharSequence(mSearchString)) {
            super.updateAdapter();
            return;
        }
        
        final String[] COLUMNS = {
                Const.COLUMN.ID, Const.COLUMN.TITLE
        };
        final String WHERE = Const.COLUMN.TITLE + " LIKE ?"; 
        final String[] FROM = {
            Const.COLUMN.TITLE
        };
        final int[] TO = {
            R.id.RowTitleTextView
        };
        String [] whereArgs = { "%" + mSearchString + "%"};
        Cursor cursor = mDb.query(
                Const.TABLE.PWMEMO,    // table 
                COLUMNS,             // columns
                WHERE,               // selection
                whereArgs,           // selectionArgs
                null,               // groupBy
                null,               // having
                Const.COLUMN.TITLE + " COLLATE NOCASE"    // orderBy
                );
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor,
                FROM, TO);
        setListAdapter(adapter);
    }

    private void finishWithResult(String result) {
        Intent intent = new Intent();
        intent.putExtra(REPLACE_KEY, result);
        setResult(RESULT_OK, intent);
        finish();
    }

}
