package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.scanRecord;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 学生个人点到记录界面
 * 记录学生的点到情况
 * 孙文权  2015年3月25日
 */
public class ScanRecordActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部显示当前位置
    private ListView scan_record;//点到记录列表
    private Context mContext;//全局可用的context
    private ArrayList<scanRecord>records= new ArrayList<scanRecord>();//点到记录数据
    private myAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(ScanRecordActivity.this,R.color.main_color);
        setContentView(R.layout.activity_scan_record);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
        scan_record= (ListView) findViewById(R.id.scan_record);
    }

    //初始化数据
    private void initData() {
        mContext = ScanRecordActivity.this;
        location.setVisibility(View.VISIBLE);
        location.setText("点到记录");
        //设置适配器
        adapter = new myAdapter();
        scan_record.setAdapter(adapter);
        //添加数据
        ClassSqliteOpenHelper helper = ClassSqliteOpenHelper.getInstanse(mContext);
        SQLiteDatabase database=helper.getWritableDatabase();
        Cursor cursor=database.query(ClassSqliteOpenHelper.STU_RECORD,null,null,null,null,null,null);
        if(!cursor.moveToNext()){
            toastUtil.show(mContext,"暂无数据");
        }else {
            cursor.move(-1);
            while (cursor.moveToNext()){
                scanRecord record=new scanRecord();
                record.setcource(cursor.getString(cursor.getColumnIndex(ClassSqliteOpenHelper.COURSENAME)));
                record.settime(cursor.getString(cursor.getColumnIndex(ClassSqliteOpenHelper.SCAN_TIME)));
                records.add(record);
                adapter.notifyDataSetChanged();
            }
        }
    }
    //初始化监听事件
    private void initListener() {
        //为顶部会退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //点到记录列表适配器
    class  myAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(mContext,R.layout.classes_item,null);
            TextView cource= (TextView) view.findViewById(R.id.class_name);
            TextView time= (TextView) view.findViewById(R.id.class_stu_num);
            cource.setText(records.get(position).getcource());
            time.setText(records.get(position).gettime());
            return view;
        }
    }
}
