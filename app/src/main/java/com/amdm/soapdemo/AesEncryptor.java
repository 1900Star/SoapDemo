package com.amdm.soapdemo;

import android.util.Base64;
import android.util.Log;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author luoshipeng
 * createDate：2019/9/7 0007 14:26
 * className   AesEncryptor
 * Des：TODO
 */
public class AesEncryptor {
    private static final String TAG = " ==== " + AesEncryptor.class.getSimpleName() + "  ";
    // 加密

    /**
     * @param sSrc 数据
     * @param sKey 密钥
     * @return str
     */
    public static String encrypt(String sSrc, String sKey) {
        if (sKey == null) {
            Log.d(TAG,"encrypt Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            Log.d(TAG,"encrypt Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //"算法/模式/补码方式"
        Cipher cipher;
        try {

            cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));

            //.encodeToString(encrypted,0);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public static String decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                Log.d(TAG,"decrypt Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                Log.d(TAG,"decrypt Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            //先用base64解密
            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original, StandardCharsets.UTF_8);
            } catch (Exception e) {
                Log.d(TAG,e.toString());
                return null;
            }
        } catch (Exception ex) {
            Log.d(TAG,ex.toString());
            return null;
        }
    }
    /**
     * Base64加密字符串
     *
     * @param content -- 代加密字符串
     * @return s
     */
    public static String base64Encode(String content) {
        String charsetName = "UTF-8";
        byte[] contentByte = new byte[0];
        try {
            contentByte = content.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(contentByte, Base64.DEFAULT);
    }
}
