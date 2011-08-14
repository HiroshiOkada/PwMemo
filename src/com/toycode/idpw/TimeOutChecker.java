package com.toycode.idpw;

import android.os.AsyncTask;

import java.util.Observable;

public class TimeOutChecker extends Observable {

    private static TimeOutChecker sInstance = null;
    
    private long mT0;
    private long mTimeOutMsec = 3*60*1000;
    private TimeOutCheckTask mTimeOutCheckTask;
    
    
    public static TimeOutChecker getInstance() {
        if (sInstance == null) {
            sInstance = new TimeOutChecker();
        }
        return sInstance;
    }
    
    public static void CleanUp() {
        if (sInstance != null) {
            if (sInstance.mTimeOutCheckTask != null) {
                sInstance.mTimeOutCheckTask.cancel(true);
            }
            sInstance = null;
        }
    }
    
    private TimeOutChecker() {
        super();
        mT0 = System.currentTimeMillis();
        mTimeOutCheckTask = new TimeOutCheckTask();
        mTimeOutCheckTask.execute();
    }
    
    public void onUser() {
        synchronized (this) {
            mT0 = System.currentTimeMillis();
        }
    }

    public boolean isTimeOut() {
        synchronized (this) {
            if ((System.currentTimeMillis() - mT0) > this.mTimeOutMsec) {
                return true;
            }            
        }       
        return false;
    }
    
    
    private class TimeOutCheckTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            boolean isTimeOutSave = false;
            while (!isCancelled()) {
                boolean isTimeOut = TimeOutChecker.this.isTimeOut();
                if (isTimeOutSave != isTimeOut) {
                    isTimeOutSave = isTimeOut;
                    publishProgress();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            TimeOutChecker.this.setChanged();
            TimeOutChecker.this.notifyObservers();
        }
    }    
}
