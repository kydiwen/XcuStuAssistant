package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.listener.SaveListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

/**
 *
 * 新用户注册界面
 * 新用户输入用户信息
 * bmob获取用户信息上传到bmob后台
 * 孙文权
 * 2016年4月8日
 */
public class SignUpActivity extends Activity {
    private Context mContext;//全局可用的context
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置提示
    private EditText username;//用户输入框
    private  EditText password;//密码输入框
    private  EditText password_again;//再次输入密码框
    private  EditText phone_number;//手机号码输入框
    private  EditText qqnumber;//qq号码输入框
    private  TextView ensure;//确定按钮
    public static final  String USER_RETURNED="返回新注册用户";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        mContext=SignUpActivity.this;
        color_same_to_app.setTopColorSameToApp(SignUpActivity.this, R.color.main_color);
        setContentView(R.layout.activity_sign_up);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        password_again= (EditText) findViewById(R.id.password_again);
        phone_number= (EditText) findViewById(R.id.phone_number);
        qqnumber= (EditText) findViewById(R.id.qqnumber);
        ensure= (TextView) findViewById(R.id.ensure);
    }
    //初始化数据
    private  void  initData(){
        //显示当前所在位置
        location.setVisibility(View.VISIBLE);
        location.setText("用户注册");
    }
    //初始化监听事件
    private  void  initListener(){
        //顶部回退按钮监听事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定按钮监听事件
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUP();
            }
        });
    }
    //注册方法封装
    private  void  signUP(){
        progressdialogUtil.showDialog(mContext,"正在注册，请稍候...");
        //获取用户输入
        String name=username.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String pass_again=password_again.getText().toString().trim();
        String phone=phone_number.getText().toString().trim();
        String qq=qqnumber.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pass)||TextUtils.isEmpty(pass_again)
                ||TextUtils.isEmpty(phone)||TextUtils.isEmpty(qq)){
            toastUtil.show(mContext,"信息不全哦");
            progressdialogUtil.cancelDialog();
        }else  if(!pass_again.equals(pass)){
            toastUtil.show(mContext,"两次密码输入不一致，请核对后再试");
            progressdialogUtil.cancelDialog();
        }else {
            final user mUser=new user();
            mUser.setUsername(name);
            mUser.setPassword(pass);
            mUser.setMobilePhoneNumber(phone);
            mUser.setQqNumber(qq);
            mUser.signUp(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    progressdialogUtil.cancelDialog();
                    toastUtil.show(mContext,"注册成功");
                    //返回数据给登陆界面
                    Intent intent=new Intent();
                    intent.putExtra(USER_RETURNED,mUser);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    toastUtil.show(mContext,"用户名已被占用");
                    progressdialogUtil.cancelDialog();
                }
            });
        }
    }
}