package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;
import xcu.stu.assistant.utils.classesList;
import xcu.stu.assistant.utils.color_same_to_app;

//显示班级列表以及班级人数，点击可查看点到记录
public class classes_list_Activity extends Activity {
    private ImageView back;//返回按钮
    private  TextView location;
    private ImageView add_class;//添加班级
    private FrameLayout class_list_container;//布局容器，显示班级列表或者暂无班级信息
    private Context mContext;//全局可用的context
    private ArrayList<classbean>classes=new ArrayList<classbean>();//班级列表数据
    private  ClassListAdapter adapter=new ClassListAdapter();//班级列表适配器
    private ListView classes_list;
    public  static  final  String CLASS_CLICKED="class_clicked";

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
        color_same_to_app.setTopColorSameToApp(classes_list_Activity.this,R.color.main_color);
        setContentView(R.layout.activity_classes_list_);
        back = (ImageView) findViewById(R.id.back);
        class_list_container = (FrameLayout) findViewById(R.id.class_list_container);
        add_class = (ImageView) findViewById(R.id.add_class);
        location= (TextView) findViewById(R.id.location);
    }

    //初始化数据
    private void initData() {
        location.setVisibility(View.VISIBLE);
        location.setText("班级管理");
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
        classes_list = (ListView) dataView.findViewById(R.id.classes_list);
        classes_list.setAdapter(adapter);
        cursor.move(-1);
        while (cursor.moveToNext()){
            classbean bean=new classbean();
            bean.setClassName(cursor.getString(cursor.getColumnIndex(ClassSqliteOpenHelper.CLASS_NAME)));
            bean.setStuNum(cursor.getInt(cursor.getColumnIndex(ClassSqliteOpenHelper.STU_NUM)));
            classes.add(bean);
            adapter.notifyDataSetChanged();
        }
        class_list_container.addView(dataView);
        //为班级列表设置点击事件
        classes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                classbean bean=classes.get(position);
                //点击进入详情页面，显示点到记录详情
                Intent intent=new Intent(mContext,RecordDetailActivity.class);
                intent.putExtra(CLASS_CLICKED,bean);
                startActivity(intent);
            }
        });
    }
    //班级列表适配器
    class ClassListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return classes.size();
        }

        @Override
        public Object getItem(int position) {
            return classes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(mContext,R.layout.classes_item,null);
            TextView class_name= (TextView) view.findViewById(R.id.class_name);
            TextView class_num= (TextView) view.findViewById(R.id.class_stu_num);
            class_name.setText(classes.get(position).getClassName());
            class_num.setText(classes.get(position).getStuNum()+"人");
            return view;
        }
    }
}
