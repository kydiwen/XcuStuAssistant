package xcu.stu.assistant.Fragment.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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

/**
 * 创建点到助手的控制器
 * 继承自basefFragment,并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class stuClassController extends baseFragment {
    private TextView class_manager;//班级管理
    private TextView scan_device;//开始点到按钮
    private TextView stu_open_bt;//学生信息管理，开启蓝牙按钮
    private TextView scan_record;//学生签到记录
    private BluetoothAdapter adapter;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_stu_class, null);
        class_manager = (TextView) view.findViewById(R.id.class_manager);
        scan_device = (TextView) view.findViewById(R.id.scan_device);
        stu_open_bt = (TextView) view.findViewById(R.id.stu_open_bt);
        scan_record = (TextView) view.findViewById(R.id.scan_record);
        return view;
    }

    @Override
    protected void initData() {
        //检测；蓝牙是否开启
        adapter = BluetoothAdapter.getDefaultAdapter();
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
        //为开始点到按钮设置点击事件
        scan_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.isEnabled()) {
                    //点击进入班级班级选择页面
                    Intent intent = new Intent(mContext, ClassChooseActivity.class);
                    startActivity(intent);
                } else {//蓝牙设备不可用，打开蓝牙
                    Intent bluStartIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluStartIntent, 1);
                }
            }
        });
        //为学生个人信息管理设置点击事件
        stu_open_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入信息设置界面
                if(adapter.isEnabled()){
                    Intent intent = new Intent(mContext, stuMsgSetActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {//蓝牙开启后进入信息设置界面
            Intent intent = new Intent(mContext, stuMsgSetActivity.class);
            startActivity(intent);
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(mContext, ClassChooseActivity.class);
            startActivity(intent);
        }

    }
}
