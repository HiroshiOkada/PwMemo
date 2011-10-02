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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * The Activity for export data.
 * 
 * @author Hiroshi Okada
 *
 */
public class ExportActivity extends Activity implements OnClickListener {

    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";
    public static final String OI_TITLE_EXTRA = "org.openintents.extra.TITLE";
    public static final String OI_BUTTON_TEXT_EXTRA = "org.openintents.extra.BUTTON_TEXT";
    public static final int REQUEST_FILENAME = 0;
    public static final int EXPORT_FILE = 1;

    private File mOutputFile;
    private boolean mUseFileManeger;
    private EditText mPasswordEdittext;
    private EditText mFileNameEdittext;
    
    private App mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = App.GetApp(this);
        // If locked then finish
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted() == false) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return;
        }
        if (checkOIActionPickFile()) {
            setContentView(R.layout.export);
            mUseFileManeger = true;
        } else {
            setContentView(R.layout.export_input_filename);
            mUseFileManeger = false;
            mFileNameEdittext = (EditText)findViewById(R.id.filename_edittext);
            mFileNameEdittext.setText(mApp.getDefaultOutputFile().toString());
        }
        mPasswordEdittext = (EditText)findViewById(R.id.export_password_edittext);
        findViewById(R.id.write_file_button).setOnClickListener(this);
        Button exportButton = (Button)findViewById(R.id.export_button);
        exportButton.setOnClickListener(this);
        if (checkSendTo() == false) {
            exportButton.setEnabled(false);
        }
        mOutputFile = mApp.getDefaultOutputFile();
        
    }

    private boolean checkOIActionPickFile() {
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    
    private boolean checkSendTo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_file_button:
                if (App.isEmptyTextView(mPasswordEdittext)) {
                    mApp.toastMessage(R.string.please_set_export_password);
                    return;
                }
                if (mUseFileManeger) {
                    startSaveFileManager();
                } else {
                    File file = new File(mFileNameEdittext.getText().toString());
                    if (makeSureParentExsist(file)) {
                        if (file != null) {
                            wirteFile(file, true);
                        }
                    }
                }
                break;
            case R.id.export_button:
                //
                if (App.isEmptyTextView(mPasswordEdittext)) {
                    mApp.toastMessage(R.string.please_set_export_password);
                    return;
                }
                wirteFile(getFileStreamPath(App.DEFALUT_FILENAME), false);
                
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("application/octet-stream");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.withAppendedPath(ExportProvider.CONTENT_URI, App.DEFALUT_FILENAME));
                startActivityForResult(intent, EXPORT_FILE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILENAME:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        File file = new File(uri.getPath());
                        if (makeSureParentExsist(file)) {
                            if (file != null) {
                                wirteFile(file, true);
                            }
                        }
                    }
                }
                break;
            case EXPORT_FILE:
                break;
        }
        
    }

    private boolean makeSureParentExsist(File file) {
        File parent = file.getParentFile();
        if (parent.isDirectory()) {
            return true;
        }
        if (parent.mkdirs() == false) {
            mApp.toastMessage(R.string.cannot_make_x, parent.toString());
            return false;
        }
        return true;
    }

    private void wirteFile(File file, boolean showToast) {
        App.debugLog(this, "wirteFile:" + file.toString());
        SQLiteDatabase db = (new PwMemoDbOpenHelper(this)).getReadableDatabase();
        if (db == null) {
            throw new RuntimeException("db==null");
        }

        byte[] mainPasswod = PasswordManager.getInstance(this).getDecryptedMainPassword();
        if (mainPasswod == null) {
            throw new RuntimeException("mainPasswod==null");
        }

        DbRw dbrw = new DbRw(db, mainPasswod);

        String data = dbrw.getAllRecords();

        try {
            byte [] password = mPasswordEdittext.getText().toString().getBytes();
            byte [] cryptBytes = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(password, data.getBytes());
            ByteBuffer dataBuf = ByteBuffer.wrap(cryptBytes);
            FileOutputStream fos = new FileOutputStream(file);
            FileChannel ch = fos.getChannel();
            ch.write(dataBuf);
            ch.close();
            fos.close();
            if (showToast) {
                mApp.toastMessage(R.string.write_x_ok, file.toString());
            }
        } catch (IOException e) {
            App.debugLog(this, e.toString());
            if (showToast) {
                mApp.toastMessage(R.string.faild_writing_x, file.toString());
            }
        }
        dbrw.cleanup();
        mOutputFile = file;
    }

    private void startSaveFileManager() {
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        intent.setData(Uri.fromFile(mOutputFile));
        intent.putExtra(OI_TITLE_EXTRA, getString(R.string.select_folder));
        intent.putExtra(OI_BUTTON_TEXT_EXTRA, getString(R.string.write_str));
        try {
            startActivityForResult(intent, REQUEST_FILENAME);
        } catch (ActivityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
