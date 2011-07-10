package com.toycode.idpw;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * データベースを読み書きする
 * @author hiroshi
 *
 */
public final class DbRw {

    public static final class Data {
        public Data ( String title, String userId, String password, String memo) {
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
    
    public DbRw(SQLiteDatabase db, byte [] masterPassword) {
       mDB = db;
       mMainPassword = masterPassword;
    }

	public void cleanup() {
		if (mDB != null) {
			mDB.close();
			mDB = null;
		}
		if (mMainPassword != null) {
			mMainPassword = null;
		}
	}
    
    /**
     * レコードの更新
     * @param id
     * @param data
     * @param db
     * @param masterPassword
     */
    public final void updateRecord( Long id, Data data) {
        if ((mDB == null) || (mMainPassword == null)){
            throw new IllegalStateException();
        }
        
        String[] whereArgs = { id.toString()};
        ContentValues values = new ContentValues();
        values.put(Const.COLUMN.TITLE, data.getTitle());
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(data.getUserId());
        jsonArray.put(data.getPassword());
        jsonArray.put(data.getMemo());
        byte[] bytesData = jsonArray.toString().getBytes();
        values.put(Const.COLUMN.CRIPTDATA,
                OpenSSLAES128CBCCrypt.INSTANCE.encrypt(mMainPassword, bytesData));
        mDB.update(Const.TABLE.IDPW, values, Const.COLUMN.ID + " = ?",
                whereArgs);
    }
    
    /**
     * レコードの取得
     */
    public final Data getRecord( Long id) {
        if ((mDB == null) || (mMainPassword == null)){
            throw new IllegalStateException();
        }
        Cursor cursor = mDB.query(Const.TABLE.IDPW, COLUMNS, Const.COLUMN.ID
                + " = " + id, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(0);
            byte[] cryptdata = cursor.getBlob(1);
            cursor.close();
            if (cryptdata == null) {
                return new Data( title, null, null, null);
            }
            byte[] bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(mMainPassword, cryptdata);
            String [] array = {null,null,null};
            try {
                JSONArray jsonArray = new JSONArray(new String(bytesData));
                array[0] = jsonArray.getString(0);
                array[1] = jsonArray.getString(1);
                array[2] = jsonArray.getString(2);
                return new Data( title, array[0], array[1], array[2]);                
            } catch (JSONException e) {
                return new Data( title, array[0], array[1], array[2]);
            }            
        }
        cursor.close();
        return new Data( null, null, null, null);
    }
    
    private static final String[] COLUMNS = {
            Const.COLUMN.TITLE, Const.COLUMN.CRIPTDATA
    };
    
    private SQLiteDatabase mDB = null;
    private byte [] mMainPassword = null;
}
