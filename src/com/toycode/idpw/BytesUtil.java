
package com.toycode.idpw;

import java.math.BigInteger;

/**
 * BytesUtil contains static methods for manipulate bytearray
 * @author hiroshi
 */
public class BytesUtil {

    /**
     * Because this is a utility class, it doesn't make any instance.
     */
    private BytesUtil() {
    }

    /**
     * Encode a byte array to a hexadecimal string.
     * 
     * @param dataBytes bytearray
     * @return hexadecimal string.
     */
    public static String toHex(byte[] dataBytes) throws NumberFormatException {
        if (dataBytes == null || dataBytes.length == 0) {
            return "";
        }
        byte[] dataPlus1Bytes = new byte[dataBytes.length + 1];
        System.arraycopy(dataBytes, 0, dataPlus1Bytes, 1, dataBytes.length);
        dataPlus1Bytes[0] = 1;
        BigInteger dataPlus1BigInteger = new BigInteger(dataPlus1Bytes);
        return dataPlus1BigInteger.toString(16).substring(1);
    }

    /**
     * Decode a hexadecimal string to a bytearray.
     * 
     * @param dataString a hexadecimal string (The string length must be even.)
     * @return Decoded bytearray (if dataString == "" then return null.)
     */
    public static byte[] fromHex(String dataString) {
        if (dataString.equals("")) {
            return null;
        }
        BigInteger dataPlus1BigInteger = new BigInteger("10" + dataString, 16);
        byte[] dataPlus1Bytes = dataPlus1BigInteger.toByteArray();
        byte[] dataBytes = new byte[dataPlus1Bytes.length - 1];
        System.arraycopy(dataPlus1Bytes, 1, dataBytes, 0, dataBytes.length);
        return dataBytes;
    }

    /**
     * Join two bytearrays 
     * 
     * @param a
     * @param b
     * @return
     */
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Compare tow bytearrays.
     * 
     * @param a
     * @param b
     * @return true if contents of two byte array are match.
     */
    public static boolean compare(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare parts of bytearrays.
     * 
     * @param a
     * @param a_pos
     * @param b
     * @param b_pos
     * @param length
     * @return true if match.
     */
    public static boolean compare(byte[] a, int a_pos, byte[] b, int b_pos,
            int length) {
        if (a == null || b == null) {
            return length == 0;
        }
        if ((a.length < a_pos + length) || (b.length < b_pos + length)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (a[a_pos + i] != b[b_pos + i]) {
                return false;
            }
        }
        return true;
    }
}
