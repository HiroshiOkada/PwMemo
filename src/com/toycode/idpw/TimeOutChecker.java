package com.toycode.idpw;

import android.os.AsyncTask;

import java.util.Observable;

public class TimeOutChecker extends Observable {

    private static TimeOutChecker sInstance;
    
    private long mT0;
    private long mTimeOutMsec = 10*60*1000;
    
    public static TimeOutChecker getInstance() {
        if (sInstance == null) {
            sInstance = new TimeOutChecker();
        }
        return sInstance;
    }
    
    private TimeOutChecker() {
        super();
        mT0 = System.currentTimeMillis();
        (new TimeOutCheckTask()).execute();
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
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                boolean isTimeOut = TimeOutChecker.this.isTimeOut();
                 if (isTimeOutSave != isTimeOut) {
                     isTimeOutSave = isTimeOut;
                     publishProgress(new Void[0]);
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
