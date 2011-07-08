
package com.toycode.idpw;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.List;

public class ExportActivity extends Activity implements OnClickListener {

    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

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
        } else {
            setContentView(R.layout.export_input_filename);
        }
        findViewById(R.id.write_file_button).setOnClickListener(this);
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
        // TODO Auto-generated method stub

    }
}
