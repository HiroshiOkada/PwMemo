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

import android.os.AsyncTask;

import java.util.Observable;

/**
 * Check timeout.
 * 
 * @author Hiroshi Okada
 */
class TimeOutChecker extends Observable {

    public final static long DEFAULT_TIMEOUT_SEC = 1800;
    private static TimeOutChecker sInstance = null;

    private long mT0;
    private boolean mUseTimeOut;
    private long mTimeOutMsec = DEFAULT_TIMEOUT_SEC * 1000;
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
        mUseTimeOut = true;
        mT0 = System.currentTimeMillis();
        mTimeOutCheckTask = new TimeOutCheckTask();
        mTimeOutCheckTask.execute();
    }

    public void onUser() {
        synchronized (this) {
            mT0 = System.currentTimeMillis();
        }
    }

    public void setUseTimeOut(boolean useTimeOut) {
        mUseTimeOut = useTimeOut;
    }

    public void setTimeOutSec(long sec) {
        synchronized (this) {
            mTimeOutMsec = sec * 1000;
        }
    }

    public boolean isTimeOut() {
        if (!mUseTimeOut) {
            return false;
        }
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
