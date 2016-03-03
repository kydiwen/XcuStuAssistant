package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import xcu.stu.assistant.R;

public class WeatherDetailActivity extends Activity {
    private JSONObject weatherData;//天气数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    //初始化视图界面
    private void initView() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_detail);
    }

    //初始化数据信息
    private void initData() {
        //获取到上一个界面的json数据，并赋值给weatherData
        Intent intent = getIntent();
        try {
            weatherData = new JSONObject(intent.getStringExtra(MainActivity.WEATHER_GIVE));
            showWeather(weatherData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取到天气的json数据并显示到界面上
    private void showWeather(JSONObject weather) {

    }
}
