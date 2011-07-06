package com.toycode.idpw;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.ViewStub;
import android.widget.Button;

import java.util.List;

public class ExportActivity extends Activity {
    
    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);
        if (checkOIActionPickFile()) {
            Button b = (Button)((ViewStub) findViewById(R.id.selectfile_viewstub)).inflate();
        }
    }
    
    
    private boolean checkOIActionPickFile() {
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
