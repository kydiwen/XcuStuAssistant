package xcu.stu.assistant.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 孙文权 on 2016/3/2.
 * 创建application并初始化requestqueue对象，用于网络请求
 */
public class MyApplication extends Application {
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
}
