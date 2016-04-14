package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.bitmapUtil;
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
    private  TextView choose_img;//用户选择图片按钮
    private  ImageView user_img;//用户选择要上传的头像
    private  final  int TAKE_PHOTO=0;//拍照
    private  final  int CHOOSE_PHOTO=1;//选取照片
    private  final  int CROP_PHOTO=2;//裁剪照片
    private Uri imgUri;//文件保存路径
    private Dialog dialog;
    private  boolean isImgChoosen=false;
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
        choose_img= (TextView) findViewById(R.id.choose_img);
        user_img= (ImageView) findViewById(R.id.user_img);
    }
    //初始化数据
    private  void  initData(){
        //显示当前所在位置
        location.setVisibility(View.VISIBLE);
        location.setText("用户注册");
        //创建文件夹
        initDirs();
    }
    //创建图片保存路径
    private  void  initDirs(){
        File userDirs=new File(Environment.getExternalStorageDirectory(),"xcustuassistant/user");
        File imgsDirs=new File(Environment.getExternalStorageDirectory(),"xcustuassistant/imgs");
        if(!userDirs.exists()){
            userDirs.mkdirs();
        }
        if(!imgsDirs.exists()){
            imgsDirs.mkdirs();
        }
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
        //为选取图片按钮设置点击事件
        choose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示对话框
                final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                View view=View.inflate(mContext,R.layout.signup_img_choose,null);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                TextView take_photo= (TextView) view.findViewById(R.id.take_photo);
                TextView choose_album= (TextView) view.findViewById(R.id.choose_album);
                ImageView close=  (ImageView) view.findViewById(R.id.close);
                //关闭按钮
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //拍照按钮
                take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file=new File(Environment.getExternalStorageDirectory(),
                                "/xcustuassistant/user/"+"user.jpg");
                        imgUri=Uri.fromFile(file);
                        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
                        startActivityForResult(intent,TAKE_PHOTO);//启动相机拍照
                    }
                });
                //选取图片按钮
                choose_album.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent,CHOOSE_PHOTO);//从相册选取图片
                    }
                });
            }
        });
    }
    //注册方法封装
    private  void  signUP(){
        progressdialogUtil.showDialog(mContext,"正在注册，请稍候...");
        //获取用户输入
        String name=username.getText().toString().trim();
        String pass=password.getText().toString().trim();
        final String pass_again=password_again.getText().toString().trim();
        String phone=phone_number.getText().toString().trim();
        String qq=qqnumber.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pass)||TextUtils.isEmpty(pass_again)
                ||TextUtils.isEmpty(phone)||TextUtils.isEmpty(qq)){
            toastUtil.show(mContext,"信息不全哦");
            progressdialogUtil.cancelDialog();
        }else  if(!pass_again.equals(pass)){
            toastUtil.show(mContext,"两次密码输入不一致，请核对后再试");
            progressdialogUtil.cancelDialog();
        }else if(!isImgChoosen){
            toastUtil.show(mContext,"您没有选择图片哦");
            progressdialogUtil.cancelDialog();
        }else {
            final user mUser=new user();
            mUser.setUsername(name);
            mUser.setPassword(pass);
            mUser.setMobilePhoneNumber(phone);
            mUser.setQqNumber(qq);
            Log.d("kydiwen",imgUri.getPath());
            final BmobFile user_img=new BmobFile(new File(Environment.getExternalStorageDirectory(),
                    "/xcustuassistant/user/user.jpg"));
            user_img.uploadblock(mContext, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    mUser.setUser_img(user_img);
                    //图片上传成功，上传用户注册信息
                    mUser.signUp(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            progressdialogUtil.cancelDialog();
                            toastUtil.show(mContext,"注册成功");
                            //返回数据给登陆界面
                            Intent intent=new Intent();
                            intent.putExtra(USER_RETURNED,mUser);
                            intent.putExtra("pass",pass_again);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toastUtil.show(mContext,"注册失败，请重试");
                            progressdialogUtil.cancelDialog();
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==TAKE_PHOTO){
            if(resultCode==RESULT_OK){
                Intent intent=new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgUri,"image/*");
                intent.putExtra("scale",true);//允许缩放
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);//输出路径
                startActivityForResult(intent,CROP_PHOTO);
            }
        }else  if(requestCode==CHOOSE_PHOTO){
            Uri uri_choose;
            uri_choose=data.getData();//获取选取的图片路径
            File file=new File(Environment.getExternalStorageDirectory(),
                    "/xcustuassistant/user/"+"user.jpg");
            imgUri=Uri.fromFile(file);
            Intent intent=new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri_choose,"image/*");//第一个参数表示要处理图片的路径
            intent.putExtra("scale",true);//允许缩放
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);//输出路径
            startActivityForResult(intent,CROP_PHOTO);
        }else  if(requestCode==CROP_PHOTO){
            try {
                user_img.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream
                            (imgUri)));
                //压缩图片
                bitmapUtil.saveimage(imgUri.getPath());
                dialog.dismiss();
                isImgChoosen=true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}