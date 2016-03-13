package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.news;

/**
 * 新闻数据详情页面
 * 显示详细新闻信息
 * 根据当前新闻信息类型
 * 加载普通文字信息，图片信息，以及视频信息
 * 2016年3月13日
 */
public class NewsDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    //初始化视图界面
    private void initView() {
        setContentView(R.layout.activity_news_detail);
    }

    //初始化数据
    private void initData() {
        //判断传入的是普通类型新闻数据，还是图片新闻数据
        news newsToLoad = (news) getIntent().getSerializableExtra(myConstant.NEWS_GIVE);
        if (!TextUtils.isEmpty(newsToLoad.getImgUrl())) {//图片连接存在
            Log.d("kydiwen", "这是图片新闻");
        } else {//图片链接不存在
            Log.d("kydiwen", "这是普通新闻");
        }
    }
}
