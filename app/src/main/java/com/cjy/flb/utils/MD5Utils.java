package com.cjy.flb.utils;

/**
 * Created by Administrator on 2015/11/24 0024.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Utils {
    /**
     * 将明文进行加密返回
     * @param textStr 要进行加密的明文
     * @return 返回明文
     */
    public static String encode(String textStr) {
        if (textStr!=null) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("md5");
                byte[] bytes = messageDigest.digest(textStr.getBytes());
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : bytes) {
                    int result = b & 0xff;
                    String hexString = Integer.toHexString(result);
                    if (hexString.length() == 1) {
                        stringBuilder.append("0");
                    }
                    stringBuilder.append(hexString);
                }
                return stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }
}