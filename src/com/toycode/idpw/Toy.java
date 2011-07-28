
package com.toycode.idpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public final class Toy {

    static final String PREFERENCE_NAME = "PREF";
    
    /**
     * Debug ログを記録
     * 
     * @param obj 対象オブジェクト
     * @param msg メッセージ
     */
    static public final void debugLog(Object obj, String msg) {
        Log.d(obj.getClass().getName(), msg);
    }

    /**
     * メッセージをトーストにして表示
     * 
     * @param context
     * @param message_id 表示するメッセージID
     */
    static public final void toastMessage(Context context, int message_id) {
        toastMessage(context, context.getString(message_id));
    }

    /**
     * メッセージをトーストにして表示
     * 
     * @param context
     * @param message 表示するメッセージ
     */
    static public final void toastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * トースト表示
     * @param context
     * @param message_id
     * @param args
     */
    static public final void toastMessage(Context context, int message_id, Object... args) {
        String messageString = String.format(context.getString(message_id), args);
        toastMessage(context, messageString);
    }

    /**
     * TextView が空かどうか調べる
     * view それ自体が空でも、text が空でも "" がセットされていても true を返す
     */
    static public boolean isEmptyTextView(TextView tv) {
        if (tv == null) {
            return true;
        }
        CharSequence text = tv.getText();
        return (text == null) || (text.length() == 0);
    }
    
    /**
     * 文字列が空かどうか調べる
     * オブジェクト自体がnull であっても true を返す
     * @param cs
     * @return
     */
    static public boolean isEmptyCharSequence(CharSequence cs) {
        return (cs == null) || (cs.length() == 0);
    }
    
   
    /**
     * Get one application local String preference.
     */
    static public String getLocalStringPreference(Context context, String key, String defalutValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defalutValue);
    }
    
    /**
     * Get one application local boolean preference.
     */
   static public boolean getLocalBooleanPreference(Context context, String key, boolean defalutValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defalutValue);
    }
   
   /**
    * Get one application local long preference.
    */
   static public long geLocaLongPreference(Context context, String key, long defalutValue) {
       SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
       return prefs.getLong(key, defalutValue);
   }
   
   /**
    * Set one application local String preference.
    */
   static public void setLocalPreference(Context context, String key, String value) {
       SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString(key, value);
       editor.commit();
   }
   
   /**
    * Set one application local long preference.
    */
   static public void setLocalPreference(Context context, String key, long value) {
       SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putLong(key, value);
       editor.commit();
   }

   /**
    * Set one application local boolean preference.
    */
   static public void setLocalPreference(Context context, String key, boolean value) {
       SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putBoolean(key, value);
       editor.commit();
   }
}
