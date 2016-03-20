package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 添加班级信息的页面
 * 孙文权   2016年3月20日
 * 老师添加班级信息，保存到数据库
 */

public class ClassAddActivity extends Activity {
    private ImageView back;//返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图
    private void initView() {
        //设置状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(ClassAddActivity.this);
        setContentView(R.layout.activity_class_add);
        back = (ImageView) findViewById(R.id.back);
    }

    //初始化数据
    private void initData() {

    }

    //初始化监听事件
    private void initListener() {
        //顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
