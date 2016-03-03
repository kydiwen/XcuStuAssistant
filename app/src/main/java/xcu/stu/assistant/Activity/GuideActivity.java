package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.custom.SystemBarTintManager;

/**
 * 引导页面
 * 2016年2月29日
 * 孙文权
 * 主要实现第一次进入应用时对用户进行引导，展示应用的主要功能与特色
 *
 * 主要技术：viewpager的使用
 * 技术难点：在使用setImageBitmap方法为imageview设置图片资源时,在图片过大时出现内存溢出的情况
 * 解决方法：将图片资源放在raw文件夹下，然后使用BitmapFactory.decodeStream(getResources().openRawResource（）
 * 方法，得到bitmap对象
 */
public class GuideActivity extends Activity {
    private ViewPager guide_page;
    private ArrayList<ImageView> imgs;//图片资源
    private TextView leapfrog;//底部跳过提示
    private Button enter_main;//进入主界面按钮

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
        setTopColorSameToApp();
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

    //设置状态栏颜色与app一直
    private void setTopColorSameToApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.guide_button_normal);//通知栏所需颜色
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
                //进入主界面
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
                //进入主界面
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
}
