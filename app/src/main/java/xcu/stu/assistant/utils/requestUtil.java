package xcu.stu.assistant.utils;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import xcu.stu.assistant.application.MyApplication;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.callback.HtmlCallback;
import xcu.stu.assistant.utils.callback.StringCallback;
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

    //获取String类型数据的方法
    public static void getString(String url, final StringCallback callback) {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.StringListener(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.requestQueue.add(request);
    }

    //获取bitmap数据类型
    public static void getBitmap(String url, final BitmapCallback callback) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                callback.getBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.requestQueue.add(request);
    }

    //获取html数据
    public static  void requestHtmlData(final String mUrl, final HtmlCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //避免乱码问题的出现
                    Document document = Jsoup.parse(new URL(mUrl).openStream(), "utf-8", mUrl);
                    //回调结果
                    callback.getHtml(document);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
