package com.toycode.idpw.test;

import java.lang.reflect.Array;
import java.math.BigInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import com.toycode.idpw.PasswordManager;

public class PasswordManagerTest extends AndroidTestCase {
	static final String PREF_NAME = "PREF";
	Context mContext;
	PasswordManager mPasswordManager;
	
	/**
	 * メインパスワード存在確認関数テスト
	 */
	public void testIsMainPasswordExist() {
		// 最初ははメインパスワードが存在しない
		assertFalse(mPasswordManager.isMainPasswordExist());
		// パスワードを設定すると存在する
		mPasswordManager.createMainPassword("abcdefg");
		assertTrue(mPasswordManager.isMainPasswordExist());
		// パスワードマネージャーを再作成しても、パスワードは存在する
		PasswordManager.deleteInstance();
		mPasswordManager = PasswordManager.getInstance(mContext);
		assertTrue(mPasswordManager.isMainPasswordExist());
	}

	/**
	 * メインパスワードが解読済みか調べる関数のテスト
	 */
	public void testIsMainPasswordDecrypted() {
		// 最初はメインパスワードが存在しないので解読済みではない
		assertFalse(mPasswordManager.isMainPasswordDecrypted());
		// メインパスワードを作成すると解読済みになる
		mPasswordManager.createMainPassword("hoge");
		assertTrue( mPasswordManager.isMainPasswordDecrypted());
		// パスワードマネージャーを再作成すると、解読済みでなくなる
		PasswordManager.deleteInstance();
		mPasswordManager = PasswordManager.getInstance(mContext);
		assertFalse(mPasswordManager.isMainPasswordDecrypted());
	}

	/**
	 * メインパスワードを解読テスト
	 * 
	 * @param masterPassword
	 */
	public void testDecryptMainPassword() {
		// メインパスワードを作成する
		byte [] mainPassword = mPasswordManager.createMainPassword("hoge");
		assertNotNull(mainPassword);
		// 間違ったパスワードでは取得できない
		byte [] resultPassword = mPasswordManager.decryptMainPassword("huga");
		assertNull( resultPassword);
		// 正しいパスワードで取得できる
		resultPassword = mPasswordManager.decryptMainPassword("hoge");
		assertTrue( java.util.Arrays.equals(mainPassword, resultPassword));
	}
	
	/**
	 * テストの度にコンテクストを取得し
	 * シェアードプリファレンスとPasswordManeger印タンスを一旦廃棄
	 * してから PasswordManager のインスタンスを得る
	 */
	@Override
	protected void setUp() throws Exception {
		mContext = getContext();
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
		PasswordManager.deleteInstance();		
		mPasswordManager = PasswordManager.getInstance(mContext);
	}
	
	/**
	 * テストが終わる度にシェアードプリファレンスとPasswordManegerを廃棄
	 */
	@Override
	protected void tearDown() throws Exception {
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
		PasswordManager.deleteInstance();		
	}
}
