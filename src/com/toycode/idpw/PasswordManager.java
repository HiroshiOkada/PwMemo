package com.toycode.idpw;

import java.security.SecureRandom;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * マスター/メインパスワードマネージャー マスターパスワードとは、ユーザーが入力したパスワード
 * メインパスワードは、起動時に作成され、データを暗号化するのに使用されるパスワード
 * メインパスワードはマスターパスワードで暗号化されてプリファレンスに格納されている
 */
public class PasswordManager {

	static final String PREF_NAME = "PREF";
	static final String PREF_KEY = "MAIN_CRYPTED";
	static final int LENGTH = 16;

	static private PasswordManager sInstance;
	private SharedPreferences mPreferences;

	private byte[] mMainPasswordCrypted = null;
	private byte[] mMainPasswordDecrypted = null;

	public static PasswordManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new PasswordManager(context);
		}
		return sInstance;
	}

	/**
	 * メインパスワードが存在するか調べる
	 * 
	 * @return
	 */
	public boolean isMainPasswordExist() {
		return mMainPasswordCrypted != null;
	}

	/**
	 * メインパスワードが解読済みか調べる
	 */
	public boolean isMainPasswordDecrypted() {
		return mMainPasswordDecrypted != null;
	}

	/**
	 * メインパスワードを解読する
	 * 
	 * @param masterPassword
	 */
	public byte[] decryptMainPassword(String masterPassword) {
		byte[] decrypted = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(
				masterPassword.getBytes(), mMainPasswordCrypted);
		if (decrypted == null) {
			return null;
		}
		mMainPasswordDecrypted = decrypted;
		return decrypted;
	}

	/**
	 * メインパスワードが解読済みの時、それを得る
	 * 
	 * @param masterPassword
	 */
	public byte[] getDecryptedMainPassword() {
		return mMainPasswordDecrypted;
	}

	/**
	 * メインパスワードの解読を無効にする
	 */
	public void unDecrypt() {
		mMainPasswordDecrypted = null;
	}

	/**
	 * メインパスワードを作成する
	 * 
	 * @param masterPassword
	 */
	public void createMainPassword(String masterPassword) {
		mMainPasswordDecrypted = new byte[LENGTH];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(mMainPasswordDecrypted);
		mMainPasswordCrypted = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(
				masterPassword.getBytes(), mMainPasswordDecrypted);

		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_KEY, BytesUtil.toHex(mMainPasswordCrypted));
		editor.commit();
	}

	/**
	 * 引数無しのコンストラクタを呼べないように private として宣言しておく
	 */
	private PasswordManager() {
	};

	/**
	 * 実際に使われるコンストラクタ、プリファレンスからメインパスワード(暗号化済み)を取得する
	 * 
	 * @param context
	 */
	private PasswordManager(Context context) {
		mPreferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		if (mPreferences.contains(PREF_KEY)) {
			try {
				mMainPasswordCrypted = BytesUtil.fromHex(mPreferences
						.getString(PREF_KEY, ""));
			} catch (NumberFormatException e) {
				mMainPasswordCrypted = null;
			}
		}
	};
}