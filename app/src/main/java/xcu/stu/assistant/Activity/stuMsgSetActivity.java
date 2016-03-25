package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 学生个人信息设置界面
 * 即设置蓝牙信息
 * 孙文权  2015年3月25日
 */
public class stuMsgSetActivity extends Activity {
    private ImageView back;//顶部回退按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(stuMsgSetActivity.this, R.color.main_color);
        setContentView(R.layout.activity_stu_msg_set);
        back = (ImageView) findViewById(R.id.back);
    }

    //初始化数据
    private void initData() {

    }

    //初始化监听事件
    private void initListener() {
        //为顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
