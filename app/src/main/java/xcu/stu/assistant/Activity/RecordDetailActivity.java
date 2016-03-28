package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 *
 * 点到详情页面
 * 显示点到每个人记录
 * 孙文权  2015年3月25日
 *
 */
public class RecordDetailActivity extends Activity {

    private Activity mContext;
    private ArrayList<classbean>datas=new ArrayList<classbean>();//数据
    private  myAdapter adapter;//适配器对象
    private ListView record_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext=RecordDetailActivity.this;
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图
    private  void  initView(){
        color_same_to_app.setTopColorSameToApp(mContext,R.color.main_color);
        setContentView(R.layout.activity_record_detail);
        record_list= (ListView) findViewById(R.id.record_list);
    }
    //初始化数据
    private  void  initData(){
        adapter=new myAdapter();
        //设置适配器
        record_list.setAdapter(adapter);
        //获取传递的数据
        Intent intent=getIntent();
        classbean bean= (classbean) intent.getSerializableExtra(classes_list_Activity.CLASS_CLICKED);
        ClassSqliteOpenHelper helper=ClassSqliteOpenHelper.getInstanse(this);
        SQLiteDatabase database=helper.getWritableDatabase();
        Cursor cursor=database.query(bean.getClassName(), null, null, null, null, null, null);
        cursor.move(-1);
        while (cursor.moveToNext()){
            classbean classbean=new classbean();
            bean.setClassName(cursor.getString(cursor.getColumnIndex(ClassAddActivity.NAME))+"         "+
                    cursor.getString(cursor.getColumnIndex(ClassAddActivity.NUM)));
            bean.setStuNum(cursor.getInt(cursor.getColumnIndex(ClassAddActivity.COMED_NUM)));
            datas.add(bean);
            adapter.notifyDataSetChanged();
        }
    }
    //初始化监听事件
    private  void  initListener(){

    }
    //listview适配器
    class  myAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
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
            name.setText(datas.get(position).getClassName());
            num.setText(datas.get(position).getStuNum()+"");
            return view;
        }
    }
}
