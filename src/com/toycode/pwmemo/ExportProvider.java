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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * The content data provider for export.
 * 
 * @author Hiroshi Okada
 *
 */
public class ExportProvider extends ContentProvider {

    public static final Uri CONTENT_URI =
        Uri.parse( "content://com.toycode.pwmemo.exportprovider");
    
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!App.DEFALUT_FILENAME.equals(uri.getLastPathSegment())) {
            App.debugLog(this, uri.toString() + App.DEFALUT_FILENAME + " != " + uri.getLastPathSegment());
            throw new FileNotFoundException(uri.toString() + " not found.");
        }
        if (!"r".equals(mode)) {
            App.debugLog(this, mode + " is prohibited.");
            throw new SecurityException(mode + " is prohibited.");
        }
        File f = getContext().getFileStreamPath(App.DEFALUT_FILENAME);
        ParcelFileDescriptor pf = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        return pf;
    }   
    
    /**
     * Do nothing
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Do nothing
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Do nothing
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /**
     * Do nothing
     */
    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * Do nothing
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        return null;
    }

    /**
     * Do nothing
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
