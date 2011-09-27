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
     * Delete all DB data.
     */
    public void deleteaAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + Const.TABLE.PWMEMO + ";");
        db.close();
    }
}
