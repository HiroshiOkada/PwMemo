package com.toycode.idpw.test;

import java.security.SecureRandom;

import com.toycode.idpw.OpenSSLAES128CBCCrypt;

import junit.framework.TestCase;

public class OpenSSLAES128CBCCryptTest extends TestCase {

	OpenSSLAES128CBCCrypt mInstance;
	
	/**
	 * 1 byte だけ暗号化
	 */
	public void testOneByte() {
		byte [] data = new byte [1];
		data[0] = 1;
		byte [] password = new byte[1];
		password[0] = 1;
		byte [] cipher = mInstance.encrypt(password, data);
		byte [] data2 = mInstance.decrypt(password, cipher);
		assertEquals(data.length,data2.length);
		assertEquals(data[0], data2[0]);
	}

	/**
	 * 0 byte のデータでエラーが起こらないことを確認
	 */
	public void testZeroByte() {
		byte [] data = new byte [0];
		byte [] password = new byte[0];
		byte [] cipher = mInstance.encrypt(password, data);
		byte [] data2 = mInstance.decrypt(password, cipher);
		assertEquals(data.length,data2.length);
	}
	

	/**
	 * 普通に暗号、複号して OK なことを確かめる
	 */
	public void testEncryptDecrypt() {
		final int SIZE = 3200;
		SecureRandom rand = new SecureRandom();
		byte [] data = new byte [SIZE];
		rand.nextBytes(data);
		byte [] password = new byte[8];
		rand.nextBytes(password);

		byte [] cipher = mInstance.encrypt(password, data);
		byte [] data2 = mInstance.decrypt(password, cipher);
		assertEquals(data.length,data2.length);
		for( int i=0; i<SIZE; i++){
			assertEquals(data[i],data2[i]);
		}		
	}
	
	/**
	 * 正しくないパスワードでは複号時に null が返る
	 */
	public void testDecryptFail() {
		final int SIZE = 3200;
		SecureRandom rand = new SecureRandom();
		byte [] data = new byte [SIZE];
		rand.nextBytes(data);
		byte [] password = new byte[8];
		rand.nextBytes(password);

		byte [] cipher = mInstance.encrypt(password, data);
		rand.nextBytes(password);
		byte [] data2 = mInstance.decrypt(password, cipher);
		assertNull(data2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		mInstance = OpenSSLAES128CBCCrypt.INSTANCE;
	}

}
