package xcu.stu.assistant.utils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import xcu.stu.assistant.application.MyApplication;
import xcu.stu.assistant.utils.callback.jsonCallback;

/**
 * 网络请求的工具类，用于从网络请求数据时使用
 * Created by 孙文权 on 2016/3/2.
 */
public class requestUtil {
    //请求json数据的方法
    public static void jsonRequest(String url, final jsonCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //将获取到的数据回掉给调用方
                callback.getJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //最后要将request对象添加到请求队列才能生效
        MyApplication.requestQueue.add(request);
    }
}
