package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.custom.SystemBarTintManager;

/**
 * 初始化界面
 * 2016年2月28日
 * 孙文权
 * 主要实现进入应用时的动画效果
 * 监听动画的状态
 * 在动画结束时，根据sharepreference中判断是否第一次进入的布尔值
 * 来判断进入主界面还是引导界面
 * <p/>
 * 主要技术
 * sharepreference数据存储
 * 安卓三种属性动画的应用
 */
public class SplashActivity extends Activity {
    private ImageView splash_login_bg;//初始化页面背景
    private ImageView splash_login_logo;//初始化页面学校logo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
    }

    //初始化视图界面
    private void initView() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTopColorSameToApp();
        setContentView(R.layout.activity_splash);
        splash_login_bg = (ImageView) findViewById(R.id.splash_login_bg);
        splash_login_logo = (ImageView) findViewById(R.id.splash_login_logo);
    }

    //设置状态栏颜色与app一直
    private void setTopColorSameToApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.splash_top);//通知栏所需颜色
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

    //初始化图片的动画效果
    private void initAnimation() {
        //动画集合
        final AnimationSet aniset = new AnimationSet(true);
        //绕图片中心旋转360度(旋转动画)
        RotateAnimation rotaAni = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotaAni.setDuration(1000);
        rotaAni.setFillAfter(true);
        aniset.addAnimation(rotaAni);
        //透明度动画
        final AlphaAnimation alplaAni = new AlphaAnimation(0, 1);
        alplaAni.setDuration(1000);
        alplaAni.setFillAfter(true);
        aniset.addAnimation(alplaAni);
        //缩放动画
        ScaleAnimation scalAni = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scalAni.setDuration(1000);
        scalAni.setFillAfter(true);
        aniset.addAnimation(scalAni);
        //设置动画监听事件
        aniset.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splash_login_bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(1500);
                alphaAnimation.setFillAfter(true);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        splash_login_logo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //logo动画结束，根据是否第一次打开应用进入不同页面(引导页面或者主页面)
                        SharedPreferences sharedPreferences = getSharedPreferences(myConstant.SP_NAME,
                                Context.MODE_PRIVATE);
                        boolean is_first = sharedPreferences.getBoolean(myConstant.IS_FIRST_LAUNCH, false);
                        if (!is_first) {//第一次启动，进入引导页面
                            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                            startActivity(intent);
                            finish();
                        } else {//进入主页面
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                //背景图片动画效果结束，开启背景logo动画
                splash_login_logo.setAnimation(alphaAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //为背景图片设置监听
        splash_login_bg.startAnimation(aniset);

    }
}
