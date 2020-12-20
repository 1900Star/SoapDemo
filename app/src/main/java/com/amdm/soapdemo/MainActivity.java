package com.amdm.soapdemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String LH_WEB_SERVER_URL = "http://112.95.175.35:30004/WebService.asmx";
    private static final String GET_KEY_RESULT = "GetKeyResult";
    private static final String GET_TABLE_RESULT = "GetTableResult";
    private static final String UPDATE_TABLE_RESULT = "UpdateTableResult";
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv_content);
        Button btn = findViewById(R.id.btn);
        mHandler = new Handler(this.getMainLooper());
        btn.setOnClickListener(v -> new Thread(() -> {
            String randomKey = RandomUtil.getRandomKey(16);
//            String data = getData();
//            Log.d("lsp", "返回的数据    " + data);
            String aesKey = getKey(randomKey);
            if (aesKey != null) {

                Log.d("lsp", "返回的数据  aesKey  " + aesKey);
//                getTableData(randomKey, aesKey);
                updateTableData(randomKey, aesKey);
            }

        }).start());
    }

    private void getTableData(String randomKey, String aesKey) {

        String sql = "SELECT TOP 10 * FROM [DB_Light_LH].[dbo].[Log_list]";
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

            Log.d("lsp", "解析结果 JsonArray  " + s);
            String decryptResult = JsonDataUtil.getDecryptResult(s, aesKey);
            Log.d("lsp", "decryptResult   " + decryptResult);

            bufferedReader.close();


        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }


    }
    private void updateTableData(String randomKey, String aesKey) {

        String sql = "update [DB_Light_LH].[dbo].[Log_list] set c_permissions = '{1}' where c_id = 21858";
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


        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }


    }


    private String getKey(String randomKey) {
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