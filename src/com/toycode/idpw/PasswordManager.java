package com.toycode.idpw;

import java.security.SecureRandom;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * マスター/メインパスワードマネージャー
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
	 * @return
	 */
	public boolean isMainPasswordExist() {
		Log.d("PasswordManager.isMainPasswordExist()", "<=" + Boolean.toString(mMainPasswordCrypted != null));
		return mMainPasswordCrypted != null;
	}
	
	/**
	 * メインパスワードが解読済みか調べる
	 */
	public boolean isMainPasswordDecrypted() {
		Log.d("PasswordManager.isMainPasswordDecrypted()", "<=" + Boolean.toString(mMainPasswordDecrypted != null));
		return mMainPasswordDecrypted != null;
	}
	
	/**
	 * メインパスワードを解読する
	 * @param masterPassword
	 */
	public byte [] decryptMainPassword(String masterPassword){
		Log.d("PasswordManager.decryptMainPassword(" + masterPassword +")", "begin");

		byte[] decrypted = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(masterPassword
				.getBytes(), mMainPasswordCrypted);
		if (decrypted == null) {
			Log.d("PasswordManager.decryptMainPassword(" + masterPassword +")", "end <= null");
			return null;
		}
		mMainPasswordDecrypted = decrypted;
		Log.d("PasswordManager.decryptMainPassword(" + masterPassword +")", "end <="
				+ BytesUtil.toHex(decrypted));
		return decrypted;
	}
	
	/**
	 * メインパスワードが解読済みの時、それを得る
	 * @param masterPassword
	 */
	public byte [] getDecryptedMainPassword(){
		Log.d("PasswordManager.getDecryptedMainPassword()", "<=" + BytesUtil.toHex(mMainPasswordDecrypted));
		return mMainPasswordDecrypted;
	}
	
	/**
	 * メインパスワードの解読を無効にする
	 */
	public void unDecrypt(){
		mMainPasswordDecrypted = null;
	}
	
	/**
	 * メインパスワードを作成する
	 * @param masterPassword
	 */
	public void createMainPassword(String masterPassword) {
		Log.d("PasswordManager.createMainPassword(" + masterPassword +")", "begin");
		mMainPasswordDecrypted = new byte[LENGTH];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(mMainPasswordDecrypted);
		mMainPasswordCrypted = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(
				masterPassword.getBytes(), mMainPasswordDecrypted);

		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_KEY, BytesUtil.toHex(mMainPasswordCrypted));
		editor.commit();
		Log.d("PasswordManager.createMainPassword(" + masterPassword +")", "mMainPasswordCrypted=" + BytesUtil.toHex(mMainPasswordCrypted));
		Log.d("PasswordManager.createMainPassword(" + masterPassword +")", "end");
	}

	private PasswordManager() {
	};

	private PasswordManager(Context context) {
		Log.d("PasswordManager(Context context)", "begin");
		mPreferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		if (mPreferences.contains(PREF_KEY)) {
			try {
				mMainPasswordCrypted = BytesUtil.fromHex(mPreferences.getString(PREF_KEY, ""));
			}catch (NumberFormatException e){
				mMainPasswordCrypted = null;
			}
		}
		Log.d("PasswordManager(Context context)", "mMainPasswordCrypted=" + BytesUtil.toHex(mMainPasswordCrypted));
		Log.d("PasswordManager(Context context)", "end");
	};

}