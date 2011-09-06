package com.toycode.pwmemo.test;

import java.security.SecureRandom;

import com.toycode.pwmemo.CryptException;
import com.toycode.pwmemo.OpenSSLAES128CBCCrypt;

import junit.framework.TestCase;

public class OpenSSLAES128CBCCryptTest extends TestCase {

	OpenSSLAES128CBCCrypt mInstance;
	
	/**
	 * One byte crypt
	 */
	public void testOneByte() {
		byte [] data = new byte [1];
		data[0] = 1;
		byte [] password = new byte[1];
		password[0] = 1;
		byte [] cipher = mInstance.encrypt(password, data);
		byte[] data2;
        try {
            data2 = mInstance.decrypt(password, cipher);
            assertEquals(data.length,data2.length);
            assertEquals(data[0], data2[0]);
        } catch (CryptException e) {
            fail();
        }
	}

	/**
	 * No error on 0 byte data.
	 */
	public void testZeroByte() {
		byte [] data = new byte [0];
		byte [] password = new byte[0];
		byte [] cipher = mInstance.encrypt(password, data);
		byte[] data2;
        try {
            data2 = mInstance.decrypt(password, cipher);
            assertEquals(data.length,data2.length);
        } catch (CryptException e) {
            fail();
        }
	}
	

	/**
	 * Encrypt, Decrypt check.
	 */
	public void testEncryptDecrypt() {
		final int SIZE = 3200;
		SecureRandom rand = new SecureRandom();
		byte [] data = new byte [SIZE];
		rand.nextBytes(data);
		byte [] password = new byte[8];
		rand.nextBytes(password);

		byte [] cipher = mInstance.encrypt(password, data);
		byte[] data2;
        try {
            data2 = mInstance.decrypt(password, cipher);
            assertEquals(data.length,data2.length);
            for( int i=0; i<SIZE; i++){
                assertEquals(data[i],data2[i]);
            }
        } catch (CryptException e) {
            fail();
        }
	}
	
	/**
	 * Wrong password return zero
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
		byte[] data2;
        try {
            data2 = mInstance.decrypt(password, cipher);
            assertNull(data2);
        } catch (CryptException e) {
            fail();
        }
	}

	/**
	 * Decode a data.
	 */
	public void testDecodeSample(){
		byte[] cipher = { 83, 97, 108, 116, 101, 100, 95, 95, 59, -107,
				-52, 19, 7, -27, -41, 7, -106, 53, -86, 3, 11, -117, -6, -31,
				-69, 49, -27, -13, -16, 7, -70, 28, -99, -80, -92, -106, 39,
				104, 102, -18, 21, -15, -72, -52, 58, 52, -20, -29, -83, 83,
				121, 73, -58, 8, 24, -26, -106, 62, 58, -113, 114, -70, 47, 36,
				-86, 47, -24, -106, -20, 8, -26, 119, 98, -81, -5, -113, -92,
				-118, -65, -37, 14, 57, -81, -70, -71, 20, 75, -63, 50, 6, -90,
				-107, -74, 87, -29, 20, 71, -39, -80, -45, -93, -93, 99, -32,
				19, 84, -77, 105, -45, -126, 56, 73, -120, -76, -30, -9, 11,
				100, -101, 32, 12, -64, -112, 52, 87, 84, 87, 10, 5, -87, 51,
				-31, -84, 32, -82, -63, -54, 114, 92, -108, -42, 18, 102, 45,
				50, -3, -108, -36, 109, 94, 38, 104, 109, 83, -27, 56, -2, 57,
				102, 65, 38, -125, -91, -88, 20, 94, -125, -94, -68, -21, 5,
				-69, 38, 51, 87, -100, 83, 102, 125, 42, 110, -73, -61, -89,
				-50, -4, 31, 47, -33, 81, -104, 63, 30, 64, -43, -30, 44, -23,
				-60, 63, -79, 89, -101, 86, -105, -100, -115, 73, -93, 13, -79,
				-55, 18, -21, 10, -28, 104, 64, 34, -5, 35, 119, 1, 53, -92,
				-19, 89, -79, -58, -66, 79, 98, 101, 110, -11, 50, 114, -42,
				-83, -15, 89, 3, -78, -11, -109, -56, 99, -101, -49, 76, 12,
				41, 52, 17, 75, 86, -125, -50, -38, 28, -105, -52, 108, 44,
				-84, 61, 37, 11, 17, 59, 113, -103, -66, 101, -103, 86, -3,
				-81, 17, -118, -74, -39, -9, 75, 56, -15, 12, -105 };
		byte [] password = { 97, 98, 99, 100, 101, 102, 103, 104};
		byte[] data;
        try {
            data = mInstance.decrypt(password, cipher);
            assertNotNull(data);
            assertEquals(256, data.length);
            for( int i=0; i<256; i++){
                assertEquals( (byte)i, data[i]);
            }
        } catch (CryptException e) {
            fail();
        }
	}
	
	/**
	 * Bad data throw exception
	 */
    public void testDecryptExcepion(){
        byte[] cipher = { 1, 2, 3, 4, 5, 6, 7};
        byte [] password = { 97, 98, 99, 100, 101, 102, 103, 104};
        
        try {
            mInstance.decrypt(password, cipher);
            fail();
        } catch (CryptException e) {
            assertTrue( e instanceof CryptException);
        }
    
    }
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		mInstance = OpenSSLAES128CBCCrypt.INSTANCE;
	}

}
