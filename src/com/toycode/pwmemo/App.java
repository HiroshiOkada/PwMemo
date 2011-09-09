
package com.toycode.pwmemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class App extends Application {
    
    /**
     * Debug flag
     */
    static final public boolean DEBUG_FLAG = true;  
    static final public String DEBUG_HEADER = "pwmemo";

    /**
     * Default file name
     */
    private static final String DEFAULT_FILENAME_KEY = "DEFAULT_FILENAME";
    public static final String DEFALUT_FILENAME = "data.pwmemo";    
    private static final String [] CANDIDATE_SUBFOLDER_NAMES = {
        "My Document",
        "data",
        "work",
        "tmp"
    };
    
    public static App GetApp(Context context) {
        return (App)context.getApplicationContext();
    }
   
    /**
     * Debug log
     * 
     * @param msg message
     */
    static public final void debugLog(String msg) {
        if (DEBUG_FLAG) {
            Log.d(DEBUG_HEADER, msg);
        }
    }

    /**
     * Debug log (object name is reported)
     * 
     * @param obj 
     * @param msg message
     */
    static public final void debugLog(Object obj, String msg) {
        if (DEBUG_FLAG) {
            String [] fullname = obj.getClass().toString().split("\\.");
            if (fullname.length > 0) {
                Log.d(DEBUG_HEADER, fullname[fullname.length-1] + ":" + msg);
            } else {
                Log.d(DEBUG_HEADER, ":" + msg);
            }
        }
    }
    
    
    /**
     * メッセージをトーストにして表示
     * 
     * @param message_id 表示するメッセージID
     */
    public final void toastMessage(int message_id) {
        try {
            String message = this.getString(message_id);
            toastMessage(message);
        } catch (Exception e) {
            debugLog( "Message not found. message_id=" + Integer.toString(message_id));
        }
    }

    /**
     * メッセージをトーストにして表示
     * 
     * @param message 表示するメッセージ
     */
    public final void toastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * トースト表示
     * @param message_id
     * @param args
     */
    public final void toastMessage(int message_id, Object... args) {
        try {
            String message = String.format(this.getString(message_id), args);
            toastMessage(message);
        } catch (Exception e) {
            debugLog("Can't format message message_id=" + Integer.toString(message_id));
        }
    }

    /**
     * TextView が空かどうか調べる
     * view それ自体が空でも、text が空でも "" がセットされていても true を返す
     */
    static public boolean isEmptyTextView(TextView tv) {
        if (tv == null) {
            debugLog("TextView is Null");
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
    public String getLocalStringPreference(String key, String defalutValue) {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(key, defalutValue);
    }

    /**
     * Get one application local boolean preference.
     */
    public boolean getLocalBooleanPreference(String key, boolean defalutValue) {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, defalutValue);
    }

    /**
     * Get one application local long preference.
     */
    public long geLocaLongPreference(String key, long defalutValue) {
        return PreferenceManager.getDefaultSharedPreferences(this).getLong(key, defalutValue);
    }

    /**
     * Set one application local String preference.
     */
    public void setLocalPreference(String key, String value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Set one application local long preference.
     */
    public void setLocalPreference(String key, long value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * Set one application local boolean preference.
     */
    public void setLocalPreference(String key, boolean value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

   /**
    * copy text to clipboard
    * @param text
    */
    public void copyTextToClipboard(CharSequence text) {
        android.text.ClipboardManager cm = 
            (android.text.ClipboardManager) this
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }
    
    /**
     * Return foldes that become candidates as output destinations.
     * Return DataDirectory If external storage is not found.
     */
    public List<File> getCandidateFolders() {
        List<File> candidates = new LinkedList<File>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File baseFolder = Environment.getExternalStorageDirectory();
            for( String subfolderName : CANDIDATE_SUBFOLDER_NAMES) {
                File folder = new File( baseFolder, subfolderName);
                if (folder.isDirectory()) {
                    candidates.add(folder);
                }
            }
            candidates.add(baseFolder);
        } else {
            candidates.add(Environment.getDataDirectory());
        }
        return candidates;
    }
    
    public  File getDefaultOutputFile() {
        String fileName = getLocalStringPreference(DEFAULT_FILENAME_KEY, "");
        if (fileName.equals("")) {
            List<File> candidateFolders = getCandidateFolders();
            for (File folder : candidateFolders) {
                File file = new File( folder, DEFALUT_FILENAME);
                if (file.exists() && file.canWrite()) {
                    return file;
                }
            }
            return  new File(candidateFolders.get(0), DEFALUT_FILENAME);
        } else {
            return new File(fileName);
        }
    }
    
    public File getDefaultInputFile() {
        String defaultFileName = getLocalStringPreference(DEFAULT_FILENAME_KEY, "");
        if (!defaultFileName.equals("")) {
            File file = new File( defaultFileName);
            if (file.canRead()) {
                return file;
            }
        }
        List<File> candidateFolders = getCandidateFolders();
        for (File folder : candidateFolders) {
            File file = new File( folder, DEFALUT_FILENAME);
            if (file.exists() && file.canWrite()) {
                return file;
            }
        }
        if (!defaultFileName.equals("")) {
            return  new File(candidateFolders.get(0), DEFALUT_FILENAME);
        } else {
            return new File(defaultFileName);
        }
    }

    public void setDefaultOutputFile(File file) {
        setLocalPreference(DEFAULT_FILENAME_KEY, file.toString());
    }

}
