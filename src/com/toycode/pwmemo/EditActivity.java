
package com.toycode.pwmemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import java.util.Observable;
import java.util.Observer;

public class EditActivity extends Activity implements OnClickListener, Observer, TextWatcher, OnFocusChangeListener {
    EditText mTitleEdit;
    EditText mUserIdEdit;
    EditText mPasswordEdit;
    EditText mMemoEdit;
    DbRw mDbRw;
    Long mId;
    Boolean mIsNewRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.debugLog(this, "onCreate(Bundle savedInstanceState)");
        SQLiteDatabase db = (new PwMemoDbOpenHelper(this)).getReadableDatabase();
        if (db == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return;
        }

        byte[] mainPasswod = PasswordManager.getInstance(this).getDecryptedMainPassword();
        if (mainPasswod == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return;
        }

        mDbRw = new DbRw(db, mainPasswod);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mId = extras.getLong(Const.COLUMN.ID);
            if (mId != null) {
                setContentView(R.layout.edit);
                mTitleEdit = setupEditText(R.id.title_textedit);
                mUserIdEdit = setupEditText(R.id.user_id_edittext);
                mPasswordEdit = setupEditText(R.id.password_edittext);
                mMemoEdit = setupEditText(R.id.memo_edittext);
                findViewById(R.id.copy_user_id_button).setOnClickListener(this);
                findViewById(R.id.copy_passwword_button).setOnClickListener(this);
                findViewById(R.id.copy_memo_button).setOnClickListener(this);
                findViewById(R.id.ok_button).setOnClickListener(this);
                findViewById(R.id.cancel_button).setOnClickListener(this);
                dbToField();
            } else {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
            
            switch (extras.getInt(Const.REQUEST_TYPE.NAME)) {
                case Const.REQUEST_TYPE.EDIT:
                    mIsNewRow = false;
                    setTitle(R.string.edit);
                    findViewById(R.id.user_id_edittext).requestFocus();                    
                    break;
                case Const.REQUEST_TYPE.NEW:
                    mIsNewRow = true;
                    setTitle(R.string.new_str);
                    findViewById(R.id.title_textedit).requestFocus();
                    break;
                default:
                    // never come
                    finish();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        TimeOutChecker.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        TimeOutChecker.getInstance().deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        App.debugLog(this, "onDestroy");
        mDbRw.cleanup();
        mDbRw = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        TimeOutChecker.getInstance().onUser();
        switch (v.getId()) {
            case R.id.ok_button:
                fieldToDb();
                setResult(RESULT_OK, new Intent());
                finish();
                break;
            case R.id.cancel_button:
                if (mIsNewRow) {
                    deleteItem();
                }
                setResult(RESULT_CANCELED, new Intent());
                finish();
                break;
            case R.id.copy_user_id_button:
                App.copyTextToClipboard(this, mUserIdEdit.getText());
                break;
            case R.id.copy_passwword_button:
                App.copyTextToClipboard(this, mPasswordEdit.getText());
                break;
            case R.id.copy_memo_button:
                App.copyTextToClipboard(this, mMemoEdit.getText());
                break;
            default:
                // did not come
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TimeOutChecker.getInstance().onUser();
        getMenuInflater().inflate(R.menu.edit_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case R.id.delete_menu_item:
               final AlertDialog alertDialog = new AlertDialog.Builder(this)
               .setTitle(getString(R.string.delete_this_item))
               .setPositiveButton(android.R.string.ok,
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                                   EditActivity.this.deleteItem();
                               }
                       }).setNegativeButton(android.R.string.cancel, null)
               .create();
               alertDialog.show();
               return true;
        }
       return false;
    }

    private void deleteItem() {
        mDbRw.deleteById(mId);
        finish();
    }
    
    private EditText setupEditText(int id) {
        EditText et = (EditText) findViewById(id);
        et.addTextChangedListener(this);
        et.setOnFocusChangeListener(this);
        return et;
    }
    private void dbToField() {
        DbRw.Data data = mDbRw.getRecord(mId);
        mTitleEdit.setText(data.getTitle());
        mUserIdEdit.setText(data.getUserId());
        mPasswordEdit.setText(data.getPassword());
        mMemoEdit.setText(data.getMemo());
    }

    private void fieldToDb() {
        DbRw.Data data = new DbRw.Data(
                mTitleEdit.getText().toString(),
                mUserIdEdit.getText().toString(),
                mPasswordEdit.getText().toString(),
                mMemoEdit.getText().toString());
        mDbRw.updateRecord(mId, data);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (TimeOutChecker.getInstance().isTimeOut()) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }        
    }

    @Override
    public void afterTextChanged(Editable s) {
        
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        TimeOutChecker.getInstance().onUser();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        TimeOutChecker.getInstance().onUser();
    }
}