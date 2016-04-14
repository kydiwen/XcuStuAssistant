package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 商品详情页面
 * 显示商品详细
 *2016年4月14日
 * 孙文权
 */
public class GoodsDetailActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示按钮
    private goods currentGood;//当前需要显示的商品

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        color_same_to_app.setTopColorSameToApp(GoodsDetailActivity.this,R.color.main_color);
        setContentView(R.layout.activity_goods_detail);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
    }
    //初始化数据
    private  void  initData(){
        //显示顶部位置指示
        location.setVisibility(View.VISIBLE);
        location.setText("商品详情");
        //获取需要显示的商品
        currentGood= (goods) getIntent().getSerializableExtra(GoodsTypeActivity.GOODS_ITEM);
        Log.d("kydiwen",currentGood.getTitle());
    }
    //初始化监听事件
    private  void  initListener(){
        //顶部回退按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
