
package com.toycode.idpw;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends Activity implements OnClickListener {
    EditText mTitleEdit;
    EditText mUserIdEdit;
    EditText mPasswordEdit;
    EditText mMemoEdit;
    Button mOkButton;
    Long mId;
    SQLiteDatabase mDb;
    final String[] COLUMNS = {
            Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = (new IdPwDbOpenHelper(this)).getReadableDatabase();
        // アンロック状態でなければ終了
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted() == false) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return;
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mId = extras.getLong(Const.COLUMN.ID);
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
                readFromDb(mId);
            } else {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                updateDb(mId);
                mDb.close();
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

    private void readFromDb(Long id) {
        Cursor cursor = mDb.query(Const.TABLE.IDPW, COLUMNS, Const.COLUMN.ID
                + " = " + id, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            mTitleEdit.setText(cursor.getString(0));
            byte[] cryptdata = cursor.getBlob(1);
            setCryptDataToFileds(cryptdata);
        }
        cursor.close();
    }

    private String getStringFromJSONArray(JSONArray jsonArray, int n) {
        try {
            return jsonArray.getString(n);
        } catch (JSONException e) {
            return "";
        }
    }

    private void setCryptDataToFileds(byte[] cryptdata) {
        if (cryptdata != null && cryptdata.length >= OpenSSLAES128CBCCrypt.BLOCK_LENGTH) {
            byte[] password = PasswordManager.getInstance(this).getDecryptedMainPassword();
            if (password != null) {
                byte[] bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(password, cryptdata);
                String stringData = new String(bytesData);
                try {
                    JSONArray jsonArray = new JSONArray(stringData);
                    mUserIdEdit.setText(getStringFromJSONArray(jsonArray, 0));
                    mPasswordEdit.setText(getStringFromJSONArray(jsonArray, 1));
                    mMemoEdit.setText(getStringFromJSONArray(jsonArray, 2));
                } catch (JSONException e) {
                    mUserIdEdit.setText("");
                    mPasswordEdit.setText("");
                    mMemoEdit.setText("");
                }
            } else {
                Toy.toastMessage(this, R.string.timeout);
                finish();
            }
        }
    }

    private void putCryptDataFromFileds(ContentValues values) {
        byte[] password = PasswordManager.getInstance(this).getDecryptedMainPassword();
        if (password == null) {
            Toy.toastMessage(this, R.string.timeout);
            return;
        } else {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(mUserIdEdit.getText().toString());
            jsonArray.put(mPasswordEdit.getText().toString());
            jsonArray.put(mMemoEdit.getText().toString());
            byte[] bytesData = jsonArray.toString().getBytes();
            values.put(Const.COLUMN.CRIPTDATA,
                    OpenSSLAES128CBCCrypt.INSTANCE.encrypt(password, bytesData));
        }
    }

    private void updateDb(Long id) {
        String[] whereArgs = {
            id.toString()
        };
        ContentValues values = new ContentValues();
        values.put(Const.COLUMN.TITLE, mTitleEdit.getText().toString());
        putCryptDataFromFileds(values);
        mDb.update(Const.TABLE.IDPW, values, Const.COLUMN.ID + " = ?",
                whereArgs);
    }

    private void copyText( CharSequence text) {
        android.text.ClipboardManager cm  = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
    }
}
