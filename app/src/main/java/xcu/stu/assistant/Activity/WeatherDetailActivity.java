package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;

import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.progressdialogUtil;
import xcu.stu.assistant.utils.requestUtil;

public class WeatherDetailActivity extends Activity {
    private JSONObject weatherData;//天气数据
    private SwipeRefreshLayout refresh_layout;
    private ScrollView weather_container;//显示天气信息的容器
    private ImageView weather_detail_back;//天气详情页面顶部返回按钮
    private TextView weather_detail_currentcity;//当前选择的城市
    private ImageView weather_detail_location;//城市选择按钮
    private TextView weather_detail_temp;//温度范围
    private TextView weather_detail_weather;//天气类型
    private TextView weather_detail_pollution;//污染状况
    private TextView comfort_dress;//穿衣指数
    private TextView comfort_cleancar;//洗车指数
    private TextView comfort_travel;//旅游指数
    private TextView comfort_ill;//感冒指数
    private TextView comfort_play;//运动指数
    private TextView comfort_purple;//紫外线强度
    private TextView furtureday_one;//未来第一天
    private ImageView furtureday_one_img;//未来第一天天气图片
    private TextView furtureday_one_temp;//未来第一天天气温度
    private TextView furtureday_two;//未来第二天
    private ImageView furtureday_two_img;//未来第二天天气图片
    private TextView furtureday_two_temp;//未来第二天天气温度
    private TextView furtureday_three;//未来第三天
    private ImageView furtureday_three_img;//未来第三天天气图片
    private TextView furtureday_three_temp;//未来第三天天气温度
    private String currentCity = "";//当前所在城市
    private Context mContext;//全局可用的context对象
    private static final int CITY_REQUESTCODE = 0;//城市选择请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        mContext = WeatherDetailActivity.this;
        //显示正在加载进度条
        progressdialogUtil.showDialog(mContext,"正在加载...");
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_detail);
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        weather_container = (ScrollView) findViewById(R.id.weather_container);
        weather_detail_back = (ImageView) findViewById(R.id.weather_detail_back);
        weather_detail_currentcity = (TextView) findViewById(R.id.weather_detail_currentcity);
        weather_detail_location = (ImageView) findViewById(R.id.weather_detail_location);
        weather_detail_temp = (TextView) findViewById(R.id.weather_detail_temp);
        weather_detail_weather = (TextView) findViewById(R.id.weather_detail_weather);
        weather_detail_pollution = (TextView) findViewById(R.id.weather_detail_pollution);
        comfort_dress = (TextView) findViewById(R.id.comfort_dress);
        comfort_cleancar = (TextView) findViewById(R.id.comfort_cleancar);
        comfort_travel = (TextView) findViewById(R.id.comfort_travel);
        comfort_ill = (TextView) findViewById(R.id.comfort_ill);
        comfort_play = (TextView) findViewById(R.id.comfort_play);
        comfort_purple = (TextView) findViewById(R.id.comfort_purple);
        furtureday_one = (TextView) findViewById(R.id.furtureday_one);
        furtureday_one_img = (ImageView) findViewById(R.id.furtureday_one_img);
        furtureday_one_temp = (TextView) findViewById(R.id.furtureday_one_temp);
        furtureday_two = (TextView) findViewById(R.id.furtureday_two);
        furtureday_two_img = (ImageView) findViewById(R.id.furtureday_two_img);
        furtureday_two_temp = (TextView) findViewById(R.id.furtureday_two_temp);
        furtureday_three = (TextView) findViewById(R.id.furtureday_three);
        furtureday_three_img = (ImageView) findViewById(R.id.furtureday_three_img);
        furtureday_three_temp = (TextView) findViewById(R.id.furtureday_three_temp);
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

    //初始化监听事件
    private void initListener() {
        //为返回按钮设置点击事件
        weather_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束当前activity的生命周期
                finish();
            }
        });
        //设置下拉刷新事件
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWeatherInfoShowed();
            }
        });
        //为城市选择按钮设置点击事件
        weather_detail_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用startactivityforresult方式进入城市选择界面，获取选择的城市信息
                Intent intent = new Intent(mContext, CityListActivity.class);
                startActivityForResult(intent, CITY_REQUESTCODE);
            }
        });
    }

    //更新天气信息
    private void updateWeatherInfoShowed() {
        getWeatherInfo(currentCity);
        //显示天气信息
        //showWeather(weatherData);
    }

    //刷新数据，重新获取天气信息
    private void getWeatherInfo(String city) {
        try {
            String url = getResources().getString(R.string.bd_weather_url) + URLEncoder.encode(city,
                    "utf-8") + "&output=json&ak=" + getResources().getString(R.string.my_bd_ak)
                    + "&mcode=" + getResources().getString(R.string.bd_mcode);
            requestUtil.jsonRequest(url, new jsonCallback() {
                @Override
                public void getJson(JSONObject response) {
                    //为天气数据重新赋值
                    weatherData = response;
                    //显示天气信息
                    showWeather(weatherData);
                    //完成刷新，刷新状态停止
                    refresh_layout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取到天气的json数据并显示到界面上
    private void showWeather(JSONObject weather) {
        //根据天气状况加载背景图片
        setDetailBackground(weather);
        //显示天气详情
        showWeatherDetail(weather);
        //显示未来天气信息
        showFurtureWeather(weather);
    }

    //根据不同的天气状况设置不同的背景图片
    private void setDetailBackground(JSONObject weather) {
        //图片代表的天气类型:
        // weather1（白天多云）
        // weather2(白天晴)
        //weather3(有雪)
        //weather4(有雨)
        //weather5(夜晚多云)
        //weather6(夜晚晴)
        //weather7(阴天)
        //天气状况
        try {
            String weatherString = weather.getJSONArray("results").getJSONObject(0).getJSONArray
                    ("weather_data").getJSONObject(0).getString("weather");
            // 判断是白天还是夜晚
            int timeNow = Integer.parseInt((String) DateFormat.format("HH", new Date()));
            if (weatherString.equals("晴")) {
                if (timeNow > 6 && timeNow < 18) {
                    weather_container.setBackgroundResource(R.drawable.weather2);
                } else {
                    weather_container.setBackgroundResource(R.drawable.weather6);
                }
            } else if (weatherString.equals("多云")) {
                if (timeNow > 6 && timeNow < 18) {
                    weather_container.setBackgroundResource(R.drawable.weather1);
                } else {
                    weather_container.setBackgroundResource(R.drawable.weather5);
                }
            } else if (weatherString.contains("雪")) {
                weather_container.setBackgroundResource(R.drawable.weather3);
            } else if (weatherString.contains("雨")) {
                weather_container.setBackgroundResource(R.drawable.weather4);
            } else if (weatherString.contains("阴")) {
                weather_container.setBackgroundResource(R.drawable.weather7);
            } else {
                weather_container.setBackgroundResource(R.drawable.weather2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示天气信息
    private void showWeatherDetail(JSONObject weather) {
        try {
            //显示当前所在城市
            currentCity = weather.getJSONArray("results").getJSONObject(0).getString("currentCity");
            weather_detail_currentcity.setText(currentCity);
            //显示天气信息
            JSONObject todayWeather = weather.getJSONArray("results").getJSONObject(0).getJSONArray
                    ("weather_data").getJSONObject(0);
            //显示温度信息
            weather_detail_temp.setText(todayWeather.getString("temperature"));
            //显示天气类型
            weather_detail_weather.setText(todayWeather.getString("weather"));
            //显示污染状况
            //获取pm2.5的值
            String pm = weather.getJSONArray("results").getJSONObject(0).getString("pm25");
            //转化为整数值
            int pm25 = Integer.parseInt(pm);
            //根据pm25的范围判断污染状况
            if (pm25 >= 0 && pm25 <= 50) {
                weather_detail_pollution.setText(pm + " 空气优");
            } else if (pm25 >= 51 && pm25 <= 100) {
                weather_detail_pollution.setText(pm + " 空气良");
            } else if (pm25 >= 101 && pm25 <= 150) {
                weather_detail_pollution.setText(pm + " 轻度污染");
            } else if (pm25 >= 151 && pm25 <= 200) {
                weather_detail_pollution.setText(pm + " 中度污染");
            } else if (pm25 >= 201 && pm25 <= 250) {
                weather_detail_pollution.setText(pm + "重度污染");
            } else {
                weather_detail_pollution.setText(pm + " 严重污染");
            }
            //显示舒适指数
            JSONArray comfortData = weather.getJSONArray("results").getJSONObject(0).getJSONArray("index");
            //显示穿衣指数
            comfort_dress.setText("穿衣：" + comfortData.getJSONObject(0).getString("zs"));
            //显示洗车指数
            comfort_cleancar.setText("洗车：" + comfortData.getJSONObject(1).getString("zs"));
            //显示旅游指数
            comfort_travel.setText("旅游：" + comfortData.getJSONObject(2).getString("zs"));
            //显示感冒指数
            comfort_ill.setText("感冒：" + comfortData.getJSONObject(3).getString("zs"));
            //显示运动指数
            comfort_play.setText("运动：" + comfortData.getJSONObject(4).getString("zs"));
            //显示紫外线指数
            comfort_purple.setText("紫外线：" + comfortData.getJSONObject(5).getString("zs"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示未来天气信息
    private void showFurtureWeather(JSONObject weather) {
        try {
            //显示未来第一天天气信息
            JSONObject dayone = weather.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data")
                    .getJSONObject(1);
            //显示时间
            furtureday_one.setText(dayone.getString("date"));
            //显示温度
            furtureday_one_temp.setText(dayone.getString("temperature"));
            //显示天气图片
            //天气图片获取链接
            String imgUrl1 = dayone.getString("dayPictureUrl");
            //加载天气图片
            loadImg(imgUrl1, furtureday_one_img);


            //显示未来第二天天气信息
            JSONObject daytwo = weather.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data")
                    .getJSONObject(2);
            //显示时间
            furtureday_two.setText(daytwo.getString("date"));
            //显示温度
            furtureday_two_temp.setText(daytwo.getString("temperature"));
            //显示天气图片
            //天气图片获取链接
            String imgUrl2 = daytwo.getString("dayPictureUrl");
            //加载天气图片
            loadImg(imgUrl2, furtureday_two_img);


            //显示未来第三天天气信息
            JSONObject daythree = weather.getJSONArray("results").getJSONObject(0).getJSONArray
                    ("weather_data").getJSONObject(3);
            //显示时间
            furtureday_three.setText(daythree.getString("date"));
            //显示温度
            furtureday_three_temp.setText(daythree.getString("temperature"));
            //显示天气图片
            //天气图片获取链接
            String imgUrl3 = daythree.getString("dayPictureUrl");
            //加载天气图片
            loadImg(imgUrl3, furtureday_three_img);
            //隐藏进度条
            progressdialogUtil.cancelDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //从网络获取天气图片，并显示到imageview上
    private void loadImg(String url, final ImageView imageView) {
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }
    //获取请求页面返回的数据

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY_REQUESTCODE && resultCode == RESULT_OK) {
            //获取返回idea城市信息，刷新天气数据
            currentCity = data.getStringExtra(CityListActivity.RETURN_DATA);
            updateWeatherInfoShowed();
            refresh_layout.setRefreshing(true);
        }
    }
}
