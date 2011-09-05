
package com.toycode.pwmemo;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * データベースを読み書きする
 *
 * 廃棄する前に必ず cleanup() を呼ぶこと
 * @author hiroshi
 */
public final class DbRw {
    static final int INDENT_SPACES=4;

    public static final class Data {
        public Data(String title, String userId, String password, String memo) {
            mTitle = title != null ? title : "";
            mUserId = userId != null ? userId : "";
            mPassword = password != null ? password : "";
            mMemo = memo != null ? memo : "";
        }

        public String getTitle() {
            return mTitle;
        }

        public String getUserId() {
            return mUserId;
        }

        public String getPassword() {
            return mPassword;
        }

        public String getMemo() {
            return mMemo;
        }

        private String mTitle;
        private String mUserId;
        private String mPassword;
        private String mMemo;
    };

    public DbRw(SQLiteDatabase db, byte[] masterPassword) {
        mDb = db;
        mMainPassword = masterPassword;
    }

    public void cleanup() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
        if (mMainPassword != null) {
            mMainPassword = null;
        }
    }

    /**
     * レコードの更新
     * 
     * @param id
     * @param data
     * @param db
     * @param masterPassword
     */
    public void updateRecord(Long id, Data data) {
        if ((mDb == null) || (mMainPassword == null)) {
            throw new IllegalStateException();
        }

        String[] whereArgs = {
            id.toString()
        };
        ContentValues values = new ContentValues();
        values.put(Const.COLUMN.TITLE, data.getTitle());
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(data.getUserId());
        jsonArray.put(data.getPassword());
        jsonArray.put(data.getMemo());
        byte[] bytesData = jsonArray.toString().getBytes();
        values.put(Const.COLUMN.CRIPTDATA,
                OpenSSLAES128CBCCrypt.INSTANCE.encrypt(mMainPassword, bytesData));
        mDb.update(Const.TABLE.PWMEMO, values, Const.COLUMN.ID + " = ?",
                whereArgs);
    }

    /**
     * レコードの取得
     */
    public Data getRecord(Long id) {
        final String[] COLUMNS = { Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA};
        if ((mDb == null) || (mMainPassword == null)) {
            throw new IllegalStateException();
        }
        Cursor cursor = mDb.query(Const.TABLE.PWMEMO, COLUMNS, Const.COLUMN.ID
                + " = " + id, null, null, null, null);
       if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(0);
            byte[] cryptdata = cursor.getBlob(1);
            cursor.close();
            if (cryptdata == null) {
                return new Data(title, null, null, null);
            }
            byte[] bytesData = new byte[0];
            try {
                bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(mMainPassword, cryptdata);
            } catch (CryptException e1) {
                App.debugLog(this, "CryptException:" + e1.GetMsgId());
            }
            String[] array = {
                    null, null, null
            };
            try {
                JSONArray jsonArray = new JSONArray(new String(bytesData));
                array[0] = jsonArray.getString(0);
                array[1] = jsonArray.getString(1);
                array[2] = jsonArray.getString(2);
                return new Data(title, array[0], array[1], array[2]);
            } catch (JSONException e) {
                return new Data(title, array[0], array[1], array[2]);
            }
        }
        cursor.close();
        return new Data(null, null, null, null);
    }

    public void deleteById(Long id) {
        if ((mDb == null) || (mMainPassword == null)) {
            throw new IllegalStateException();
        }
        String [] whereArgs = { id.toString() };
        mDb.delete(Const.TABLE.PWMEMO, Const.COLUMN.ID
                + " = ?", whereArgs);
    }
    /**
     * 全てのレコードを json 文字列データとして得る
     */
    public String getAllRecords() {
        final String[] COLUMNS = { Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA};
        if ((mDb == null) || (mMainPassword == null)) {
            throw new IllegalStateException();
        }
        Cursor cursor = mDb.query(Const.TABLE.PWMEMO, COLUMNS,
                null, null, null, null, null, null);
        JSONArray root = new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                JSONArray child = new JSONArray();
                child.put(cursor.getString(0));
                byte[] cryptdata = cursor.getBlob(1);
                if (cryptdata != null) {
                    byte[] bytesData = new byte[0];
                    try {
                        bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(mMainPassword, cryptdata);
                    } catch (CryptException e1) {
                        App.debugLog(this, "CryptException:" + e1.GetMsgId());
                    }
                    try {
                        JSONArray pwMemo = new JSONArray(new String(bytesData));
                        child.put(pwMemo.getString(0));
                        child.put(pwMemo.getString(1));
                        child.put(pwMemo.getString(2));
                    } catch (JSONException e) {
                    }
                }
                root.put(child);
            } while (cursor.moveToNext());
        }
        cursor.close();
        try {
            return root.toString(INDENT_SPACES);
        } catch (JSONException e) {
            App.debugLog(this, e.toString());
            return "[]";
        }
    }

    /**
     * Insert records with the JSON data.
     * The record with the same title and data isn't duplicated.
     * 
     * @param json
     */
    public void insertRecords(String json) {
        // TEMPORARY_FLAGS == 0 : not changed
        // TEMPORARY_FLAGS == 1 : updated or inserted 
        clearAllTemporaryFlags();
        JSONArray root;
        try {
            root = new JSONArray(json);
        } catch (JSONException e1) {
            App.debugLog(this, e1.getMessage());
            return;
        }
        for (int i=0; i<root.length(); i++){
            try {
                JSONArray record = root.getJSONArray(i);
                String title = record.getString(0);
                String userId = record.getString(1);
                String password = record.getString(2);
                String memo = record.getString(3);
                insertRecord(title, userId, password, memo);
            } catch (JSONException e) {
                // only disregarded.
            }
        }
    }
    
    /**
     * Merge records with the JSON data.
     * Data with the same title and user ID is overwritten. 
     * 
     * @param json
     */
    public void mergeRecords(String json) {
        // TEMPORARY_FLAGS == 0 : not changed
        // TEMPORARY_FLAGS == 1 : updated or inserted 
        clearAllTemporaryFlags();
        JSONArray root;
        try {
            root = new JSONArray(json);
        } catch (JSONException e1) {
            App.debugLog(this, e1.getMessage());
            return;
        }
        for (int i=0; i<root.length(); i++){
            try {
                JSONArray record = root.getJSONArray(i);
                String title = record.getString(0);
                String userId = record.getString(1);
                String password = record.getString(2);
                String memo = record.getString(3);
                mergeRecord(title, userId, password, memo);
            } catch (JSONException e) {
                // only disregarded.
            }
        }
    }

    /**
     * Insert one record
     */
    private void insertRecord(String newTitle, String newUserId, String newPassword, String newMemo) {
        final String[] COLUMNS = { Const.COLUMN.ID, Const.COLUMN.CRIPTDATA};
        // TEMPORARY_FLAGS == 0 : not changed
        final String SELECTION = Const.COLUMN.TEMPORARY_FLAGS + " = 0  AND " + Const.COLUMN.TITLE + " = ?"; 
        String [] selectionArgs = { newTitle};

        Cursor cursor = mDb.query(
                Const.TABLE.PWMEMO,    // table 
                COLUMNS,
                SELECTION,
                selectionArgs,
                null,               // groupBy
                null,               // having
                null    // orderBy
                );
        Long id = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] cryptdata = cursor.getBlob(1);
                if (cryptdata != null) {
                    byte[] bytesData = new byte[0];
                    try {
                        bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(mMainPassword, cryptdata);
                    } catch (CryptException e1) {
                        App.debugLog(this, "CryptException:" + e1.GetMsgId());
                    }
                    try {
                        JSONArray pwMemo = new JSONArray(new String(bytesData));
                        if (pwMemo.getString(0).equals(newUserId)  &&
                              pwMemo.getString(1).equals(newPassword) &&
                              pwMemo.getString(2).equals(newMemo)) {
                            id = cursor.getLong(0);
                        }
                    } catch (JSONException e) {
                    }
                }
            } while (cursor.moveToNext());
        }
        if (id == null) {
            ContentValues values = new ContentValues();
            values.put(Const.COLUMN.TITLE, newTitle);
            id = mDb.insert(Const.TABLE.PWMEMO, null, values);
        }
        ContentValues values = new ContentValues();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(newUserId);
        jsonArray.put(newPassword);
        jsonArray.put(newMemo);
        byte[] newBytesData = jsonArray.toString().getBytes();
        values.put(Const.COLUMN.CRIPTDATA,
                OpenSSLAES128CBCCrypt.INSTANCE.encrypt(mMainPassword, newBytesData));
        // TEMPORARY_FLAGS == 1 : updated or inserted 
        values.put(Const.COLUMN.TEMPORARY_FLAGS, 1);
        String [] whereArgs = { id.toString()};
        mDb.update(Const.TABLE.PWMEMO, values, Const.COLUMN.ID + " = ?", whereArgs);
    }

    /**
     * Merge one record
     */
    private void mergeRecord(String newTitle, String newUserId, String newPassword, String newMemo) {
        final String[] COLUMNS = { Const.COLUMN.ID};
        // TEMPORARY_FLAGS == 0 : not changed
        final String SELECTION = Const.COLUMN.TEMPORARY_FLAGS + " = 0  AND " + Const.COLUMN.TITLE + " = ?"; 
        String [] selectionArgs = { newTitle};

        Cursor cursor = mDb.query(
                Const.TABLE.PWMEMO,    // table 
                COLUMNS,
                SELECTION,
                selectionArgs,
                null,               // groupBy
                null,               // having
                null    // orderBy
                );
        Long id = null;
        if (cursor.moveToFirst()) {
           id = cursor.getLong(0);
        } else {
           ContentValues values = new ContentValues();
           values.put(Const.COLUMN.TITLE, newTitle);
           id = mDb.insert(Const.TABLE.PWMEMO, null, values);
        }
        ContentValues values = new ContentValues();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(newUserId);
        jsonArray.put(newPassword);
        jsonArray.put(newMemo);
        byte[] newBytesData = jsonArray.toString().getBytes();
        values.put(Const.COLUMN.CRIPTDATA,
                OpenSSLAES128CBCCrypt.INSTANCE.encrypt(mMainPassword, newBytesData));
        // TEMPORARY_FLAGS == 1 : updated or inserted 
        values.put(Const.COLUMN.TEMPORARY_FLAGS, 1);
        String [] whereArgs = { id.toString()};
        mDb.update(Const.TABLE.PWMEMO, values, Const.COLUMN.ID + " = ?", whereArgs);
    }

    /**
     * Set all TEMPORARY_FLAGS to 0
     */
    private void clearAllTemporaryFlags() {
        ContentValues values = new ContentValues();
        values.put(Const.COLUMN.TEMPORARY_FLAGS, 0);
        mDb.update(Const.TABLE.PWMEMO, values, null, null);
    }
    

    private SQLiteDatabase mDb = null;
    private byte[] mMainPassword = null;
}
