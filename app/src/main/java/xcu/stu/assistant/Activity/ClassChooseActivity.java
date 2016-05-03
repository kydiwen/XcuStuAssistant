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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;
import xcu.stu.assistant.utils.classesList;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 *
 * 班级选择界面，点击进入点到界面
 * 孙文权  2015年2月25日
 *
 *
 */
public class ClassChooseActivity extends Activity {
    private Context mContext;//全局可用的context
    private ImageView back;//顶部回退按钮
    private ListView classes_list;//班级列表
    private ArrayList<classbean>classes=new ArrayList<classbean>();//班级列表数据
    private  myAdapter adapter;//班级列表数据适配器
    public  static  final  String CLASS_CHOOSED="classchoosed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=ClassChooseActivity.this;
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        color_same_to_app.setTopColorSameToApp(ClassChooseActivity.this,R.color.main_color);
        setContentView(R.layout.activity_class_choose);
        back= (ImageView) findViewById(R.id.back);
        classes_list= (ListView) findViewById(R.id.classes_list);
    }
    //初始化监听事件
    private  void  initListener(){
        //为顶部会退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //为listview设置点击事件
        classes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                classbean bean=classes.get(position);
                Intent intent=new Intent(mContext,RecordActivity.class);
                intent.putExtra(CLASS_CHOOSED,bean);
                startActivity(intent);
            }
        });
    }
    //初始化数据
    private  void  initData(){
        //为班级列表设置适配器
        adapter=new myAdapter();
        classes_list.setAdapter(adapter);
        //查询数据库，获取班级列表数据
       Cursor cursor= classesList.getClasses(mContext);
        cursor.move(-1);
        while (cursor.moveToNext()){
           classbean bean=new classbean();
            String name=cursor.getString(cursor.getColumnIndex(ClassSqliteOpenHelper.CLASS_NAME));
            bean.setClassName(name.substring(0,name.length()));
            bean.setStuNum(cursor.getInt(cursor.getColumnIndex(ClassSqliteOpenHelper.STU_NUM)));
            classes.add(bean);
            adapter.notifyDataSetChanged();
        }
    }
    //班级列表适配器
    class  myAdapter extends BaseAdapter{

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
            TextView name= (TextView) view.findViewById(R.id.class_name);
            TextView num= (TextView) view.findViewById(R.id.class_stu_num);
            name.setText(classes.get(position).getClassName());
            num.setText(classes.get(position).getStuNum()+"人");
            return view;
        }
    }
}
