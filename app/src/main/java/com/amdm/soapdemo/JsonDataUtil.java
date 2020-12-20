package com.amdm.soapdemo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author luoshipeng
 * createDate：2019/7/30 0030 9:58
 * className   JsonDataUtil
 * Des：TODO
 */
public class JsonDataUtil {
    private static final String TAG = "  ====  " + JsonDataUtil.class.getSimpleName() + ":  ";

    public static String getValue(String json, String key) {
        if (json == null) {
            return null;
        } else {
            String replace = json.replace("[", "").replace("]", "");
            try {
                JSONObject jsonObject = new JSONObject(replace);
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "[]";
        }
    }


    public static String getDecryptResult(String jsonArray, String aesKey) {
        if (jsonArray != null) {
            String responseCode1 = JsonDataUtil.getValue(jsonArray, "result");
            if ("200".equals(responseCode1)) {
                String result = JsonDataUtil.getValue(jsonArray, "result");
                return AesEncryptor.decrypt(result, aesKey);
            } else {
                String result = JsonDataUtil.getValue(jsonArray, "result");
                String decrypt = AesEncryptor.decrypt(result, aesKey);
                Log.d(TAG, "请求失效的结果：  没有权限 " + decrypt);
                return decrypt;
            }
        } else {
            Log.d(TAG, "请求失效  结果为空   ");
            return null;
        }
    }



}