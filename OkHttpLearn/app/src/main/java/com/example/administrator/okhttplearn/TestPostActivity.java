package com.example.administrator.okhttplearn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * DATE：2018/4/9
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class TestPostActivity extends AppCompatActivity {

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final String TAG = "TestPostActivity";
    private final OkHttpClient client = new OkHttpClient();
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_post);
        //1.post提交键值对 form
        //异步同步已经知道他们的区别了，这里为了简化采用同步请求
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                //同步
//                try {
//                    String url = "http://api.k780.com:88/";
//                    OkHttpClient okHttpClient = new OkHttpClient();
//                    FormBody formBody = new FormBody.Builder()
//                            .add("app", "weather.future")
//                            .add("weaid", "1")
//                            .add("appkey", "10003")
//                            .add("sign",
//                                    "b59bc3ef6191eb9f747dd4e83c99f2a4")
//                            .add("format", "json")
//                            .build();
//
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .post(formBody)
//                            .build();
//                    okHttpClient.newCall(request).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    doPost();
//                    postStream();
//                    postMultipartBody();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            setCacheResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 2.Post提交String字符串
     * 可以使用Post方法发送一串字符串，但不建议发送超过1M的文本信息，如下示例展示了，发送一个markdown文本
     *
     * @throws Exception
     */
    public void doPost() throws Exception {
        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Log.e(TAG, "doPost: --" + response.body().string());
    }

    /**
     * post可以把流对象作为请求体，依赖okio将数据写成输出流的形式
     *
     * @throws IOException
     */
    public void postStream() throws IOException {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Log.e(TAG, "postStream: ---" + response.body().string());
    }

    /**
     * post提交文件
     *
     * @throws IOException
     */
    public void postFile() throws IOException {
        File file = new File("README.md");

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Log.e(TAG, "postFile: ---" + response.body().string());
    }

    /**
     * post分块提交
     *
     * @throws IOException
     */
    public void postMultipartBody() throws IOException {
        //创建请求体
        MultipartBody multipartBody = new MultipartBody.Builder("AaB03x")
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\""),
                        RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
                .build();
        //创建请求
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(multipartBody)
                .build();
        //用okhttpClient做请求
        Call call = client.newCall(request);
        Response response = call.execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Log.e(TAG, "postMultipartBody: --" + response.body().string());
    }

    public void setCacheResponse() throws Exception {
        //1.OkHttpClient设置缓存
        //a.设置缓存文件路径
        File cacheFile = new File(getCacheDir(), "okhttpcache");
        //b.设置缓存大小
        int cacheSize = 10 * 1024 * 1024; //10MB
        //c.Cache类创建缓存
        Cache cache = new Cache(cacheFile, cacheSize);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        //拦截器
        Interceptor cachInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isNetworkAvailable(TestPostActivity.this)) { //如果网络不可用
                    //重新Request
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)   //无网络只从缓存中读取
                            .build();
                } else {
                    //重新Request
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_NETWORK)   //有网络时只从网络获取
                            .build();
                }


                Response response = chain.proceed(request);
                if (isNetworkAvailable(TestPostActivity.this)) {
                    int maxAge = 5 * 60; //有网络时设置超时时间5分钟
                    response = response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)  //这个可以控制我们的我们频繁操作网络请求，给缓存设置一个有效时间，
                            // 在这段时间内会优先拿缓存，超过时间则拿网络数据并且更新缓存
                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    int maxStale = 24 * 60 * 60; //无网络时设置超时为1天
                    response = response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                }

                return response;

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(cachInterceptor)
                .addInterceptor(new LoggerInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)  //连接超时10s
                .readTimeout(10, TimeUnit.SECONDS) //读取超时10s
                .writeTimeout(10, TimeUnit.SECONDS) //写超时10s
                .cache(cache)
                .build();
        final Request request = new Request.Builder()
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("hello","luopeng")
                .url("http://app.quanjiakan.com/pingan/api?handler=jugui&action=getversion&alias=pingan_android_version&token=b3b837ada4b3c0d00c0957bd160764d2")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ---" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Log.e(TAG, "onResponse: ---" + response.body().string());
            }
        });

    }

    /**
     * 检测当的网络状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    class LoggerInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.d(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            Log.d(TAG, String.format("Received response for %s in %.1fms%n%sconnection=%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), chain.connection()));
            return response;
        }
    }

    //还可以设置Token
    class TokenInterceptor implements Interceptor {

        private static final String TOKEN = "Authorization";
        private String token;

        public TokenInterceptor(String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (token == null || originalRequest.header(TOKEN) != null) {
                return chain.proceed(originalRequest);
            } else {
                Request newRequest = originalRequest.newBuilder()
                        .header(TOKEN, token)
                        .build();
                return chain.proceed(newRequest);
            }

        }
    }
}
