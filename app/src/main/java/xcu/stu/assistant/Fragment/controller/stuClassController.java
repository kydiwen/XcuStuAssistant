package xcu.stu.assistant.Fragment.controller;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import xcu.stu.assistant.Activity.ClassChooseActivity;
import xcu.stu.assistant.Activity.ScanRecordActivity;
import xcu.stu.assistant.Activity.classes_list_Activity;
import xcu.stu.assistant.Activity.stuMsgSetActivity;
import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.callback.toastUtil;

/**
 * 创建点到助手的控制器
 * 继承自basefFragment,并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class stuClassController extends baseFragment {
    private TextView class_manager;//班级管理
    private TextView teacher_open_bt;//教师开启蓝牙按钮
    private TextView scan_device;//开始点到按钮
    private TextView stu_open_bt;//学生信息管理，开启蓝牙按钮
    private TextView scan_record;//学生签到记录
    private myReceiver receiver;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_stu_class, null);
        class_manager = (TextView) view.findViewById(R.id.class_manager);
        teacher_open_bt = (TextView) view.findViewById(R.id.teacher_open_bt);
        scan_device = (TextView) view.findViewById(R.id.scan_device);
        stu_open_bt = (TextView) view.findViewById(R.id.stu_open_bt);
        scan_record = (TextView) view.findViewById(R.id.scan_record);
        return view;
    }

    @Override
    protected void initData() {
        //创建班级名称表
        SQLiteDatabase database = ClassSqliteOpenHelper.getInstanse(mContext).getWritableDatabase();
        database.close();
    }

    @Override
    protected void initListener() {
        //为班级管理按钮设置点击事件
        class_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入班级管理页面
                Intent intent = new Intent(mContext, classes_list_Activity.class);
                startActivity(intent);
            }
        });
        //为教师开启蓝牙按钮设置点击事件
        teacher_open_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前蓝牙状态
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter.isEnabled()) {
                    toastUtil.show(mContext, "蓝牙设备已开启");
                } else {
                    //注册广播
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    receiver = new myReceiver();
                    mContext.registerReceiver(receiver, filter);
                    //请求开启蓝牙
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
            }
        });
        //为开始点到按钮设置点击事件
        scan_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入需要点到的班级
                Intent intent = new Intent(mContext, ClassChooseActivity.class);
                startActivity(intent);
            }
        });
        //为学生个人信息管理设置点击事件
        stu_open_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入信息设置界面
                Intent intent = new Intent(mContext, stuMsgSetActivity.class);
                startActivity(intent);
            }
        });
        //为点到记录设置点击事件
        scan_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入点到记录界面
                Intent intent = new Intent(mContext, ScanRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    //创建监听蓝牙状态的广播监听蓝牙状态
    class myReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前监听到的蓝牙状态
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    toastUtil.show(mContext, "蓝牙设备已开启");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    toastUtil.show(mContext, "蓝牙设备已关闭");
                    //注销广播
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
    }
}
