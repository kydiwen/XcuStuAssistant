package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.DB.CitySqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.city;
import xcu.stu.assistant.utils.callback.StringCallback;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.requestUtil;

/**
 * 引导页面
 * 2016年2月29日
 * 孙文权
 * 主要实现第一次进入应用时对用户进行引导，展示应用的主要功能与特色
 * <p/>
 * 主要技术：viewpager的使用
 * 技术难点：在使用setImageBitmap方法为imageview设置图片资源时,在图片过大时出现内存溢出的情况
 * 解决方法：将图片资源放在raw文件夹下，然后使用BitmapFactory.decodeStream(getResources().openRawResource（）
 * 方法，得到bitmap对象
 * 2016年3月7日
 * 实现在第一次进入应用时请求全国城市数据，并保存到本地数据库
 */
public class GuideActivity extends Activity {
    private ViewPager guide_page;
    private ArrayList<ImageView> imgs;//图片资源
    private TextView leapfrog;//底部跳过提示
    private Button enter_main;//进入主界面按钮
    private GuideActivity.myAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图
    private void initView() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(GuideActivity.this,R.color.guide_top);
        setContentView(R.layout.activity_classes_list_);
        setContentView(R.layout.activity_guide);
        guide_page = (ViewPager) findViewById(R.id.guide_page);
        leapfrog = (TextView) findViewById(R.id.leapfrog);
        enter_main = (Button) findViewById(R.id.enter_main);
        imgs = new ArrayList<>();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeStream(getResources().openRawResource(R.raw.a)));
        bitmaps.add(BitmapFactory.decodeStream(getResources().openRawResource(R.raw.b)));
        bitmaps.add(BitmapFactory.decodeStream(getResources().openRawResource(R.raw.c)));
        for (int i = 0; i < bitmaps.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmaps.get(i));
            imgs.add(imageView);
        }
    }

    //初始化数据
    private void initData() {
        //为adapter填充数据
        myAdatper adatper = new myAdatper();
        guide_page.setAdapter(adatper);
    }

    //初始化监听事件
    private void initListener() {
        //监听viewpager的滑动事件
        guide_page.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //设置按钮是否可见
                if (position == 2) {
                    enter_main.setVisibility(View.VISIBLE);
                    //设置跳过提示在最后一页不可见
                    leapfrog.setVisibility(View.INVISIBLE);
                } else {
                    enter_main.setVisibility(View.INVISIBLE);
                    leapfrog.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //为按钮设置点击事件
        enter_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将是否第一次进入设置为true
                SharedPreferences preferences = getSharedPreferences(myConstant.SP_NAME, Context
                        .MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(myConstant.IS_FIRST_LAUNCH, true);
                editor.commit();
                //获取城市列表
                myAsyncTask = new myAsyncTask();
                myAsyncTask.execute();
            }
        });
        //为跳过提示信息设置点击事件
        leapfrog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将是否第一次进入设置为true
                SharedPreferences preferences = getSharedPreferences(myConstant.SP_NAME, Context
                        .MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(myConstant.IS_FIRST_LAUNCH, true);
                editor.commit();
                //获取城市列表
                new myAsyncTask().execute();
            }
        });
    }

    private class myAdatper extends PagerAdapter {

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imgs.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //初始化城市数据，网络获取，并保存到数据库
    private void initDB() {
        getAllProvince();
    }

    //获取所有的省份
    private void getAllProvince() {
        //省份请求链接
        String url = getResources().getString(R.string.city_url) + ".xml";
        requestUtil.getString(url, new StringCallback() {
            @Override
            public void StringListener(String response) {
                try {
                    String provinceData = new String(response.getBytes("iso8859-1"), "utf-8");
                    String[] splitProvince = provinceData.split(",");
                    for (String pro : splitProvince) {
                        getAllcity(pro);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取省份下的所有县市
    private void getAllcity(String pro) {
        //当前省份下所有县的省份名
        final String provinceName = pro.split("\\|")[1];
        //获取该省份下所有地级市
        String url = getResources().getString(R.string.city_url) + pro.split("\\|")[0] + ".xml";
        requestUtil.getString(url, new StringCallback() {
            @Override
            public void StringListener(String response) {
                try {
                    //省下所有市
                    String City = new String(response.getBytes("iso8859-1"), "utf-8");
                    String[] splitCity = City.split(",");
                    for (String C : splitCity) {
                        getAllCounty(C, provinceName);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取所有县
    private void getAllCounty(String cityString, final String provinceName) {
        //请求县级城市的链接
        String url = getResources().getString(R.string.city_url) + cityString.split("\\|")[0] + ".xml";
        requestUtil.getString(url, new StringCallback() {

            private SQLiteDatabase database;

            @Override
            public void StringListener(String response) {
                try {
                    //所有县级城市数据
                    String allCity = new String(response.getBytes("iso8859-1"), "utf-8");
                    String[] splitAllCity = allCity.split(",");
                    //获取数据库对象
                    database = CitySqliteOpenHelper.getInstanse(GuideActivity.this)
                            .getWritableDatabase();
                    database.beginTransaction();
                    for (String ci : splitAllCity) {
                        //将城市数据保存到数据库
                        city c = new city();
                        c.setPrivincename(provinceName);
                        c.setCitynum(ci.split("\\|")[0]);
                        c.setCityname(ci.split("\\|")[1]);
                        ContentValues values = new ContentValues();
                        values.put(CitySqliteOpenHelper.PROVINVE_NAME, c.getPrivincename());
                        values.put(CitySqliteOpenHelper.CITY_NAME, c.getCityname());
                        database.insert(CitySqliteOpenHelper.CITYTABLE, null, values);
                    }
                    database.setTransactionSuccessful();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
            }
        });
    }

    //使用asynctask实现数据异步加载
    class myAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            initDB();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(GuideActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
