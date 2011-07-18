
package com.toycode.idpw;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class ImportActivity extends Activity implements OnClickListener {

    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";
    public static final String OI_TITLE_EXTRA = "org.openintents.extra.TITLE";
    public static final String OI_BUTTON_TEXT_EXTRA = "org.openintents.extra.BUTTON_TEXT";
    public static final String DEFALUT_OUTPUT_FILENAME = "idpw.dat";
    public static final int REQUEST_FILENAME = 0;

    private File mInputFile = null;
    private boolean mUseFileManeger;
    private EditText mPasswordEdittext;
    private EditText mFileNameEdittext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // アンロック状態でなければ終了
        if (PasswordManager.getInstance(this).isMainPasswordDecrypted() == false) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
            return;
        }
        if (checkOIActionPickFile()) {
            setContentView(R.layout.import_layout);
            mUseFileManeger = true;
        } else {
            setContentView(R.layout.import_input_filename);
            mUseFileManeger = false;
            mFileNameEdittext = (EditText)findViewById(R.id.filename_edittext);
            mFileNameEdittext.setText(getDefaultInputFile().toString());
        }
        mPasswordEdittext = (EditText)findViewById(R.id.import_password_edittext);
        findViewById(R.id.read_file_button).setOnClickListener(this);
        mInputFile = getDefaultInputFile();
    }

    private boolean checkOIActionPickFile() {
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_file_button:
                if (Toy.isEmptyTextView(mPasswordEdittext)) {
                    Toy.toastMessage(this, R.string.please_set_import_password);
                    return;
                }
                if (mUseFileManeger) {
                    startSaveFileManager();
                } else {
                    File file = new File(mFileNameEdittext.getText().toString());
                    if (file != null) {
                            readFile(file);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILENAME && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                File file = new File(uri.getPath());
                if (file != null) {
                   readFile(file);
                }
            }
        }
    }

    private void readFile(File file) {
        SQLiteDatabase db = (new IdPwDbOpenHelper(this)).getReadableDatabase();
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
            FileInputStream fos = new FileInputStream(file);
            FileChannel ch = fos.getChannel();
            fos.close();
            Toy.toastMessage(this, R.string.read_x_ok, file.toString());
        } catch (IOException e) {
            Toy.debugLog(this, e.toString());
            Toy.toastMessage(this, R.string.faild_read_x, file.toString());
        }
        dbrw.cleanup();
        mInputFile = file;
    }

    private void startSaveFileManager() {
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        intent.setData(Uri.fromFile(mInputFile));
        intent.putExtra(OI_TITLE_EXTRA, getString(R.string.select_folder));
        intent.putExtra(OI_BUTTON_TEXT_EXTRA, getString(R.string.read));
        try {
            startActivityForResult(intent, REQUEST_FILENAME);
        } catch (ActivityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private File getDefaultInputFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, DEFALUT_OUTPUT_FILENAME);
            return file;
        } else {
            return new File(Environment.getDataDirectory(), DEFALUT_OUTPUT_FILENAME);
        }
    }

}
