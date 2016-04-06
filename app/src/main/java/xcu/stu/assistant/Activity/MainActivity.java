package xcu.stu.assistant.Activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.Fragment.controller.businessController;
import xcu.stu.assistant.Fragment.controller.haveFunController;
import xcu.stu.assistant.Fragment.controller.newsCenterController;
import xcu.stu.assistant.Fragment.controller.stuClassController;
import xcu.stu.assistant.R;
import xcu.stu.assistant.application.MyApplication;
import xcu.stu.assistant.receiver.bluetoothStateReceiver;
import xcu.stu.assistant.service.weatherUpdateService;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.requestUtil;

/**
 * 程序主界面
 * 顶部状态栏实现系统颜色与app一致
 * 顶部显示当前所在模块
 * 顶部显示当前天气信息
 * 底部使用viewpager与radiogroup结合fragment实现模块切换
 * 2016年3月1日
 * 孙文权
 * 2016年3月13日
 * 修改碎片的添加方式，使用add方式，代替replace
 * 解决了数据重复加载的问题
 * 通过碎片的显示与隐藏实现界面切换
 */
public class MainActivity extends FragmentActivity {
    private ArrayList<baseFragment> mainPages;//存放不同的控制器的集合
    private FragmentManager manager;
    private android.support.v4.app.FragmentTransaction transaction;//使用事务控制碎片的显示与隐藏
    private RadioGroup main_rg;//底部控制栏
    private TextView main_controller_text;//顶部指示当前模块的textview
    private ImageView main_weather_img;//显示天气信息的图片
    private TextView main_weather_city;//显示当前所在城市
    private TextView main_weather_temp;//显示当前温度信息
    private Context mContext;//全局可用的context
    public static boolean dayNow = false;//判断当前是否是白天,默认为flase
    private LinearLayout weather_info;//顶部天气信息显示区域
    private JSONObject weather;//传入天气详情界面的json数据
    public static final String WEATHER_GIVE = "weather";
    private LinearLayout main_error;//错误页面
    private bluetoothStateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        //初始化gif图片加载
        Fresco.initialize(mContext);
        //注册广播
        registerBluetoothReceiver();
        initView();
    }

    //注册广播，监听蓝牙状态
    private void registerBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new bluetoothStateReceiver();
        registerReceiver(receiver, intentFilter);
    }

    //初始化视图
    private void initView() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //判断网络连接是否可用
        if (isNetWorkUseful()) {//网络可用，加载正常页面
            loadNormalPage();
        } else {//网络不可用，加载错误页面
            loadErrorPage();
        }
    }

    //当网络连接出错时加载提示页面
    private void loadErrorPage() {
        setContentView(R.layout.activity_main_errorpage);
        main_error = (LinearLayout) findViewById(R.id.main_error);
        initErrorPageListener();
    }

    //当网络连接正常时加载正常的页面
    private void loadNormalPage() {
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(MainActivity.this, R.color.main_color);
        setContentView(R.layout.activity_classes_list_);
        setContentView(R.layout.activity_main);
        main_rg = (RadioGroup) findViewById(R.id.main_rg);
        main_controller_text = (TextView) findViewById(R.id.main_controller_text);
        main_weather_img = (ImageView) findViewById(R.id.main_weather_img);
        main_weather_city = (TextView) findViewById(R.id.main_weather_city);
        main_weather_temp = (TextView) findViewById(R.id.main_weather_temp);
        weather_info = (LinearLayout) findViewById(R.id.weather_info);
        initData();
        initListener();
    }

    //判断当前网络是否可用，返回布尔值
    private boolean isNetWorkUseful() {
        //获取网络连接管理类对象
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {//当前网络不可用
            return false;
        } else {
            return true;
        }
    }

    //初始化数据
    private void initData() {
        manager = getSupportFragmentManager();
        //初始化控制器集合的数据
        mainPages = new ArrayList<baseFragment>();
        mainPages.add(new newsCenterController());
        mainPages.add(new stuClassController());
        mainPages.add(new businessController());
        mainPages.add(new haveFunController());
        //使用add的方式添加碎片
        transaction = manager.beginTransaction();
        for (baseFragment base : mainPages) {
            transaction.add(R.id.fragment_container, base);
        }
        //默认选中第一项,并选中底部控制栏第一项
        main_rg.check(R.id.main_news_rb);
        transaction.show(mainPages.get(0));
        setFragmentToShow(0, transaction);
        //显示天气信息
        initWeatherInfo();
    }

    //设置需要显示的fragment
    private void setFragmentToShow(int position, FragmentTransaction Mytransaction) {
        for (int i = 0; i < mainPages.size(); i++) {
            if (i == position) {
                Mytransaction.show(mainPages.get(position));

            } else {
                Mytransaction.hide(mainPages.get(i));
            }
        }
        Mytransaction.commit();
    }

    //初始化天气信息
    private void initWeatherInfo() {
        LocationClient client = new LocationClient(mContext);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.disableCache(true);//禁止启用缓存定位
        client.setLocOption(option);
        client.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //精确到县级城市
                String district = bdLocation.getDistrict();
                //如果不存在则显示市级城市名称
                if (TextUtils.isEmpty(district)) {
                    main_weather_city.setText(bdLocation.getCity());
                } else {//如果存在，精确到县级
                    main_weather_city.setText(district);
                }
                //获取天气信息并显示
                getWeatherInfo(bdLocation.getCity());
                //将当前城市保存到本地文件
                SharedPreferences preferences = getSharedPreferences(myConstant.SP_NAME, Context
                        .MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(myConstant.CURRENT_CITY, bdLocation.getCity());
                editor.commit();
                //开启服务，后台定时提示天气信息
                Intent intent = new Intent(MainActivity.this, weatherUpdateService.class);
                startService(intent);
            }
        });
        client.start();
    }

    //获取天气信息的json数据
    private void getWeatherInfo(String city) {
        //天气信息请求地址
        try {
            String url = getResources().getString(R.string.bd_weather_url) + URLEncoder.encode(city,
                    "utf-8") + "&output=json&ak=" + getResources().getString(R.string.my_bd_ak)
                    + "&mcode=" + getResources().getString(R.string.bd_mcode);
            requestUtil.jsonRequest(url, new jsonCallback() {
                @Override
                public void getJson(JSONObject response) {
                    weather = response;
                    try {
                        showWeatherInfo(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析天气信息（json）并显示到顶部状态栏
    private void showWeatherInfo(JSONObject jsonObject) throws JSONException {
        int timeNow = Integer.parseInt((String) DateFormat.format("HH", new Date()));
        //判断是夜晚还是白天
        String imgUrl;//天气图片的获取链接
        if (timeNow > 6 && timeNow < 18) {
            dayNow = true;
            imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data")
                    .getJSONObject(0).getString("dayPictureUrl");
        } else {
            dayNow = false;
            imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data")
                    .getJSONObject(0).getString("nightPictureUrl");
        }
        //显示天气图片
        loadImage(imgUrl, main_weather_img);
        //显示温度信息
        String temp = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("weather_data")
                .getJSONObject(0).getString("date");
        main_weather_temp.setText(temp.substring(temp.indexOf("(") + 4, temp.indexOf(")")));
    }

    //加载天气图片
    public static void loadImage(String imgurl, final ImageView img) {
        ImageRequest imgrequest = new ImageRequest(imgurl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                img.setImageBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.requestQueue.add(imgrequest);
    }

    //初始化监听事件
    private void initListener() {
        //监听radioGroup的选中改变事件，实现不同的逻辑
        main_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int currentId = 0;
                transaction = manager.beginTransaction();
                switch (checkedId) {
                    case R.id.main_news_rb:
                        currentId = 0;
                        main_controller_text.setText("新闻中心");
                        break;
                    case R.id.main_cla_rb:
                        currentId = 1;
                        main_controller_text.setText("点到助手");
                        break;
                    case R.id.main_busi_rb:
                        currentId = 2;
                        main_controller_text.setText("跳蚤市场");
                        break;
                    case R.id.main_fun_rb:
                        currentId = 3;
                        main_controller_text.setText("糗事百科");
                        break;
                }
                setFragmentToShow(currentId, transaction);
            }
        });
        //为顶部天气显示区域设置点击事件
        weather_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WeatherDetailActivity.class);
                intent.putExtra(WEATHER_GIVE, weather.toString());
                startActivity(intent);
            }
        });
    }

    //为错误页面设置点击事件
    private void initErrorPageListener() {
        //为错误页面设置点击事件，获取当前网络状况，直到网络可用，加载正常页面
        main_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前网络状况
                if (isNetWorkUseful()) {
                    loadNormalPage();
                    Toast.makeText(mContext, "网络连接已恢复", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //监听回退键，显示对话框
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("点击确定按钮退出");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
