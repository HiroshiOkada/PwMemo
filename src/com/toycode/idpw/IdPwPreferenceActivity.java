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
	    setAutoLock(context, getAutoLock(context));
	    setAutoLockTime(context, getAutoLockTime(context));
	}

	public static boolean getAutoLock(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String autolock_key = context.getString(R.string.autolock_key);
        return prefs.getBoolean(autolock_key, true);
    }

    public static long getAutoLockTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String autolocktime_key = context.getString(R.string.autolocktime_key);
        String autolocktime_str = prefs.getString(autolocktime_key, Long.toString(TimeOutChecker.DEFAULT_TIMEOUT_SEC));
        return Long.parseLong(autolocktime_str);
    }

	/**
	 * 
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String keyname) {
	    TimeOutChecker.getInstance().setUseTimeOut(getAutoLock(this));
	    TimeOutChecker.getInstance().setTimeOutSec(getAutoLockTime(this));
	}
	
	
    private static void setAutoLock(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        String autolock_key = context.getString(R.string.autolock_key);
        edit.putBoolean(autolock_key, value);
        edit.commit();
    }
	

	private static void setAutoLockTime(Context context, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        String autolocktime_key = context.getString(R.string.autolocktime_key);
        edit.putString(autolocktime_key, Long.toString(value));
        edit.commit();
    }
}