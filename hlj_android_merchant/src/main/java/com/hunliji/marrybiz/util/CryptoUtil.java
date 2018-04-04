package com.hunliji.marrybiz.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by LuoHanLin on 14/11/6.
 */
public class CryptoUtil {

    private static final String CRYPT_LOGIN_TOKEN_MIX = "weddingmerchant";
    private static final String CRYPT_LOGIN_PASSWORD_MIX = "merchant";

    public static String encryptLoginAccessToken(String accessToken, String timestamp) {
        String mixed = accessToken + CRYPT_LOGIN_TOKEN_MIX + timestamp;

        return getMD5(mixed);
    }

    public static String encryptPasswordWithSalt(String password, String salt) {
        String mixed = password + CRYPT_LOGIN_PASSWORD_MIX + salt;

        return getSHA256(mixed);
    }

    public static String getMD5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int temp = 0xff & b;
                if (temp <= 0x0F) {
                    sb.append("0" + Integer.toHexString(temp));
                } else {
                    sb.append(Integer.toHexString(temp));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSHA256(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(str.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int temp = 0xff & b;
                if (temp <= 0x0F) {
                    sb.append("0" + Integer.toHexString(temp));
                } else {
                    sb.append(Integer.toHexString(temp));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
