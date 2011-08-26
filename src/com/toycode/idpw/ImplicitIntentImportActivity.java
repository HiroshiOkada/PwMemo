package com.toycode.idpw;

import android.app.Activity;
import android.os.Bundle;

/**
 * Class that handles when open a "*.idpw" file by implicit intents.
 */
public class ImplicitIntentImportActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.implicit_intent_import);
    }

}
