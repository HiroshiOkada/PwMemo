
package com.toycode.idpw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IdPwDbOpenHelper extends SQLiteOpenHelper {

    public IdPwDbOpenHelper(Context context) {
        super(context, Const.DB.NAME, null, Const.DB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Const.TABLE.IDPW +
                    " ( " + Const.COLUMN.ID + " integer primary key autoincrement, " +
                            Const.COLUMN.TITLE + " text not null, " +
                            Const.COLUMN.CRIPTDATA + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Const.TABLE.IDPW + ";");
        onCreate(db);
    }

    /**
     * DBのデータを全部削除する
     */
    public void deleteaAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + Const.TABLE.IDPW + ";");
    }
}
