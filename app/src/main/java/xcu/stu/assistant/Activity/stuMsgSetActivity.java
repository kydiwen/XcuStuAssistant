package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 学生个人信息设置界面
 * 即设置蓝牙信息
 * 孙文权  2015年3月25日
 */
public class stuMsgSetActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private EditText name;//学生姓名
    private  EditText num;//学生学号
    private Button open_bluetooth;//开启蓝牙按钮
    private BluetoothAdapter adapter;//蓝牙设备对象
    private Context mContext;//全局可用的context
    private SharedPreferences preferences;//用来保存用户输入，实现记住密码效果
    private  Button scan_success;//签到成功按钮，点击关闭蓝牙并保存点到记录
    private  EditText class_name;//课程名称
    private TextView location;//顶部显示当前位置
    private  EditText command;
    private  String scan_command;//点到口令

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(stuMsgSetActivity.this, R.color.main_color);
        setContentView(R.layout.activity_stu_msg_set);
        back = (ImageView) findViewById(R.id.back);
        name= (EditText) findViewById(R.id.name);
        num= (EditText) findViewById(R.id.num);
        open_bluetooth= (Button) findViewById(R.id.open_bluetooth);
        scan_success= (Button) findViewById(R.id.scan_success);
        class_name= (EditText) findViewById(R.id.class_name);
        location= (TextView) findViewById(R.id.location);
        command= (EditText) findViewById(R.id.command);
    }

    //初始化数据
    private void initData() {
        location.setVisibility(View.VISIBLE);
        location.setText("个人信息");
        mContext=stuMsgSetActivity.this;
        adapter=BluetoothAdapter.getDefaultAdapter();
        preferences=mContext.getSharedPreferences(myConstant.SP_NAME,Context.MODE_PRIVATE);
        //保存的姓名信息存在时，自动输入
        if(!TextUtils.isEmpty(preferences.getString(myConstant.STU_NAME,""))){
            name.setText(preferences.getString(myConstant.STU_NAME,""));
            num.setText(preferences.getString(myConstant.STU_NUM,""));
        }
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
        //为开启蓝牙按钮设置点击事件
        open_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置口令
                scan_command=command.getText().toString();
                //获取输入的姓名和学好信息
                String stu_name=name.getText().toString().trim();
                String stu_num=num.getText().toString().trim();
                //保存输入信息
                //编辑器对象
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(myConstant.STU_NAME,stu_name);
                editor.putString(myConstant.STU_NUM,stu_num);
                editor.commit();
                //判断输入是否为空
                if (TextUtils.isEmpty(stu_name)||TextUtils.isEmpty(stu_num)) {
                    //弹出对话框提示用户输入
                    toastUtil.show(mContext,"请输入姓名和学号");
                }else {
                    String bt_name=stu_name+scan_command+stu_num;//蓝牙名称
                    //设置蓝牙设备名称
                    adapter.setName(bt_name);
                    //设置蓝牙设备可被发现
                    Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                    startActivity(intent);
                }
            }
        });
        //点到成功按钮
        scan_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cource=class_name.getText().toString().trim();
                if(TextUtils.isEmpty(cource)){
                    toastUtil.show(mContext,"请输入课程名称");
                }else {
                    //获取当前时间
                    Date date=new Date();
                    String time= DateFormat.format("yy/MM/dd HH:mm",date).toString();
                    //保存到数据库
                    ClassSqliteOpenHelper helper=ClassSqliteOpenHelper.getInstanse(mContext);
                    SQLiteDatabase database=helper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put(ClassSqliteOpenHelper.COURSENAME,cource);
                    values.put(ClassSqliteOpenHelper.SCAN_TIME,time);
                    database.insert(ClassSqliteOpenHelper.STU_RECORD, null, values);
                    database.close();
                    //关闭蓝牙
                    adapter.disable();
                    finish();
                }
            }
        });
    }
}
