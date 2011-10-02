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

/**
 * Global use constants.
 * 
 * @author Hiroshi Okada
 */
public final class Const {

    /**
     * Database filename and version
     */
    public static final class DB {
        public static final String NAME = "pwmemo.db";
        public static final int VERSION = 6;
    }

    /**
     * Database table name
     */
    public static final class TABLE {
        public static final String PWMEMO = "pwmemo";
    }

    /**
     * Database column names
     */
    public static final class COLUMN {
        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String CRIPTDATA = "cryptdata";
        public static final String TEMPORARY_FLAGS = "temporaryflags";
    }

    /**
     * REQUEST TYPE for startActivity
     */
    public static final class REQUEST_TYPE {
        public static final String NAME = "request_type";
        public static final int EDIT = 1;
        public static final int NEW = 2;
        public static final int READ = 3;
    }

    /**
     * Minimum password length
     */
    public static final int MIN_PASSWORD_LEN = 4;
}
