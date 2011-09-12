package com.toycode.pwmemo.test;

import com.toycode.pwmemo.Const;
import com.toycode.pwmemo.DbRw;
import com.toycode.pwmemo.PasswordManager;
import com.toycode.pwmemo.PwMemoDbOpenHelper;
import com.toycode.pwmemo.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import java.lang.reflect.Method;

public class DbRwTest extends AndroidTestCase {

    private SQLiteDatabase mDb;
    private DbRw mDbRw;
    private byte[] mPasswod = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    
    public void testDataClass() {
        DbRw.Data data = new DbRw.Data("title", "userId", "password", "memo");
        assertEquals("title", data.getTitle());
        assertEquals("userId", data.getUserId());
        assertEquals("password", data.getPassword());
        assertEquals("memo", data.getMemo());
        data = new DbRw.Data(null, null, null, null);
        assertEquals("", data.getTitle());
        assertEquals("", data.getUserId());
        assertEquals("", data.getPassword());
        assertEquals("", data.getMemo());
    }
    
    public void testInsertRecord() {
        mDbRw.insertRecord("title1", "userId1", "password1", "memo1");
        Long id = getIdByTitle("title1");
        assertNotNull(id);
        DbRw.Data data = mDbRw.getRecord(id);
        assertEquals("title1", data.getTitle());
        assertEquals("userId1", data.getUserId());
        assertEquals("password1", data.getPassword());
        assertEquals("memo1", data.getMemo());
    }
    
    private Long getIdByTitle(String title)
    {
        final String[] COLUMNS = {
                Const.COLUMN.ID
        };
        final String WHERE = Const.COLUMN.TITLE + " = ?"; 
        String whereArgs[] = {title};
        
        Cursor cursor = mDb.query(
            Const.TABLE.PWMEMO,    // table 
            COLUMNS,             // columns
            WHERE,               // selection
            whereArgs,           // selectionArgs
            null,               // groupBy
            null,               // having
            Const.COLUMN.TITLE + " COLLATE NOCASE"    // orderBy
            );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getLong(0);
        }
        return null;
    }
    
    /**
     * prepare a (empty) database & dbrw
     */
    @Override
    protected void setUp() throws Exception {
        PwMemoDbOpenHelper helper = (new PwMemoDbOpenHelper(getContext()));
        helper.deleteaAll();
        mDb = helper.getWritableDatabase();
        mDbRw = new DbRw(mDb, mPasswod);
    }
    
    /**
     */
    @Override
    protected void tearDown() throws Exception {
        (new PwMemoDbOpenHelper(getContext())).deleteaAll();
        mDbRw.cleanup();
        
    }

}
