package com.toycode.idpw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Class that handles when open a "*.idpw" file by implicit intents.
 */
public class ImplicitIntentImportActivity extends Activity implements OnClickListener {

    private EditText mPasswordEdittext;
    enum ReadMethod { MERGE, INSERT };
    private ReadMethod mReadMethod = ReadMethod.MERGE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.implicit_intent_import);
        findViewById(R.id.import_button).setOnClickListener(this);
        mPasswordEdittext = (EditText)findViewById(R.id.password_edittext);
        String action = getIntent().getAction();
        Uri uri = getIntent().getData();
        Toy.toastMessage(this, uri.toString());
        Toy.toastMessage(this, action);
        Toy.debugLog(this, uri.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.import_button:
                if (Toy.isEmptyTextView(mPasswordEdittext)) {
                    Toy.toastMessage(this, R.string.please_set_import_password);
                    return;
                }
        }
    }
 
    private class ReadFileTask extends AsyncTask<InputStream, Void, Boolean> {
        ProgressDialog mProgressDialog;
        int mErrorMessageId = 0;
        private final int READ_SIZE = 1024;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(ImplicitIntentImportActivity.this, "", "Reading...");
        }

        @Override
        protected Boolean doInBackground(InputStream... inputStreams) {
            SQLiteDatabase db = (new IdPwDbOpenHelper(ImplicitIntentImportActivity.this)).getReadableDatabase();
            if (db == null) {
                throw new RuntimeException("db==null");
            }

            byte[] mainPasswod = PasswordManager.getInstance(ImplicitIntentImportActivity.this).getDecryptedMainPassword();
            if (mainPasswod == null) {
                throw new RuntimeException("mainPasswod==null");
            }
            String json = "";
            try {
                byte[] password = mPasswordEdittext.getText().toString().getBytes();
                InputStream is = inputStreams[0];
                ReadableByteChannel ch = Channels.newChannel(is);
                ByteArrayOutputStream cryptBytesStream = new ByteArrayOutputStream();
                byte [] bufferArray = new byte[READ_SIZE];
                ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
                buffer.rewind();
                int readSize = 0;
                try {
                    while( (readSize = ch.read(buffer)) >= 0) {
                        if (readSize > 0) {
                            cryptBytesStream.write(bufferArray, 0, readSize);
                            buffer.rewind();
                        }
                    }
                } catch (IOException e) {
                    ;
                }
                
                byte[] cryptBytes = cryptBytesStream.toByteArray();
                ch.close();
                byte[] bytesData = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(password, cryptBytes);
                if (bytesData == null) {
                    mErrorMessageId = R.string.password_does_not_match;
                    return false;
                }
                json = new String(bytesData);
            } catch (FileNotFoundException e) {
                mErrorMessageId = R.string.file_not_found;
                return false;
            } catch (IOException e) {
                mErrorMessageId = R.string.file_read_error;
                return false;
            }

            DbRw dbrw = new DbRw(db, mainPasswod);
            switch (mReadMethod) {
                case MERGE:
                    dbrw.insertRecords(json);
                    break;
                case INSERT:
                    dbrw.mergeRecords(json);
                    break;
                default:
                    break;
            }
            dbrw.cleanup();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            if (result == false) {
                Toy.toastMessage(ImplicitIntentImportActivity.this, mErrorMessageId);
            }
            super.onPostExecute(result);
        }
        
     }
    
}
