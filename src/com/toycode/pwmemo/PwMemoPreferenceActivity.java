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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PwMemoPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pwmemopreference);
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
