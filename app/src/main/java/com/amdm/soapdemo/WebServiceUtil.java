package com.amdm.soapdemo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author luoshipeng
 * createDate：2020/12/21 0021 9:41
 * className   WebServiceUtil
 * Des：TODO
 */
class WebServiceUtil {
    private static String Tag = "lsp";
    private static final String LH_WEB_SERVER_URL = "http://112.95.175.35:30004/WebService.asmx";
    private static final String GET_KEY_RESULT = "GetKeyResult";
    private static final String GET_TABLE_RESULT = "GetTableResult";
    private static final String UPDATE_TABLE_RESULT = "UpdateTableResult";
    private static final String POST_ORDER2_RESULT = "PostOrder2Result";
    private static final String QUERY_ORDER_RESULT = "QueryOrderResult";

    public static String getKey(String randomKey) {
        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction",
                    "http://tempuri.org/GetKey");

            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();

            sb.append("<id>").append(randomKey).append("</id>");
            String header_sb = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soap:Body>" +
                    "<GetKey xmlns=\"http://tempuri.org/\">";
            String footer_sb = "</GetKey>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";
            out.write(header_sb + sb.toString() + footer_sb); // 直接post的进行调用！

            //解析返回的XML字串
            out.flush();
            out.close();
            connection.connect();

            InputStream urlStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
            String s = parseResponseXml(urlStream, GET_KEY_RESULT);
            Log.d("lsp", "解析结果 Key " + s);
            bufferedReader.close();
            return s;

        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }
        return null;
    }

    public static String postOrder(String randomKey, String aesKey) {

        String js = "{\"DvcID\":\"1700\",\"Command\":\"召测\"}";
        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction",
                    "http://tempuri.org/PostOrder2");

            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8);
            StringBuilder header_sb = new StringBuilder();
            String encryptUserName = AesEncryptor.encrypt("admin", aesKey);
            String encryptPassword = AesEncryptor.encrypt("admin", aesKey);
            String encryptJs = AesEncryptor.encrypt(js, aesKey);
            header_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            header_sb
                    .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");

            header_sb.append("<soap:Header>");
            header_sb.append("<MySoapHeader xmlns=\"http://tempuri.org/\">");
            header_sb.append("<UserID>").append(encryptUserName).append("</UserID>");
            header_sb.append("<PassWord>").append(encryptPassword).append("</PassWord>");
            header_sb.append("</MySoapHeader>");
            header_sb.append("</soap:Header>");

            header_sb.append("<soap:Body>");
            header_sb.append("<PostOrder2 xmlns=\"http://tempuri.org/\">");
            header_sb.append("<id>").append(randomKey).append("</id>");
            header_sb.append("<user>").append("18575539075").append("</user>");
            header_sb.append("<js>").append(encryptJs).append("</js>");
            header_sb.append("</PostOrder2>");
            header_sb.append("</soap:Body>");
            header_sb.append("</soap:Envelope>");
            // 需要的参数
            out.write(header_sb.toString()); // 直接post的进行调用！

            //解析返回的XML字串
            out.flush();
            out.close();
            connection.connect();

            InputStream urlStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
            String s = parseResponseXml(urlStream, POST_ORDER2_RESULT);

            String mark = JsonDataUtil.getDecryptResult(s, aesKey);
//            Log.d("lsp", "decryptResult   " + decryptResult);
            bufferedReader.close();
            return mark;
        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }

        return "";
    }

    public static String queryOrder(String mark, String randomKey, String aesKey) {

        try {

            FutureTask<String> futureTask = new FutureTask<>(() -> {
                // 调用webservice
                while (true) {
                    URL url = new URL(LH_WEB_SERVER_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type",
                            "text/xml; charset=utf-8");
                    connection.setRequestProperty("SOAPAction",
                            "http://tempuri.org/QueryOrder");

                    OutputStreamWriter out = new OutputStreamWriter(
                            connection.getOutputStream(), StandardCharsets.UTF_8);
                    StringBuilder header_sb = new StringBuilder();
                    String encryptUserName = AesEncryptor.encrypt("admin", aesKey);
                    String encryptPassword = AesEncryptor.encrypt("admin", aesKey);
                    String encryptMark = AesEncryptor.encrypt(mark, aesKey);
                    header_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    header_sb.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");

                    header_sb.append("<soap:Header>");
                    header_sb.append("<MySoapHeader xmlns=\"http://tempuri.org/\">");
                    header_sb.append("<UserID>").append(encryptUserName).append("</UserID>");
                    header_sb.append("<PassWord>").append(encryptPassword).append("</PassWord>");
                    header_sb.append("</MySoapHeader>");
                    header_sb.append("</soap:Header>");

                    header_sb.append("<soap:Body>");
                    header_sb.append("<QueryOrder xmlns=\"http://tempuri.org/\">");
                    header_sb.append("<id>").append(randomKey).append("</id>");
                    header_sb.append("<mark>").append(encryptMark).append("</mark>");
                    header_sb.append("</QueryOrder>");
                    header_sb.append("</soap:Body>");
                    header_sb.append("</soap:Envelope>");
                    out.write(header_sb.toString()); // 直接post的进行调用！
                    //解析返回的XML字串
                    out.flush();
                    out.close();
                    connection.connect();
                    InputStream urlStream = connection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlStream));
                    String s = parseResponseXml(urlStream, QUERY_ORDER_RESULT);
                    Log.d("lsp", "解析结果 QUERY_ORDER_RESULT  " + s);
                    if (s != null) {
                        String decryptResult = JsonDataUtil.getDecryptResult(s, aesKey);

                        Log.d("lsp", "解析结果 decryptResult  " + decryptResult);
                        if (decryptResult != null) {
                            if (!decryptResult.contains("等待")) {
                                bufferedReader.close();
                                return decryptResult;
                            }
                        } else {
                            return "失败";
                        }
                    } else {
                        return "失败";
                    }

                    Log.d(Tag, "BBBBBBBBBBBBBB");
                }
            });


            ThreadPoolProxyFactory.newInstance().execute(futureTask);
            try {
                return futureTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        } catch (
                Exception e) {
            Log.d("lsp", e.getMessage());

        }

        return "";
    }

    public static String getTableData(String randomKey, String aesKey) {

        String sql = "SELECT TOP 10 * FROM [DB_Light_LH].[dbo].[Log_list] order by c_datetime desc";
        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction",
                    "http://tempuri.org/GetTable");

            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8);
            StringBuilder header_sb = new StringBuilder();
            String encryptUserName = AesEncryptor.encrypt("admin", aesKey);
            String encryptPassword = AesEncryptor.encrypt("admin", aesKey);
            String encryptSql = AesEncryptor.encrypt(sql, aesKey);
            header_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            header_sb
                    .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");

            header_sb.append("<soap:Header>");
            header_sb.append("<MySoapHeader xmlns=\"http://tempuri.org/\">");
            header_sb.append("<UserID>").append(encryptUserName).append("</UserID>");
            header_sb.append("<PassWord>").append(encryptPassword).append("</PassWord>");
            header_sb.append("</MySoapHeader>");
            header_sb.append("</soap:Header>");

            header_sb.append("<soap:Body>");
            header_sb.append("<GetTable xmlns=\"http://tempuri.org/\">");
            header_sb.append("<id>").append(randomKey).append("</id>");
            header_sb.append("<sql>").append(encryptSql).append("</sql>");
            header_sb.append("</GetTable>");
            header_sb.append("</soap:Body>");
            header_sb.append("</soap:Envelope>");
            Log.d("lsp", "最后的参数   " + header_sb.toString());
            // 需要的参数
            out.write(header_sb.toString()); // 直接post的进行调用！

            //解析返回的XML字串
            out.flush();
            out.close();
            connection.connect();

            InputStream urlStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
            String s = parseResponseXml(urlStream, GET_TABLE_RESULT);

//            Log.d("lsp", "解析结果 JsonArray  " + s);
            String decryptResult = JsonDataUtil.getDecryptResult(s, aesKey);
            bufferedReader.close();
            return decryptResult;

        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }

        return null;
    }

    public static boolean updateTableData(String randomKey, String aesKey) {

        String sql = "update [DB_Light_LH].[dbo].[Log_list] set c_Ip = 'App' where c_id = 162793";
        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction",
                    "http://tempuri.org/UpdateTable");

            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8);
            StringBuilder header_sb = new StringBuilder();
            String encryptUserName = AesEncryptor.encrypt("admin", aesKey);
            String encryptPassword = AesEncryptor.encrypt("admin", aesKey);
            String encryptSql = AesEncryptor.encrypt(sql, aesKey);
            header_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            header_sb
                    .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");

            header_sb.append("<soap:Header>");
            header_sb.append("<MySoapHeader xmlns=\"http://tempuri.org/\">");
            header_sb.append("<UserID>").append(encryptUserName).append("</UserID>");
            header_sb.append("<PassWord>").append(encryptPassword).append("</PassWord>");
            header_sb.append("</MySoapHeader>");
            header_sb.append("</soap:Header>");

            header_sb.append("<soap:Body>");
            header_sb.append("<UpdateTable xmlns=\"http://tempuri.org/\">");
            header_sb.append("<id>").append(randomKey).append("</id>");
            header_sb.append("<sql>").append(encryptSql).append("</sql>");
            header_sb.append("</UpdateTable>");
            header_sb.append("</soap:Body>");
            header_sb.append("</soap:Envelope>");
            Log.d("lsp", "最后的参数   " + header_sb.toString());
            // 需要的参数
            out.write(header_sb.toString()); // 直接post的进行调用！

            //解析返回的XML字串
            out.flush();
            out.close();
            connection.connect();

            InputStream urlStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
            String s = parseResponseXml(urlStream, UPDATE_TABLE_RESULT);

            Log.d("lsp", "解析结果 update JsonArray  " + s);
            String decryptResult = JsonDataUtil.getDecryptResult(s, aesKey);
            Log.d("lsp", "decryptResult  update " + decryptResult);
            bufferedReader.close();
            return decryptResult.equals("1");

        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }

        return false;
    }

    private static String parseResponseXml(InputStream inStream, String nodeName) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inStream, "UTF-8");
        int eventType = parser.getEventType();// 产生第一个事件
        while (eventType != XmlPullParser.END_DOCUMENT) {
            // 只要不是文档结束事件
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();// 获取解析器当前指向的元素的名称
                if (nodeName.equals(name)) {
                    return parser.nextText();
                }
            }
            eventType = parser.next();
        }
        return null;
    }

}
