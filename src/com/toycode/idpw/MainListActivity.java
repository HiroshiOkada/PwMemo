
package com.toycode.idpw;

import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class MainListActivity extends MainListActivityBase {

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
        createContextMenuTask(menu, v, menuInfo, R.menu.list_item_menu);
     }
    
    /**
     * Called when a context menu item selected 
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TimeOutChecker.getInstance().onUser();
        
        //  If locked do nothing. (maybe never occur.)
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
        String title = cursor.getString(1);
        DbRw.Data data = getData(id);
        switch (item.getItemId()) {
            case R.id.copy_userid_menu_item:
                if (data != null) {
                    App.copyTextToClipboard(this, data.getUserId());
                    App.toastMessage(this, R.string.copy_x, data.getUserId());
                }
                break;
            case R.id.copy_password_menu_item:
                if (data != null) {
                    App.copyTextToClipboard(this, data.getPassword());
                    App.toastMessage(this, R.string.copy_x_password, title);
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

    @Override
    protected void itemClickNormalTask(int position, long id) {
        startEditActivity(getListAdapter().getItemId(position), Const.REQUEST_TYPE.EDIT);        
    }
}
