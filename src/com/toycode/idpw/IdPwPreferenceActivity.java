package com.toycode.idpw;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class IdPwPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.idpwpreference);
	}

}
