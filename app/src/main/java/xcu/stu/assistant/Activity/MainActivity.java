package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.Fragment.controller.businessController;
import xcu.stu.assistant.Fragment.controller.haveFunController;
import xcu.stu.assistant.Fragment.controller.newsCenterController;
import xcu.stu.assistant.Fragment.controller.stuClassController;
import xcu.stu.assistant.R;
import xcu.stu.assistant.application.MyApplication;
import xcu.stu.assistant.custom.SystemBarTintManager;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.requestUtil;
import xcu.stu.assistant.widget.NoscrollViewpager;

/**
 * 程序主界面
 * 顶部状态栏实现系统颜色与app一致
 * 顶部显示当前所在模块
 * 顶部显示当前天气信息
 * 底部使用viewpager与radiogroup结合fragment实现模块切换
 * 2016年3月1日
 * 孙文权
 */
public class MainActivity extends FragmentActivity {
    private NoscrollViewpager main_viewpager;//主页面存放控制器的viewpager
    private ArrayList<baseFragment> mainPages;//存放不同的控制器的集合
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        initView();
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
        setTopColorSameToApp();
        setContentView(R.layout.activity_main);
        main_viewpager = (NoscrollViewpager) findViewById(R.id.main_viewpager);
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
        //初始化控制器集合的数据
        mainPages = new ArrayList<baseFragment>();
        mainPages.add(new newsCenterController());
        mainPages.add(new stuClassController());
        mainPages.add(new businessController());
        mainPages.add(new haveFunController());
        //创建viewpager的适配器
        myPagerAdapter adapter = new myPagerAdapter(getSupportFragmentManager());
        main_viewpager.setAdapter(adapter);
        //默认选中第一项,并选中底部控制栏第一项
        main_rg.check(R.id.main_news_rb);
        main_viewpager.setCurrentItem(0);
        //显示天气信息
        initWeatherInfo();
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
                    Log.d("kydiwen", response.toString());
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
                main_viewpager.setCurrentItem(currentId);
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

    //创建viewpager的适配器
    private class myPagerAdapter extends FragmentPagerAdapter {

        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mainPages.get(position);
        }

        @Override
        public int getCount() {
            return mainPages.size();
        }
    }

    //设置状态栏颜色与app一直
    private void setTopColorSameToApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.main_color);//通知栏所需颜色
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
}