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
	public static final int MIN_PASSWORD_LEN = 3;
}
