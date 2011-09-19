package com.toycode.pwmemo;

import java.security.SecureRandom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Master/Main password manager
 * Master Password: User input password.
 * Main Passowrd: Password that use for date encrypt.
 *   Created from secure random number at the first time.
 */
public class PasswordManager {

	static final String PREF_KEY = "MAIN_CRYPTED";
	static final int LENGTH = 16;

	static private PasswordManager sInstance;
	private SharedPreferences mPreferences;

	private byte[] mMainPasswordCrypted = null;
	private byte[] mMainPasswordDecrypted = null;

	/**
	 * Get a unique instance.
	 * 
	 * @param context
	 * @return PasswordManager' instance
	 */
	public static PasswordManager getInstance(Context context) {
		if ((sInstance == null) && (context != null)) {
			sInstance = new PasswordManager(context.getApplicationContext());
		}
		return sInstance;
	}

	/**
	 * Check if there is a main password.
     *
	 * @return
	 */
	public boolean isMainPasswordExist() {
		return mMainPasswordCrypted != null;
	}

	/**
     * Check the main password has decrypted.
	 * 
	 */
	public boolean isMainPasswordDecrypted() {
		return mMainPasswordDecrypted != null;
	}

	/**
	 * Dreccrypt Main Password
     * When it cannot decrypt  the main password, returns null.
	 * 
	 * @param Main Password
	 */
	public byte[] decryptMainPassword(String masterPassword) {
		byte[] decrypted = null;
        try {
            decrypted = OpenSSLAES128CBCCrypt.INSTANCE.decrypt(
            		masterPassword.getBytes(), mMainPasswordCrypted);
        } catch (CryptException e) {
            App.debugLog(this, "CryptException:" + e.GetMsgId());
        }
		if (decrypted == null) {
			return null;
		}
		mMainPasswordDecrypted = decrypted;
		return decrypted;
	}

	/**
	 * If Main Password is decrypted return it.
     * Otherwise return null.
	 * 
	 * @param masterPassword
	 */
	public byte[] getDecryptedMainPassword() {
		return mMainPasswordDecrypted;
	}

	/**
	 * Set Main Password undecrypt.
     *
	 */
	public void unDecrypt() {
		mMainPasswordDecrypted = null;
	}

	/**
	 * Make Main Password
	 * 
	 * @param masterPassword
	 */
	public byte[] createMainPassword(String masterPassword) {
		mMainPasswordDecrypted = new byte[LENGTH];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(mMainPasswordDecrypted);
		mMainPasswordCrypted = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(
				masterPassword.getBytes(), mMainPasswordDecrypted);

		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_KEY, BytesUtil.toHex(mMainPasswordCrypted));
		editor.commit();
		return mMainPasswordDecrypted;
	}

	/**
	 * Dispose instance (only for test purpose.)
	 */
	public static void deleteInstance() {
		sInstance = null;
	}

	/**
     * Change Master Password.
     * The original master password must be decrypted.
	 * 
	 * @return True if password change success.
	 */
	public boolean changeMasterPassword(String newMasterPassword) {
		// not decrypted.
		if (mMainPasswordDecrypted == null) {
			return false;
		}

		// crypt with new password.
		mMainPasswordCrypted = OpenSSLAES128CBCCrypt.INSTANCE.encrypt(
				newMasterPassword.getBytes(), mMainPasswordDecrypted);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_KEY, BytesUtil.toHex(mMainPasswordCrypted));
		return editor.commit();
	}

	/**
     * Remove the master password from shared preferences and memory.
	 */
	public void deleteMasterPassword() {
		mMainPasswordDecrypted = null;
		mMainPasswordCrypted = null;
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.remove(PREF_KEY);
		editor.commit();
	}

	/**
	 * Disable the default constructor.
	 */
	private PasswordManager() {
	};

	/**
     * Constructor, The main password gets from preferences.
	 * 
	 * @param context
	 */
	private PasswordManager(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (mPreferences.contains(PREF_KEY)) {
			try {
				mMainPasswordCrypted = BytesUtil.fromHex(mPreferences
						.getString(PREF_KEY, ""));
			} catch (NumberFormatException e) {
				mMainPasswordCrypted = null;
			}
		}
		mMainPasswordDecrypted = null;
	};

}
