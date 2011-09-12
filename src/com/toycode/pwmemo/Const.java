
package com.toycode.pwmemo;

public final class Const {
    public static final class DB {
        public static final String NAME = "pwmemo.db";
        public static final int VERSION = 6;
    }

    public static final class TABLE {
        public static final String PWMEMO = "pwmemo";
    }

    public static final class COLUMN {
        // PwMemo
        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String CRIPTDATA = "cryptdata";
        public static final String TEMPORARY_FLAGS = "temporaryflags";
    }

    public static final class REQUEST_TYPE {
        public static final String NAME = "request_type";
        public static final int EDIT = 1;
        public static final int NEW = 2;
        public static final int READ = 3;
    }

    public static final int MIN_PASSWORD_LEN = 4;
}
