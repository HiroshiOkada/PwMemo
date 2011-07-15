
package com.toycode.idpw;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EditActivity extends Activity implements OnClickListener {
    EditText mTitleEdit;
    EditText mUserIdEdit;
    EditText mPasswordEdit;
    EditText mMemoEdit;
    DbRw mDbRw;
    Long mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toy.debugLog(this, "onCreate(Bundle savedInstanceState)");
        SQLiteDatabase db = (new IdPwDbOpenHelper(this)).getReadableDatabase();
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
            Toy.debugLog(this, "mId=" + mId.toString());
            if (mId != null) {
                setTitle(R.string.edit);
                setContentView(R.layout.edit);
                mTitleEdit = (EditText) findViewById(R.id.title_textedit);
                mUserIdEdit = (EditText) findViewById(R.id.user_id_edittext);
                mPasswordEdit = (EditText) findViewById(R.id.password_edittext);
                mMemoEdit = (EditText) findViewById(R.id.memo_edittext);
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                fieldToDb();
                setResult(RESULT_OK, new Intent());
                finish();
                break;
            case R.id.cancel_button:
                setResult(RESULT_CANCELED, new Intent());
                finish();
                break;
            case R.id.copy_user_id_button:
                copyText(mUserIdEdit.getText());
                break;
            case R.id.copy_passwword_button:
                copyText(mPasswordEdit.getText());
                break;
            case R.id.copy_memo_button:
                copyText(mMemoEdit.getText());
                break;
            default:
                // did not come
                break;
        }
    }

    @Override
    public void onDestroy() {
        Toy.debugLog(this, "onDestroy");
        mDbRw.cleanup();
        mDbRw = null;
        super.onDestroy();
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

    private void copyText(CharSequence text) {
        android.text.ClipboardManager cm = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
    }

}
