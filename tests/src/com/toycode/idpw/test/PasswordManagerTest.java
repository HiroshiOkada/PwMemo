package com.toycode.idpw.test;

import com.toycode.idpw.PasswordManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import java.lang.reflect.Field;

public class PasswordManagerTest extends AndroidTestCase {
	static final String PREF_NAME = "PREF";
	Context mContext;
	
	
	/**
	 * メインパスワード存在確認関数テスト
	 */
	public void testIsMainPasswordExist() {
		PasswordManager pm = PasswordManager.getInstance(mContext);
		// 作成直後はメインパスワードが存在しない
		AndroidTestCase.assertFalse(pm.isMainPasswordExist());
		// パスワードを設定すると存在する
		pm.createMainPassword("abcdefg");
		AndroidTestCase.assertTrue(pm.isMainPasswordExist());
	}

	/**
	 * テストの度にコンテクストを取得し
	 * シェアードプリファレンスを削除
	 */
	@Override
	protected void setUp() throws Exception {
		mContext = getContext();
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
		
	}
	
	/**
	 * テストが終わる度にシェアードプリファレンスとPasswordManegerを廃棄
	 */
	@Override
	protected void tearDown() throws Exception {
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
		deletePasswordManager();
	}

	/**
	 * PasswordManeger を廃棄する
	 */
	private void deletePasswordManager(){
		try {
			Class cls = Class.forName("com.toycode.idpw.PasswordManager");
			Field f = cls.getField("sInstance");
			f.set(null, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
