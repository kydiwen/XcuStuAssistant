package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.classesList;
import xcu.stu.assistant.utils.color_same_to_app;

//显示班级列表以及班级人数，点击可查看点到记录
public class classes_list_Activity extends Activity {
    private ImageView back;//返回按钮
    private ImageView add_class;//添加班级
    private FrameLayout class_list_container;//布局容器，显示班级列表或者暂无班级信息
    private Context mContext;//全局可用的context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = classes_list_Activity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(classes_list_Activity.this);
        setContentView(R.layout.activity_classes_list_);
        back = (ImageView) findViewById(R.id.back);
        class_list_container = (FrameLayout) findViewById(R.id.class_list_container);
        add_class = (ImageView) findViewById(R.id.add_class);
    }

    //初始化数据
    private void initData() {
        //获取查询班级列表数据库结果
        Cursor classCursor = classesList.getClasses(mContext);
        if (!classCursor.moveToNext()) {//数据为空
            initDataNull();
        } else {//班级数据存在
            initClassData(classCursor);
        }
    }

    //初始化监听事件
    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //为添加班级信息按钮设置点击事件
        add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入班级添加页面
                Intent intent = new Intent(mContext, ClassAddActivity.class);
                startActivity(intent);
                //销毁当前页面
                finish();
            }
        });
    }

    //当班级信息没空时与有数据时加载不同的布局
    //加载班级为空时的数据
    private void initDataNull() {
        //加载布局
        View nullView = View.inflate(mContext, R.layout.class_null, null);
        class_list_container.addView(nullView);
    }

    //加载有班级数据时的布局
    private void initClassData(Cursor cursor) {
        //加载布局
        View dataView = View.inflate(mContext, R.layout.classes_list, null);
        class_list_container.addView(dataView);
    }
}
