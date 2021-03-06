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

/**
 * Global application object.
 * define common utility functions.
 * 
 * @author Hiroshi Okada
 * 
 */
public final class App extends Application {
    
    /**
     * Debug flag
     */
    static final public boolean DEBUG_FLAG = false;  
    
    /**
     * Debug log header string
     */
    static final public String DEBUG_HEADER = "pwmemo";

    /**
     * Default file name key. (in preference)
     */
    private static final String DEFAULT_FILENAME_KEY = "DEFAULT_FILENAME";
    
    /**
     * Default file name
     */
    public static final String DEFALUT_FILENAME = "data.pwmemo";    
    
    /**
     * Check these directories under SD card when read/write.
     */
    private static final String [] CANDIDATE_SUBFOLDER_NAMES = {
        "pwmemo",
        "data",
        "work",
        "tmp"
    };
    
    /**
     * Get the shared App object with this application. 
     */
    public static App GetApp(Context context) {
        return (App)context.getApplicationContext();
    }
   
    /**
     * Log for debug.
     * 
     * @param msg message
     */
    static public final void debugLog(String msg) {
        if (DEBUG_FLAG) {
            Log.d(DEBUG_HEADER, msg);
        }
    }

    /**
     * Log for debug. (object name is reported)
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
     * Raise the toast message.
     * 
     * @param message_id message id to display.
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
     * Raise the toast message.
     * 
     * @param message message string to display.
     */
    public final void toastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Raise the toast message with arguments.
     * 
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
     * Investigate if TextView is empty.
     * If targets are null, Regard as empty.
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
     * Investigate if CharSequence is empty.
     * If target is null, Regard as empty.
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
     * Return folders that become candidates as output destinations.
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
    
    /**
     * Get default output file.
     * if default output file already set in shared preference return it.
     * if a candidate folder exist then return "first candidate folder" + DEFALUT_FILENAME.
     * otherwise return "first candidate folder" + DEFALUT_FILENAME
     * 
     * @return output file 
     */
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
    
    /**
     * Get Default Input Filename.
     * @return
     */
    public File getDefaultInputFile() {
        // If a default file name exists in the preference 
        // and it is readable, returns it.
        String defaultFileName = getLocalStringPreference(DEFAULT_FILENAME_KEY, "");
        if (!defaultFileName.equals("")) {
            File file = new File( defaultFileName);
            if (file.canRead()) {
                return file;
            }
        }
        //If there is DEFAULT FILENAME and readable in a candidate folder,return it.
        List<File> candidateFolders = getCandidateFolders();
        for (File folder : candidateFolders) {
            File file = new File( folder, DEFALUT_FILENAME);
            if (file.exists() && file.canWrite()) {
                return file;
            }
        }
        // return predefined default filename even if it can't read.
        return  new File(candidateFolders.get(0), DEFALUT_FILENAME);
    }

    /**
     * Set default output file to preference.
     * 
     * @param file to set.
     */
    public void setDefaultOutputFile(File file) {
        setLocalPreference(DEFAULT_FILENAME_KEY, file.toString());
    }
}
