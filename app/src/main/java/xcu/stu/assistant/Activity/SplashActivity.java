package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import cn.bmob.v3.Bmob;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 初始化界面
 * 2016年2月28日
 * 孙文权
 * 主要实现进入应用时的动画效果
 * 监听动画的状态
 * 在动画结束时，根据sharepreference中判断是否第一次进入的布尔值
 * 来判断进入主界面还是引导界面
 * 主要技术
 * sharepreference数据存储
 * 安卓三种属性动画的应用
 */
public class SplashActivity extends Activity {
    private ImageView splash_login_logo;//初始化页面学校logo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化bmob
        Bmob.initialize(SplashActivity.this, "d25c82dae3a4d656facdcf97ae80d975");
        initView();
        initAnimation();
    }

    //初始化视图界面
    private void initView() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(SplashActivity.this, R.color.splash_top);
        setContentView(R.layout.activity_classes_list_);
        setContentView(R.layout.activity_splash);
        splash_login_logo = (ImageView) findViewById(R.id.splash_login_logo);
    }

    //初始化图片的动画效果
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splash_login_logo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //进入登陆界面
                Intent intent = new Intent(SplashActivity.this, LoginACtivity.class);
                //淡入浅出效果
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //背景图片动画效果结束，开启背景logo动画
        splash_login_logo.setAnimation(alphaAnimation);
    }
}
