package com.toycode.pwmemo;

public class CryptException extends Exception {

    private static final long serialVersionUID = 1L;
    private int mMsgId;

    public CryptException(int msgId) {
        super();
        mMsgId = msgId;
    }
    
    public int GetMsgId() {
        return mMsgId;
    }
}
