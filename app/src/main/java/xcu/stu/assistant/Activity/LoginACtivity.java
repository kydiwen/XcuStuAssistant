package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.listener.SaveListener;
import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

/**
 * 登陆界面
 * 实用bmobsdk实现登录功能
 * 孙文权
 * 2016年4月8日
 *
 */

public class LoginACtivity extends Activity {
    private Context mContext;//全局可用的context
    private TextView location;//顶部状态栏显示当前位置
    private EditText username;//用户名输入
    private  EditText password;//密码输入
    private  TextView login;//登录按钮
    private  TextView new_user;//用户注册按钮
    private  final  int LOGIN=0;
    private ImageView back;//顶部回退按钮
    private  user mUser;
    private String name;
    private String pass;
    private SharedPreferences preferences;
    private  TextView use_nologin;//直接进入按钮
    private  TextView pass_for;//忘记密码按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private void initView() {
        mContext=LoginACtivity.this;
        color_same_to_app.setTopColorSameToApp(LoginACtivity.this,R.color.main_color);
        setContentView(R.layout.activity_login_activity);
        location= (TextView) findViewById(R.id.location);
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        login= (TextView) findViewById(R.id.login);
        new_user= (TextView) findViewById(R.id.new_user);
        back= (ImageView) findViewById(R.id.back);
        use_nologin= (TextView) findViewById(R.id.use_nologin);
        pass_for= (TextView) findViewById(R.id.pass_for);
    }
    //初始化数据
    private  void  initData(){
        location.setVisibility(View.VISIBLE);
        location.setText("用户登录");
        preferences = getSharedPreferences(myConstant.SP_NAME, Context.MODE_PRIVATE);
        //读取保存的用户名和密码
        read();
    }
    //初始化监听事件
    private  void  initListener(){
        //登录按钮点击事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入
                name = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
                    toastUtil.show(mContext, "请输入用户名");
                } else if (TextUtils.isEmpty(pass) && !TextUtils.isEmpty(name)) {
                    toastUtil.show(mContext, "请输入密码");
                } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pass)) {
                    toastUtil.show(mContext, "请输入用户名和密码");
                }else {
                    mUser = new user();
                    mUser.setUsername(name);
                    mUser.setPassword(pass);
                    login(mUser);
                }
            }
        });
        //新用户按钮点击事件
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivityForResult(intent, LOGIN);
            }
        });
        //顶部回退按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //未登录方式进入应用
        use_nologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               page_to_load();
            }
        });
        //修改密码按钮
        pass_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,Pass_alert_activity.class);
                startActivity(intent);
            }
        });
    }
    //登录事件方法封装
     private  void  login(user u){
         username.setText(u.getUsername());
         password.setText(pass);
         //显示正在登录提示
             progressdialogUtil.showDialog(mContext, "正在登录，请稍候...");
             u.login(mContext, new SaveListener() {
                 @Override
                 public void onSuccess() {
                     //登录成功，保存密码
                     save();
                     progressdialogUtil.cancelDialog();
                     page_to_load();
                 }

                 @Override
                 public void onFailure(int i, String s) {
                     toastUtil.show(mContext, "登录失败,请重试");
                     progressdialogUtil.cancelDialog();
                 }
             });

     }
    //进入主页或引导页封装
    private  void  page_to_load(){
        SharedPreferences sharedPreferences = getSharedPreferences(myConstant.SP_NAME,
                Context.MODE_PRIVATE);
        boolean is_first = sharedPreferences.getBoolean(myConstant.IS_FIRST_LAUNCH, false);
        if (!is_first) {//第一次启动，进入引导页面
            Intent intent = new Intent(mContext, GuideActivity.class);
            startActivity(intent);
            finish();
        } else {//进入主页面
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==LOGIN&&resultCode==RESULT_OK){
            user myUser= (user) data.getSerializableExtra(SignUpActivity.USER_RETURNED);
            pass=data.getStringExtra("pass");
            name=myUser.getUsername();
            //自动执行登录
            login(myUser);
        }
    }
    //保存用户名和密码
    private  void  save(){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(myConstant.USER,name);
        editor.putString(myConstant.PASSWORD,pass);
        editor.commit();
    }
    //读取用户名和密码
    private  void  read(){
        String myName=preferences.getString(myConstant.USER, "");
        String myPass=preferences.getString(myConstant.PASSWORD,"");
        username.setText(myName);
        password.setText(myPass);
        username.setSelection(myName.length());
    }
}
