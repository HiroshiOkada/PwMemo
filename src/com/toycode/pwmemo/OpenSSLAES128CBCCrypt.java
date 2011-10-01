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

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * The cipher that compatible with OpenSSL "aes-128-cbc"
 * 
 * @author Hiroshi Okada
 */
public class OpenSSLAES128CBCCrypt {

    public static OpenSSLAES128CBCCrypt INSTANCE = new OpenSSLAES128CBCCrypt();

    public static final int BLOCK_LENGTH = 16;
    static final byte[] SALTED = {
            0x53, 0x61, 0x6c, 0x74, 0x65, 0x64, 0x5f, 0x5f
    };
    static final String ALGORITHM = "AES";
    static final String MODE = "CBC";
    static final String PADDING = "PKCS5Padding";
    static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
    static final int SALT_LENGTH = 8;
    static final int KEY_LENGTH = 16;

    Cipher mChipher = null;

    private OpenSSLAES128CBCCrypt() {
        try {
            mChipher = Cipher.getInstance(TRANSFORMATION);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Crypt with password.
     * 
     * @param password
     * @param data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] password, byte[] data) {
        try {
            byte[] salt = newSalt();

            byte[] keyIV = makeKeyIV(salt, password, KEY_LENGTH + BLOCK_LENGTH);
            byte[] iv = new byte[BLOCK_LENGTH];
            SecretKeySpec key = new SecretKeySpec(keyIV, 0, KEY_LENGTH,
                    ALGORITHM);
            System.arraycopy(keyIV, KEY_LENGTH, iv, 0, BLOCK_LENGTH);

            mChipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] cipherd = mChipher.doFinal(data);
            byte[] header = BytesUtil.concat(SALTED, salt);

            return BytesUtil.concat(header, cipherd);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * DeCrypt with password.
     * 
     * @param password
     * @param cipher
     * @return decoded result is it can't decode return null.
     */
    public byte[] decrypt(byte[] password, byte[] cipher) throws CryptException {
        try {
            if (!BytesUtil.compare(SALTED, 0, cipher, 0, SALTED.length)) {
                throw new CryptException(R.string.not_a_crypt_data);
            }
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(cipher, SALTED.length, salt, 0, SALTED.length);
            byte[] keyIV = makeKeyIV(salt, password, KEY_LENGTH + BLOCK_LENGTH);
            byte[] iv = new byte[BLOCK_LENGTH];
            SecretKeySpec key = new SecretKeySpec(keyIV, 0, KEY_LENGTH,
                    ALGORITHM);
            System.arraycopy(keyIV, KEY_LENGTH, iv, 0, BLOCK_LENGTH);

            mChipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] data;
            data = mChipher.doFinal(cipher, SALTED.length + SALT_LENGTH,
                    cipher.length - (SALTED.length + SALT_LENGTH));
            return data;
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (BadPaddingException e) {
            // can't decode
            return null;
        }
    }

    /**
     * Make salt
     * 
     * @return new salt
     */
    private byte[] newSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(salt);
        return salt;
    }

    /**
     * Get key and iv.
     * see: OpenSSl EVP_BytesToKey(3) count=1 
     * 
     * @param salt
     * @param data
     * @param keyIVlength (key + iv length)
     * @return (key + iv Array sometime have excessive bytes)
     */
    private byte[] makeKeyIV(byte[] salt, byte[] data, int keyIVlength) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] data_salt = BytesUtil.concat(data, salt);
            byte[] md5sum = md5.digest(data_salt);
            byte[] key_iv = md5sum.clone();
            while (key_iv.length < keyIVlength) {
                md5sum = md5.digest(BytesUtil.concat(md5sum, data_salt));
                key_iv = BytesUtil.concat(key_iv, md5sum);
            }
            return key_iv;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

