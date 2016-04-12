package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import xcu.stu.assistant.R;

/**
 * 商品类别选择页面
 * 选择商品分类，返回给商品发布页面
 * 2016年4月12日
 * 孙文权
 */
public class CategeryChooseActivity extends Activity {
    private Context mContext;//全局可用Activity
    private ListView categery;
    private  String[]data={
            "手机",
            "电脑",
            "数码",
            "书籍",
            "运动",
            "美妆",
            "衣服",
            "其他"
    };
    private ArrayAdapter<String>adapter;
    public  static  final  String CATEGERY_RETURNED="返回的类别";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化试图界面
    private void initView() {
        mContext=CategeryChooseActivity.this;
        setContentView(R.layout.activity_categery_choose);
        categery = (ListView) findViewById(R.id.categery);
    }

    //初始化数据
    private void initData() {
        adapter=new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,data);
        categery.setAdapter(adapter);
    }

    //初始化监听事件
    private void initListener() {
        categery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra(CATEGERY_RETURNED,data[position]);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
