package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

/**
 * 点到界面
 * 扫描蓝牙信息，操作数据库，记录点到情况
 * 孙文权  2015年3月25日
 */
public class RecordActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部班级标示栏
    private ClassSqliteOpenHelper helper;//数据库帮助类
    private SQLiteDatabase database;//数据库对象
    private Context mContext;//全局可用的context
    private TextView start;//开始点到按钮
    private TextView end;//结束点到按钮
    private ListView stu_scaned_list;//点到结果列表
    private ArrayList<BluetoothDevice> students = new ArrayList<BluetoothDevice>();//点到结果数据
    private BluetoothAdapter adapter;//蓝牙适配器对象
    private RecordActivity.myAdapter myAdapter;//数据显示适配器
    private classbean bean;
    private myReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图
    private void initView() {
        mContext = RecordActivity.this;
        color_same_to_app.setTopColorSameToApp(RecordActivity.this, R.color.main_color);
        setContentView(R.layout.activity_record);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end);
        stu_scaned_list = (ListView) findViewById(R.id.stu_scaned_list);
    }

    //初始化数据
    private void initData() {
        //初始化数据库操作对象
        helper = ClassSqliteOpenHelper.getInstanse(mContext);
        database = helper.getWritableDatabase();
        //初始化蓝牙适配器对象
        adapter = BluetoothAdapter.getDefaultAdapter();
        //获取传递的数据
        Intent intent = getIntent();
        bean = (classbean) intent.getSerializableExtra(ClassChooseActivity.CLASS_CHOOSED);
        location.setVisibility(View.VISIBLE);
        location.setText(bean.getClassName());
        //为扫描结果列表设置适配器
        myAdapter = new myAdapter();
        stu_scaned_list.setAdapter(myAdapter);
    }

    //初始化监听事件
    private void initListener() {
        //为顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //开始点到按钮点击事件
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //注册广播，监听扫描到的蓝牙设备
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                    receiver=new myReceiver();
                    registerReceiver(receiver, intentFilter);
                    adapter.startDiscovery();//开始扫描设备
                //弹出进度条提示
                progressdialogUtil.showDialog(mContext);
            }
        });
        //结束点到按钮设置点击事件
        end.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请认真核对点到信息");
                builder.setCancelable(false);
                //设置布局
                View view = View.inflate(mContext, R.layout.record_dialog, null);
                TextView stu_should = (TextView) view.findViewById(R.id.stu_should);//应到人数
                TextView stu_actual = (TextView) view.findViewById(R.id.stu_actual);//实到人数
                TextView proportion = (TextView) view.findViewById(R.id.proportion);//出勤率
                //设置数据
                stu_should.setText(bean.getStuNum() + "");
                stu_actual.setText(students.size() + "");
                proportion.setText(String.valueOf(100 * ((double) (students.size()) / (double) bean
                        .getStuNum())) + "%");
                //格式化数据
                DecimalFormat df = new DecimalFormat("#.00");
                String pro = df.format(100 * (double) (students.size()) / (double) bean.getStuNum());
                proportion.setText(pro + "%");
                builder.setView(view);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存数据到数据库
                                //数据库帮助类
                                ClassSqliteOpenHelper helper = ClassSqliteOpenHelper.getInstanse(mContext);
                                //获取数据库对象
                                SQLiteDatabase database = helper.getWritableDatabase();
                                //遍历所有数据
                                for (int i = 0; i < students.size(); i++) {
                                    String select = "select * from " + bean.getClassName() + " where " +
                                            "" + ClassAddActivity.NAME + "=" + "'" + students.get(i)
                                            .getName().split(":")[0] + "'";
                                    Cursor cursor = database.rawQuery(select, null);
                                    if (cursor.moveToNext()) {//数据已添加
                                        int come = cursor.getInt(cursor.getColumnIndex(ClassAddActivity
                                                .COMED_NUM));
                                        come++;
                                        ContentValues values=new ContentValues();
                                        values.put(ClassAddActivity.COMED_NUM, come);
                                        //更新数据
                                        database.update(bean.getClassName(), values, ClassAddActivity
                                                        .NAME+"=?", new String[]{students.get(i).getName()
                                                .split(":")[0]});
                                    } else {
                                        ContentValues values = new ContentValues();
                                        //插入新数据
                                        values.put(ClassAddActivity.NAME, students.get(i).getName().split(":")
                                                [0]);
                                        values.put(ClassAddActivity.NUM, students.get(i).getName().split
                                                (":")[1]);
                                        values.put(ClassAddActivity.COMED_NUM, 1);
                                        database.insert(bean.getClassName(), null, values);
                                    }
                                }
                                database.close();
                                finish();
                            }
                        }

                );
                    builder.create().

                    show();
                }
            });
    }

    //点到结果列表适配器
    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return students.size();
        }

        @Override
        public Object getItem(int position) {
            return students.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.classes_item, null);
            //显示姓名
            TextView name = (TextView) view.findViewById(R.id.class_name);
            //显示学号
            TextView num = (TextView) view.findViewById(R.id.class_stu_num);
            //显示数据
            String name_num = students.get(position).getName();
            name.setText(name_num.split(":")[0]);
            num.setText(name_num.split(":")[1]);
            return view;
        }
    }

    //监听蓝牙状态和扫描到的蓝牙设备变化的广播接收器
    class myReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == BluetoothDevice.ACTION_FOUND) {
                progressdialogUtil.cancelDialog();
                //扫描到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //判断是否是指定命名
                    if(device.getName().split(":").length==2){
                        //防止数据重复
                        if(!students.contains(device)){
                            students.add(device);
                            myAdapter.notifyDataSetChanged();
                        }
                    }
            }
        }
    }
}
