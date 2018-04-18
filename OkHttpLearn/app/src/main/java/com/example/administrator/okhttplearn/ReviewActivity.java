package com.example.administrator.okhttplearn;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * DATE：2018/4/16
 * USER： liuzj
 * DESC：回顾一下我们的学习成果
 * email：liuzj@hi-board.com
 */
public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";
    private TextView content;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String obj = (String) msg.obj;
                    content.setText(obj);
                    break;

                default:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        content = (TextView) findViewById(R.id.text);
//        test1();//get请求
//        verifyStoragePermissions(this);
//        test2();//文件下载
        //当然Get也支持阻塞方式的同步请求，不过在开发中这种方法很少被使用。上面我们也说了Call有一个execute()方法，
        // 你也可以直接调用call.execute()返回一个Response。然后利用isSuccessful()判读是否成功,进行相应的结果解析。
//        test3();//form提交键值对
        test4();


    }

    /**
     * post异步上传文件
     */
    private void test4(){
        //1
        OkHttpClient okHttpClient = new OkHttpClient();
        //2 ***创建请求体
        //2.1 ***获取要上传的文件

        File file = new File(getCacheDir() + "hh.txt");
        try {

            InputStream is = getAssets().open("test.txt");
            writeBytesToFile(is,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.2 *** 创建MediaType设置创建文件类型(也就是请求头中的Content-Type)  就是设置请求的媒体类型text/plain代表纯文本，charset=utf-8编码格式utf-8
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        //2.3 ***获取请求体
        RequestBody requestBody = RequestBody.create(mediaType, file);
        //3.
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(requestBody)
                .build();

        try {
            Log.e(TAG, "test4: ---------" + requestBody.contentType() + requestBody.contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //4.
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ---------" + "失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: ---------" + "成功");
            }
        });
    }
    public static void writeBytesToFile(InputStream is, File file) throws IOException{
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while((nbread=is.read(data))>-1){
                fos.write(data,0,nbread);
            }
        }
        catch (Exception ex) {
            Log.e("Exception",ex.getMessage());
        }
        finally{
            if (fos!=null){
                fos.close();
            }
        }
    }

    /**
     * post请求,form形式提交键值对
     */
    private void test3() {
        //1.创建okhttpclient
        OkHttpClient okHttpClient = new OkHttpClient();

        //2.创建FormBody
        FormBody formBody = new FormBody.Builder()
                .add("password", "123456")
                .add("name", "haha")
                .build();

        //3.创建请求
        Request request = new Request.Builder()
                .url("http://baidu.com")
                .post(formBody)
                .build();

        //4.建立联系创建Call对象
        Call call = okHttpClient.newCall(request);

        //5.同步或者异步执行任务
        //call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: -------" + "请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: ------" + "请求成功");
            }
        });


    }

    private void test2() {
        //step 1: 不变的第一步创建 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient();

        //step 2: 创建Requset
        Request request = new Request.Builder()
                .url("http://picture.quanjiakan.com/quanjiakan/resources/doctor/20180320172332_t0jw9r3h6bk46iy0ehxd.jpg")
                .build();

        //step 3:建立联系，创建Call
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory(), "touxiang.jpg");
                        Log.e(TAG, "onResponse: -----" + file.getAbsolutePath());
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.flush();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("downloadAsynFile", "文件下载成功");
            }
        });
    }

    private void test1() {
                /*1.创建okhttpclient对象*/
        OkHttpClient okHttpClient = new OkHttpClient();

        /*2.创建一个请求，不设置请求方法默认是GET*/
        Request.Builder requestBuilder = new Request.Builder().url("http://device.quanjiakan.com/devices/api?handler=watch&action=devicelist&memberId=11780&platform=2&token=708c2d9c28d38f85bd2c29c143e78806");

        /*3.创建call对象*/
        Call call = okHttpClient.newCall(requestBuilder.build());

        /*4.开始做请求，这里采用异步,也可以用call.execute()采用同步请求*/
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /*请求失败*/
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();//这里做个说明，算是okhttp的一个坑；在调用了response.body().string()方法之后，response中的流会被关闭，
                // 我们需要创建出一个新的response给应用层处理,多次调用会报java.lang.IllegalStateException: closed异常
                /*请求成功*/
                Log.e(TAG, "onResponse: --" + result);
                //在这里直接做ui更新是不允许的，因为回调并不在主线程会报异常
                // android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
//                content.setText(response.body().string());
                /*我们可以采用Handler sendMessage来进行主线程与工作线程的交互，还可以使用runOnUiThread切回到主线程做ui刷新*/
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    String state = jsonObject.getString("message");
//                    Message msg = mHandler.obtainMessage();
//                    msg.what = 1;
//                    msg.obj = state;
//                    mHandler.sendMessage(msg);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String state = jsonObject.getString("message");
                            content.setText(state);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        /*ps 假设请求网络的过程中我们退出当前界面了。我们是不是应该取消这个任务*/
//        call.cancel();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
