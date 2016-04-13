package xcu.stu.assistant.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import xcu.stu.assistant.Fragment.business_page.myErshousFragent;
import xcu.stu.assistant.Fragment.business_page.myNeedsFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 我的二手商品显示界面
 * 2016年4月11日
 */
public class MyErshouActivity extends FragmentActivity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示
    private Context mContext;//全局可用的context
    private RadioGroup my_ershou_rg;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private myErshousFragent myershous;
    private myNeedsFragment myneeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MyErshouActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(MyErshouActivity.this, R.color.main_color);
        setContentView(R.layout.activity_my_ershou);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        my_ershou_rg = (RadioGroup) findViewById(R.id.my_ershou_rg);
    }

    //初始化数据
    private void initData() {
        manager = getSupportFragmentManager();
        myershous = new myErshousFragent();
        myneeds = new myNeedsFragment();
        //显示当前位置
        location.setVisibility(View.VISIBLE);
        location.setText("个人商品管理");
        //默认默认选中第一项
        my_ershou_rg.check(R.id.rb_my_ershous);
        transaction = manager.beginTransaction();
        //添加fragment
        transaction.add(R.id.goods_container, myershous);
        transaction.add(R.id.goods_container, myneeds);
        transaction.show(myershous);
        transaction.hide(myneeds);
        transaction.commit();
    }

    //初始化监听事件
    private void initListener() {
        //监听radiogroup的选择事件
        my_ershou_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_my_ershous:
                        transaction = manager.beginTransaction();
                        transaction.show(myershous);
                        transaction.hide(myneeds);
                        transaction.commit();
                        break;
                    case R.id.rb_my_needs:
                        transaction = manager.beginTransaction();
                        transaction.show(myneeds);
                        transaction.hide(myershous);
                        transaction.commit();
                        break;
                }
            }
        });
    }
}
