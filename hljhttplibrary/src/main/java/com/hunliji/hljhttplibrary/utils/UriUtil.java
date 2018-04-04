package com.hunliji.hljhttplibrary.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by werther on 16/7/28.
 */
public class UriUtil {


    public static String SHA1(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(text.getBytes("utf-8"));
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }


    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char)
                        ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String bodyToString(final RequestBody body) {
        try {
            if (body == null || body.contentLength() == 0||body instanceof MultipartBody) {
                return null;
            }

            final Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return null;
        }
    }


}
