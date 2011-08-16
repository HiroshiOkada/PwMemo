package com.toycode.idpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class IdPwPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.idpwpreference);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 * Initialize SharedPreferences that  will set by this PreferenceActivity.
	 * If the value is not set, the default value is set. If the value was already set, it doesn't change. 
	 * @param context
	 */
	public static void initPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String autolock_key = context.getString(R.string.autolock_key);
		String autolocktime_key = context.getString(R.string.autolocktime_key);
		boolean isAutoLock = prefs.getBoolean(autolock_key, true);
		long autoLockTimeSec = prefs.getLong(autolocktime_key, TimeOutChecker.DEFAULT_TIMEOUT_SEC);
		Editor edit = prefs.edit();
		edit.putBoolean(context.getString(R.string.autolock_key), isAutoLock);
		edit.putString(context.getString(R.string.autolocktime_key), Long.toString(autoLockTimeSec));
		edit.commit();
	}

	/**
	 * 
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String keyname) {
		if (keyname == null) {
			return;
		}
		String autolock_key = getString(R.string.autolock_key);
		String autolocktime_key = getString(R.string.autolocktime_key);
		if (keyname.equals(autolock_key)) {
			TimeOutChecker.getInstance().setUseTimeOut(prefs.getBoolean(autolock_key, true));
		} else if (keyname.equals(autolocktime_key)) {
			String timeout_sec_str = prefs.getString(autolocktime_key, Long.toString(TimeOutChecker.DEFAULT_TIMEOUT_SEC));
			TimeOutChecker.getInstance().setTimeOutSec(Long.parseLong(timeout_sec_str));			
		}
	}
}