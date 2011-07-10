
package com.toycode.idpw;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.util.List;

public class ExportActivity extends Activity implements OnClickListener {

    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";
    public static final String OI_TITLE_EXTRA = "org.openintents.extra.TITLE";
    public static final String OI_BUTTON_TEXT_EXTRA = "org.openintents.extra.BUTTON_TEXT";
    public static final String OUTPUT_FILENAME = "idpw.crypt";
    public static final int REQUEST_FILENAME = 0;

    private File mOutputFile;
    private boolean mUseFileManeger;

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
            setContentView(R.layout.export);
            mUseFileManeger = true;
        } else {
            setContentView(R.layout.export_input_filename);
            mUseFileManeger = false;
        }
        findViewById(R.id.write_file_button).setOnClickListener(this);
        mOutputFile = getDefaultOutputFile();
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
            case R.id.write_file_button:
                if (mUseFileManeger) {
                    startSaveFileManager();
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
                    Toy.toastMessage(this, file.toString());
                }
            }
        }
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

    private File getDefaultOutputFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, OUTPUT_FILENAME);
            file.mkdirs();
            return file;
        } else {
            return new File(Environment.getDataDirectory(), OUTPUT_FILENAME);
        }
    }

}
