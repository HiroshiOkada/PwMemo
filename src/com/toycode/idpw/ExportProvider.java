package com.toycode.idpw;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

public class ExportProvider extends ContentProvider {

    public static final Uri CONTENT_URI =
        Uri.parse( "content://com.toycode.idpw.exportprovider");
    
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!FileUtils.DEFALUT_FILENAME.equals(uri.getLastPathSegment())) {
            Toy.debugLog(this, uri.toString() + FileUtils.DEFALUT_FILENAME + " != " + uri.getLastPathSegment());
            throw new FileNotFoundException(uri.toString() + " not found.");
        }
        if (!"r".equals(mode)) {
            Toy.debugLog(this, mode + " is prohibited.");
            throw new SecurityException(mode + " is prohibited.");
        }
        File f = getContext().getFileStreamPath(FileUtils.DEFALUT_FILENAME);
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
