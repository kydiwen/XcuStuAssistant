package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 *
 * 用户密码修改界面
 *
 *
 */
public class Pass_alert_activity extends Activity {
    private ImageView back;//回退按钮
    private TextView location;//顶部指示
    private EditText username;//用户名
    private  EditText phone_number;//手机号码
    private  EditText password;//输入新密码
    private  EditText pass_again;//再次输入密码
    private  EditText verification;
    private Button ensure;//确定按钮
    private Context mContext;//全局可用的context
    private  TextView get_verification;//获取验证码按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        mContext=Pass_alert_activity.this;
        color_same_to_app.setTopColorSameToApp(Pass_alert_activity.this,R.color.main_color);
        setContentView(R.layout.activity_pass_alert_activity);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
        username= (EditText) findViewById(R.id.username);
        phone_number= (EditText) findViewById(R.id.phone_number);
        password= (EditText) findViewById(R.id.password);
        pass_again= (EditText) findViewById(R.id.pass_again);
        ensure= (Button) findViewById(R.id.ensure);
        get_verification= (TextView) findViewById(R.id.get_verification);
        verification= (EditText) findViewById(R.id.verification);
    }
    //初始化数据
    private  void  initData(){
        //显示当前位置
        location.setVisibility(View.VISIBLE);
        location.setText("密码修改");
    }
    //初始化监听事件
    private  void  initListener(){
        //返回按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定按钮点击事件
       ensure.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //判断信息输入是否完整
                if(TextUtils.isEmpty(phone_number.getText().toString())||TextUtils.isEmpty(verification
                        .getText().toString())||TextUtils.isEmpty(password.getText().toString())||TextUtils
                        .isEmpty(pass_again.getText().toString())){
                    toastUtil.show(mContext,"信息不全哦");
                }else {
                    if(TextUtils.isEmpty(phone_number.getText().toString())){
                        toastUtil.show(mContext,"请输入手机号码");
                    }else  if(TextUtils.isEmpty(verification.getText().toString())){
                        toastUtil.show(mContext,"请输入验证码");
                    }else  if(TextUtils.isEmpty(pass_again.getText().toString())||TextUtils.isEmpty
                            (password.getText().toString())){
                        toastUtil.show(mContext,"密码要输入两次哦");
                    }else {//验证并修改密码
                        BmobUser.resetPasswordBySMSCode(mContext, verification.getText().toString(), password
                                .getText().toString(), new ResetPasswordByCodeListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toastUtil.show(mContext,"密码修改成功");
                                    finish();
                                }else {
                                    if(e.toString().contains("code error")){
                                        toastUtil.show(mContext,"验证码错误");
                                    }else {
                                        toastUtil.show(mContext,"您尚未注册，请核对后再试");
                                    }
                                }
                            }
                        });
                    }
                }
           }
       });
          //获取验证码按钮
        get_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phone_number.getText().toString())){
                    toastUtil.show(mContext,"请输入手机号码");
                }else {//获取验证码
                    BmobSMS.requestSMSCode(mContext, phone_number.getText().toString(), "许院学梓助手", new RequestSMSCodeListener() {

                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e==null){
                                toastUtil.show(mContext,"验证码已发送");
                            }
                        }
                    });
                }
            }
        });

    }
}
