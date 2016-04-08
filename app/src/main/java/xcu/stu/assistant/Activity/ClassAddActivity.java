package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

/**
 * 添加班级信息的页面
 * 孙文权   2016年3月20日
 * 老师添加班级信息，保存到数据库
 */

public class ClassAddActivity extends Activity {
    private ImageView back;//返回按钮
    private TextView ensure;//确定按钮
    private EditText class_name;//班级名称
    private  EditText class_stu_num;//班级人数
    private Context mContext;
    public  static  final  String NAME="姓名";//学生姓名列
    public  static  final  String NUM="学号";//学生学号列
    public  static  final  String COMED_NUM="已到次数";//已到次数列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=ClassAddActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图
    private void initView() {
        //设置状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(ClassAddActivity.this,R.color.main_color);
        setContentView(R.layout.activity_class_add);
        back = (ImageView) findViewById(R.id.back);
        ensure= (TextView) findViewById(R.id.ensure);
        class_name= (EditText) findViewById(R.id.class_name);
        class_stu_num= (EditText) findViewById(R.id.class_stu_num);
    }

    //初始化数据
    private void initData() {

    }

    //初始化监听事件
    private void initListener() {
        //顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定按钮设置点击事件
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入内容为空,显示提示对话框
                if(TextUtils.isEmpty(class_name.getText().toString())){
                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                    builder.setMessage("请输入班级");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }else {
                    initClass();
                }
            }
        });
    }
    //将输入内容写入数据库并创建学生表
    private  void  initClass(){
        //显示进度条
        progressdialogUtil.showDialog(mContext,"正在保存...");
        //获取用户输入信息
        String className=class_name.getText().toString().trim();
        String num=class_stu_num.getText().toString().trim();
        //获取数据库帮助类
        ClassSqliteOpenHelper helper=ClassSqliteOpenHelper.getInstanse(mContext);
        //获取数据库对象
        SQLiteDatabase database=helper.getWritableDatabase();
        try {
            //启用事务
            database.beginTransaction();
            //插入新班级到班级信息表
            ContentValues values=new ContentValues();
            values.put(ClassSqliteOpenHelper.CLASS_NAME,className);
            values.put(ClassSqliteOpenHelper.STU_NUM, num);
            database.insert(ClassSqliteOpenHelper.CLASSES_TABLE, null, values);
            //创建新的班级表，存放点到信息
            String CREATE_TABLE="create table "+className+"(_id integer primary key autoincrement," +
                    NAME+" text,"+NUM+" integer,"+COMED_NUM+" integer"+")";
            database.execSQL(CREATE_TABLE);
            database.setTransactionSuccessful();
        }finally {
            //关闭事务
            database.endTransaction();
            //关闭数据库
            database.close();
            //隐藏进度条
            progressdialogUtil.cancelDialog();
        }
        finish();
    }
}
