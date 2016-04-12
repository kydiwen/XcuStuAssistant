package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

/**
 * 发布二手商品页面
 * 用户拍摄照片，上传商品图片信息
 * 添加商品描述信息
 * 同时更新用户信息，在用户发布的商品信息字段中添加信息
 * 2016年4月11日
 */
public class AddErshouActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示
    private Context mContext;//全部可用context
    private LinearLayout img_container;//图片容器
    private TextView choose_img;//选择图片按钮
    private TextView categary;//选择分类
    private EditText title;//标题信息
    private EditText price;//价格信息
    private EditText describe;//商品描述信息
    private EditText transactionplace;//交易地点
    private EditText phone;//手机号码
    private EditText qqnumber;//手机号码
    private EditText micromsg;//微信号码
    private TextView ensure;//确定按钮
    private AlertDialog dialog;
    private ArrayList<Uri> imgUris = new ArrayList<Uri>();//图片路径
    private Uri imgUri;//当前操作图片的路径
    private final static int TAKE_PHOTO = 0;//拍照
    private final static int CHOOSE_PHOTO = 1;//从相册选取
    private final static int CROP_PHOTO = 2;//裁剪照片
    private boolean isImgChoosen = false;//是否已经选取照片
    private final static int CATEGERY_CHOOSE = 3;//选择商品类别
    private user me;//当前登陆的用户，用来上传
    private List<String> current_ershou;//我的二手商品列表
    private boolean isCategeryChoosed = false;//判断是否选择了类别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = AddErshouActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(AddErshouActivity.this, R.color.main_color);
        setContentView(R.layout.activity_add_ershou);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        img_container = (LinearLayout) findViewById(R.id.img_container);
        choose_img = (TextView) findViewById(R.id.choose_img);
        categary = (TextView) findViewById(R.id.categary);
        title = (EditText) findViewById(R.id.title);
        price = (EditText) findViewById(R.id.price);
        describe = (EditText) findViewById(R.id.describe);
        transactionplace = (EditText) findViewById(R.id.transactionplace);
        phone = (EditText) findViewById(R.id.phone);
        qqnumber = (EditText) findViewById(R.id.qqnumber);
        micromsg = (EditText) findViewById(R.id.micromsg);
        ensure = (TextView) findViewById(R.id.ensure);
    }

    //初始化数据
    private void initData() {
        //提示用户等待
        progressdialogUtil.showDialog(mContext, "请稍候...");
        //显示顶部位置指示
        location.setVisibility(View.VISIBLE);
        location.setText("发布新品");
        //获取当前用户实例
        queryCurrentUser();
    }

    //获取当前用户实例
    private void queryCurrentUser() {
        BmobQuery<user> query = new BmobQuery<user>();
        query.getObject(mContext, BmobUser.getCurrentUser(mContext).getObjectId(), new GetListener<user>() {
            @Override
            public void onSuccess(user user) {
                me = user;
                //获取到当前用户实例后，获取当前所发布的商品列表
                current_ershou = me.getMy_ershou();
                progressdialogUtil.cancelDialog();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
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
        //选择图片按钮点击事件
        choose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示对话框
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View view = View.inflate(mContext, R.layout.signup_img_choose, null);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                TextView take_photo = (TextView) view.findViewById(R.id.take_photo);
                TextView choose_album = (TextView) view.findViewById(R.id.choose_album);
                ImageView close = (ImageView) view.findViewById(R.id.close);
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
                        String path = DateFormat.format("yyyyMMddhhmmss", new Date()).toString();
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/xcustuassistant/imgs/" + path + ".jpg");
                        imgUri = Uri.fromFile(file);
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                        startActivityForResult(intent, TAKE_PHOTO);//启动相机拍照
                    }
                });
                //选取图片按钮
                choose_album.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_PHOTO);//从相册选取图片
                    }
                });
            }
        });
        //确定按钮点击事件
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定，开始上传
                if (!isImgChoosen) {//未选择图片
                    toastUtil.show(mContext, "请至少选择一张图片");
                } else {//商品信息必填，联系方式至少填一个
                    if (TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(price.getText()
                            .toString()) || TextUtils.isEmpty(describe.getText().toString()) || TextUtils
                            .isEmpty(transactionplace.getText().toString())) {
                        toastUtil.show(mContext, "请补全商品信息哦");
                    } else {
                        if (TextUtils.isEmpty(phone.getText().toString()) && TextUtils.isEmpty(micromsg
                                .getText().toString()) && TextUtils.isEmpty(qqnumber.getText().toString())) {
                            toastUtil.show(mContext, "请至少填写一种联系方式");
                        } else {
                            progressdialogUtil.showDialog(mContext, "正在上传，请稍候...");
                            String[] bmobFiles = new String[imgUris.size()];
                            for (int i = 0; i < imgUris.size(); i++) {
                                bmobFiles[i] = imgUris.get(i).getPath();
                            }
                            //开始上传图片
                            Bmob.uploadBatch(mContext, bmobFiles, new UploadBatchListener() {
                                @Override
                                public void onSuccess(List<BmobFile> list, List<String> urls) {
                                    if (urls.size() == imgUris.size()) {
                                        //图片上传完成后保存
                                        goods myGood = new goods();
                                        //设置商品信息
                                        //设置类别
                                        if (isCategeryChoosed) {
                                            myGood.setCategory(categary.getText().toString());
                                        } else {
                                            myGood.setCategory("其他");
                                        }
                                        //设置主图
                                        myGood.setMain_img(urls.get(0));
                                        //设置描述信息
                                        myGood.setDescribe(describe.getText().toString());
                                        //设置标题
                                        myGood.setTitle(title.getText().toString());
                                        //设置价格
                                        myGood.setPrice(price.getText().toString());
                                        //设置交易地点
                                        myGood.setLocation(transactionplace.getText().toString());
                                        //设置图片
                                        myGood.setImgs((ArrayList<String>) urls);
                                        //设置联系方式
                                        //设置电话
                                        if (!TextUtils.isEmpty(phone.getText().toString())) {
                                            myGood.setPhone(phone.getText().toString());
                                        }
                                        //设置微信号码
                                        if (!TextUtils.isEmpty(micromsg.getText().toString())) {
                                            myGood.setMicromsg(micromsg.getText().toString());
                                        }
                                        //设置qq
                                        if (!TextUtils.isEmpty(qqnumber.getText().toString())) {
                                            myGood.setQq(qqnumber.getText().toString());
                                        }
                                        //设置发布者
                                        myGood.setUserid(me.getObjectId());
                                        //保存商品信息
                                        myGood.save(mContext, new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                toastUtil.show(mContext, "发布成功");
                                                progressdialogUtil.cancelDialog();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Log.d("kydiwen", "商品信息保存失败信息" + s);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onProgress(int i, int i1, int i2, int i3) {

                                }

                                @Override
                                public void onError(int i, String s) {
                                    toastUtil.show(mContext,"上传失败，请检查您的网络设置");
                                }
                            });
                        }
                    }
                }
            }
        });
        //选择分类按钮点击事件
        categary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CategeryChooseActivity.class);
                startActivityForResult(intent, CATEGERY_CHOOSE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgUri, "image/*");
                intent.putExtra("scale", true);//允许缩放
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);//输出路径
                startActivityForResult(intent, CROP_PHOTO);
            }else  if(resultCode==RESULT_CANCELED){
                toastUtil.show(mContext,"请重新选择图片");
            }
        } else if (requestCode == CHOOSE_PHOTO) {
            if(resultCode==RESULT_OK){
                Uri uri_choose;
                uri_choose = data.getData();//获取选取的图片路径
                String path = DateFormat.format("yyyyMMddhhmmss", new Date()).toString();
                File file = new File(Environment.getExternalStorageDirectory(),
                        "/xcustuassistant/imgs/" + path + ".jpg");
                imgUri = Uri.fromFile(file);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri_choose, "image/*");//第一个参数表示要处理图片的路径
                intent.putExtra("scale", true);//允许缩放
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);//输出路径
                startActivityForResult(intent, CROP_PHOTO);
            }else  if(resultCode==RESULT_CANCELED){
                toastUtil.show(mContext,"请重新选择图片");
            }
        } else if (requestCode == CROP_PHOTO) {
            if(resultCode==RESULT_OK){
                try {
                    //添加图片路径
                    imgUris.add(imgUri);
                    final View img = View.inflate(mContext, R.layout.img_add, null);
                    ImageView img_add = (ImageView) img.findViewById(R.id.img_add);
                    ImageView img_del = (ImageView) img.findViewById(R.id.img_del);
                    img_add.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream
                            (imgUri)));
                    //添加图片
                    img_container.addView(img);
                    img_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //删除图片
                            img_container.removeView(img);
                            imgUris.remove(imgUris.size() - 1);
                        }
                    });
                    dialog.dismiss();
                    isImgChoosen = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(resultCode==RESULT_CANCELED){
                toastUtil.show(mContext,"请重新选择图片");
            }
        } else if (requestCode == CATEGERY_CHOOSE) {
            if (resultCode == RESULT_OK) {
                //显示商品类别
                String cat = data.getStringExtra(CategeryChooseActivity.CATEGERY_RETURNED);
                categary.setText(cat);
                isCategeryChoosed = true;//设置为已选择类别
            }else if(resultCode==RESULT_CANCELED) {
                toastUtil.show(mContext,"您未选择图片，将默认为其他");
            }
        }
    }
}
