package com.toycode.idpw;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class Toy {
	
	/**
	 * Debug ログを記録
	 * @param obj 対象オブジェクト
	 * @param msg メッセージ
	 */
	static public final void debugLog( Object obj, String msg) {
		Log.d( obj.getClass().getName(), msg);
	}

	/**
	 * メッセージをトーストにして表示
	 * @param context
	 * @param message_id 表示するメッセージID
	 */
	static public final void toastMessage( Context context, int message_id) {
		toastMessage( context, context.getString(message_id));
    }
	
	/**
	 * メッセージをトーストにして表示
	 * @param context
	 * @param message 表示するメッセージ
	 */
	static public final void toastMessage( Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();   
	}

}
