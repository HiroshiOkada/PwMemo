
package com.toycode.pwmemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PwMemoDbOpenHelper extends SQLiteOpenHelper {

    public PwMemoDbOpenHelper(Context context) {
        super(context, Const.DB.NAME, null, Const.DB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Const.TABLE.PWMEMO +
                    " ( " + Const.COLUMN.ID + " integer primary key autoincrement," +
                            Const.COLUMN.TITLE + " text not null," +
                            Const.COLUMN.CRIPTDATA + " text," +
                            Const.COLUMN.TEMPORARY_FLAGS + " integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Const.TABLE.PWMEMO + ";");
        onCreate(db);
    }

    /**
     * DBのデータを全部削除する
     */
    public void deleteaAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + Const.TABLE.PWMEMO + ";");
    }
}
