package com.toycode.idpw;

import android.util.Log;

public final class Toy {
	public final static void log( Object obj, String msg) {
		Log.d( obj.getClass().getName(), msg);
	}
}
