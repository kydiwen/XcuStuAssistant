package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.os.Bundle;

import xcu.stu.assistant.R;

/**
 *
 * 城市选择界面
 * 可显示全国城市三级菜单
 * 2016年3月5日
 *
 *
 */
public class CityListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        initView();
        initData();
        initListener();
    }
    //初始化视图
    private void initView() {
    }
    //初始化数据
    private  void  initData(){

    }
    //初始化监听事件
    private  void  initListener(){

    }
}
