
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
 * OpenSSL の -aes-128-cbc とコンパチブルな暗号、複合
 * 
 * @author hiroshi
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

    /**
     * コンストラクタ
     */
    private OpenSSLAES128CBCCrypt() {
        try {
            mChipher = Cipher.getInstance(TRANSFORMATION);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * パスワードを使ってデータを暗号化する
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
     * パスワードを使って暗号データを複号する
     * 
     * @param password
     * @param cipher
     * @return デコードされたデータ, パスワードが間違っていた場合は null を返す
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
            // padding データが正しい形式に復元できないのはパスワードが間違っているため
            return null;
        }
    }

    /**
     * セキュリティ用乱数を使って salt を作成する
     * 
     * @return 新しく作られた salt
     */
    private byte[] newSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(salt);
        return salt;
    }

    /**
     * OpenSSl の EVP_BytesToKey(3) で count が 1 の場合の key と iv を求める
     * 
     * @param salt
     * @param data
     * @param keyIVlength (key と iv をつなげた永さ)
     * @return (key と iv をつなげた配列 終わりに余計なデータがついている場合もある)
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
