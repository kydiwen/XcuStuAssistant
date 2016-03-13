package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.news;
import xcu.stu.assistant.custom.SystemBarTintManager;

/**
 * 全部新闻信息显示界面，可实现不同类型信息的切换
 */
public class NewsMoreActivity extends Activity {
    private ImageView back;//回退按钮
    private TextView news_type;//新闻类型显示
    private Context mContext;//全局可用的context对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化布局
    private void initView() {
        mContext = NewsMoreActivity.this;
        setTopColorSameToApp();
        setContentView(R.layout.activity_news_more);
        back = (ImageView) findViewById(R.id.back);
        news_type = (TextView) findViewById(R.id.news_type);
    }

    //初始化数据
    private void initData() {
        news newsToLoad = (news) getIntent().getSerializableExtra(myConstant.NEWS_GIVE);
        //显示当前新闻数据类型
        news_type.setText(newsToLoad.getNewsType());
    }

    //初始化监听事件
    private void initListener() {
        //为回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭当前页面
                finish();
            }
        });
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
}
