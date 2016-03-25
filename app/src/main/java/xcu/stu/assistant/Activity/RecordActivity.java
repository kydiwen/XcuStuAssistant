package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 *
 * 点到界面
 * 扫描蓝牙信息，操作数据库，记录点到情况
 * 孙文权  2015年3月25日
 *
 *
 */
public class RecordActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部班级标示栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图
    private  void  initView(){
        color_same_to_app.setTopColorSameToApp(RecordActivity.this,R.color.main_color);
        setContentView(R.layout.activity_record);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
    }
    //初始化数据
    private  void  initData(){
        //获取传递的数据
        Intent intent=getIntent();
        classbean bean= (classbean) intent.getSerializableExtra(ClassChooseActivity.CLASS_CHOOSED);
        location.setVisibility(View.VISIBLE);
        location.setText(bean.getClassName());
    }
    //初始化监听事件
    private  void  initListener(){
        //为顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
