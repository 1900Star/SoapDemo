package com.amdm.soapdemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.tv_content);
        Button btn = findViewById(R.id.btn);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        mHandler = new Handler(this.getMainLooper());
        btn.setOnClickListener(v -> new Thread(() -> {
            String randomKey = RandomUtil.getRandomKey(16);
            String aesKey = WebServiceUtil.getKey(randomKey);
            String js = "{\"DvcID\":\"1700\",\"Command\":\"召测\"}";
            String encryptJs = AesEncryptor.encrypt(js, aesKey);
            Log.d("lsp", "返回的数据  encryptJs  " + encryptJs);

            if (aesKey != null) {
                Log.d("lsp", "返回的数据  aesKey  " + aesKey);
                String tableData = WebServiceUtil.getTableData(randomKey, aesKey);
                Log.d("lsp", tableData);
            }
        }).start());
        btn2.setOnClickListener(v -> new Thread(() -> {
            String randomKey = RandomUtil.getRandomKey(16);
            String aesKey = WebServiceUtil.getKey(randomKey);
            if (aesKey != null) {
                Log.d("lsp", "返回的数据  aesKey  " + aesKey);
                boolean aBoolean = WebServiceUtil.updateTableData(randomKey, aesKey);
                Log.d("lsp",""+ aBoolean);
            }

        }).start());
        btn3.setOnClickListener(v -> {
            new Thread(() -> {
                String randomKey = RandomUtil.getRandomKey(16);
                String aesKey = WebServiceUtil.getKey(randomKey);
                if (aesKey != null) {
                    String mark = WebServiceUtil.postOrder(randomKey, aesKey);
                    Log.d("lsp", "mark   " + mark);
                    String s1 = WebServiceUtil.queryOrder(mark, randomKey, aesKey);
                    Log.d("lsp", "queryOrder  result  " + s1);

                }

            }).start();
        });
    }

}