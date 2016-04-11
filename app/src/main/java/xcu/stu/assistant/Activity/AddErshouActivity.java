package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 *
 * 发布二手商品页面
 * 用户拍摄照片，上传商品图片信息
 * 添加商品描述信息
 * 同时更新用户信息，在用户发布的商品信息字段中添加信息
 * 2016年4月11日
 */
public class AddErshouActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示
    private Context mContext;//全部可用context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=AddErshouActivity.this;
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        color_same_to_app.setTopColorSameToApp(AddErshouActivity.this,R.color.main_color);
        setContentView(R.layout.activity_add_ershou);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
    }
    //初始化数据
    private  void  initData(){
        //显示顶部位置指示
        location.setVisibility(View.VISIBLE);
        location.setText("发布新品");
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
