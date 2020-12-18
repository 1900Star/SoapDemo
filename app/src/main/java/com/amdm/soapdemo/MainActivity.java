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
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LH_WEB_SERVER_URL = "http://112.95.175.35:30004/WebService.asmx";
    private static final String GET_KEY_RESULT = "GetKeyResult";
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv_content);
        Button btn = findViewById(R.id.btn);
        mHandler = new Handler(this.getMainLooper());
        btn.setOnClickListener(v -> new Thread(() -> {
//            String data = getData();
//            Log.d("lsp", "返回的数据    " + data);
            getKey();

        }).start());
    }

    private void getJsonData(String randomKey, String aesKey) {

        String Sql = "SELECT TOP 10 * FROM [DB_Light_LH].[dbo].[ListUser]";

        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction",
                    "http://tempuri.org/GetKey");

            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8);
            StringBuilder header_sb = new StringBuilder();
            StringBuilder footer_sb = new StringBuilder();

//          <?xml version="1.0" encoding="utf-8"?>
//            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//  <soap:Header>
//    <MySoapHeader xmlns="http://tempuri.org/">
//      <UserID>string</UserID>
//      <PassWord>string</PassWord>
//    </MySoapHeader>
//  </soap:Header>
//  <soap:Body>
//    <GetTable xmlns="http://tempuri.org/">
//      <id>string</id>
//      <sql>string</sql>
//    </GetTable>
//  </soap:Body>
//</soap:Envelope>
            StringBuilder headerBuilder = new StringBuilder();
            header_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            header_sb
                    .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");

            header_sb.append("<soap:Header>");
            header_sb.append("<MySoapHeader xmlns=\"http://tempuri.org/\">");
            headerBuilder.append("<UserID>").append(randomKey).append("</UserID>");
            headerBuilder.append("<PassWord>").append(randomKey).append("</PassWord>");
            header_sb.append("</MySoapHeader>");
            header_sb.append("</soap:Header>");

            header_sb.append("<soap:Body>");
            header_sb.append("<GetTable xmlns=\"http://tempuri.org/\">");
            header_sb.append("<id>").append(randomKey).append("</id>");
            header_sb.append("<sql>").append(randomKey).append("</sql>");
            header_sb.append("</GetTable>");
            header_sb.append("</Body>");
            header_sb.append("</soap:Envelope>");



            // 需要的参数
            StringBuilder idBuilder = new StringBuilder();

            Log.d("lsp", "随机数  " + randomKey);
            idBuilder.append("<id>").append(randomKey).append("</id>");
            out.write(header_sb + idBuilder.toString() + footer_sb); // 直接post的进行调用！

            //解析返回的XML字串
            out.flush();
            out.close();
            connection.connect();

            InputStream urlStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
            String s = parseResponseXml(urlStream, GET_KEY_RESULT);
            Log.d("lsp", "解析结果   " + s);

            bufferedReader.close();


        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }



    }


    private void getKey() {
        try {
            URL url = new URL(LH_WEB_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
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
            String randomKey = RandomUtil.getRandomKey(16);
            Log.d("lsp", "随机数  " + randomKey);
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
            Log.d("lsp", "解析结果   " + s);

            bufferedReader.close();


        } catch (Exception e) {
            Log.d("lsp", e.getMessage());

        }

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