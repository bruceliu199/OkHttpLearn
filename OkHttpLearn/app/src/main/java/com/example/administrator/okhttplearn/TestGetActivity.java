package com.example.administrator.okhttplearn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * DATE：2018/4/3
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class TestGetActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_get);
        new Thread(runnable).start();
//       使用enqueue方法，将call放入请求队列，然后okHttp会在线程池中进行网络访问；
//        try {
//            asynGet();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                syncGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 同步请求get
     * @throws Exception
     */
    public void syncGet() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
//                .url("http://device.quanjiakan.com/devices/api?handler=watch&action=devicelist&memberId=11780&platform=2&token=2156b5715e8afbd0e6b63469f292fba2")
                .build();

        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            Log.e(TAG, "请求头: ---"+ headers.name(i) + ": " + headers.value(i));
        }

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Headers responseHeaders = response.headers();
        ResponseBody responseBody = response.body();
        for (int i = 0; i < responseHeaders.size(); i++) {
            Log.e(TAG, "响应头:---" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        Log.e(TAG, "响应体:---" + responseBody.string());

    }

    /**
     * 异步Get请求
     * @throws Exception
     */
    public void asynGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://device.quanjiakan.com/devices/api?handler=watch&action=devicelist&memberId=11780&platform=2&token=2156b5715e8afbd0e6b63469f292fba2")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: --" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.e(TAG, "onResponse: --" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println();
                Log.e(TAG, "onResponse: --" + response.body().string());
            }
        });
    }


}
