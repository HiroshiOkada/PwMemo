
package com.toycode.idpw;

public final class Const {
    public static final class DB {
        static final String NAME = "idpw.db";
        static final int VERSION = 5;
    }

    static final class TABLE {
        static final String IDPW = "idpw";
    }

    static final class COLUMN {
        // IDPW
        static final String ID = "_id";
        static final String TITLE = "title";
        static final String CRIPTDATA = "cryptdata";
    }

    static final class REQUEST_TYPE {
        static final String NAME = "request_type";
        static final int EDIT = 1;
        static final int NEW = 2;
        static final int READ = 3;
    }

    public static final int MIN_PASSWORD_LEN = 4;
}
