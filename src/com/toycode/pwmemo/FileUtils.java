package com.toycode.pwmemo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class FileUtils {

    private static final String DEFAULT_FILENAME_KEY = "DEFAULT_FILENAME";
    public static final String DEFALUT_FILENAME = "data.pwmemo";
    
    private static final String [] CANDIDATE_SUBFOLDER_NAMES = {
        "My Document",
        "data",
        "work",
        "tmp"
    };
    
    /**
     * Return foldes that become candidates as output destinations.
     * Return DataDirectory If external storage is not found.
     */
    public static final List<File> getCandidateFolders() {
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
    
    public static final File getDefaultOutputFile(Context context) {
        String fileName = App.getLocalStringPreference(context, DEFAULT_FILENAME_KEY, "");
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
    
    public static final File getDefaultInputFile(Context context) {
        String defaultFileName = App.getLocalStringPreference(context, DEFAULT_FILENAME_KEY, "");
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

    public static final void setDefaultOutputFile(Context context, File file) {
        App.setLocalPreference(context, DEFAULT_FILENAME_KEY, file.toString());
    }
    
}
