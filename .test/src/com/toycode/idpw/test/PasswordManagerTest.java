package com.toycode.idpw.test;

import com.toycode.idpw.PasswordManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class PasswordManagerTest extends AndroidTestCase {
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
	 * メインパスワードを取得するテスト(unDecrypt すると取得できないテスト)
	 */
	public void testGetDecryptedMainPassword() {
		// メインパスワードを作成する
		byte [] mainPassword = mPasswordManager.createMainPassword("master1");
		// 作成直後はパスワードを取得できる
		byte [] resultPassword = mPasswordManager.getDecryptedMainPassword();
		assertTrue( java.util.Arrays.equals(mainPassword, resultPassword));
		// unDecrypt() を呼ぶと取得できなくなる
		mPasswordManager.unDecrypt();
		resultPassword = mPasswordManager.getDecryptedMainPassword();
		assertNull( resultPassword);
		// 間違ったパスワードでは取得できない
		resultPassword = mPasswordManager.decryptMainPassword("huga");
		assertNull( resultPassword);
		resultPassword = mPasswordManager.getDecryptedMainPassword();
		assertNull( resultPassword);
		// 正しいパスワードで取得できる
		resultPassword = mPasswordManager.decryptMainPassword("master1");
		assertTrue( java.util.Arrays.equals(mainPassword, resultPassword));
		resultPassword = mPasswordManager.getDecryptedMainPassword();
		assertTrue( java.util.Arrays.equals(mainPassword, resultPassword));
	}

	/**
	 * マスターパスワードを変更するテスト
	 */
	public void testChangeMasterPassword()
	{
	    String firstPassword = "oldmasterpassword";
	    String newPassword = "newmasterpassword";
		// メインパスワードを作成する
		byte [] mainPassword = mPasswordManager.createMainPassword(firstPassword);
		// メインパスワードの解読を無効にする
		mPasswordManager.unDecrypt();
		assertFalse( mPasswordManager.changeMasterPassword(firstPassword));
		assertNull( mPasswordManager.decryptMainPassword(newPassword));
		// マスターパスワードを変更する
		mPasswordManager.decryptMainPassword(firstPassword);
		assertTrue( mPasswordManager.changeMasterPassword(newPassword));
		// 新しいマスターパスワード取得できることを確認
		byte [] resultPassword = mPasswordManager.decryptMainPassword(newPassword);
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
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		pref.edit().clear().commit();
		PasswordManager.deleteInstance();		
		mPasswordManager = PasswordManager.getInstance(mContext);
	}
	
	/**
	 * テストが終わる度にシェアードプリファレンスとPasswordManegerを廃棄
	 */
	@Override
	protected void tearDown() throws Exception {
       SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		pref.edit().clear().commit();
		PasswordManager.deleteInstance();		
	}
}
