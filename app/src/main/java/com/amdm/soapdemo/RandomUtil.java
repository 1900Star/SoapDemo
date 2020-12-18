package com.amdm.soapdemo;

import java.util.Random;

/**
 * @author luoshipeng
 * createDate：2019/9/7 0007 19:16
 * className   RandomUtil
 * Des：TODO
 */
public class RandomUtil {

    /**
     * 获取验证过的随机密码
     *
     * @param len l
     * @return s
     */
    public static String getRandomKey(int len) {
        String result;
        result = makeRandomPassword(len);
        if (result.matches(".*[a-z]{1,}.*") && result.matches(".*[A-Z]{1,}.*") && result.matches(".*[0-9]{1,}.*") && result.matches(".*[~!@#$%^&*.?]{1,}.*")) {
            return result;
        }
        return getRandomKey(len);
    }

    /**
     * 随机生成指定长度的字符串
     *
     * @param len l
     * @return r
     */
    private static String makeRandomPassword(int len) {
        char[] charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int x = 0; x < len; ++x) {
            sb.append(charArray[r.nextInt(charArray.length)]);
        }
        return sb.toString();
    }
}
