package xcu.stu.assistant.Fragment.business_page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import de.hdodenhof.circleimageview.CircleImageView;
import xcu.stu.assistant.Activity.AboutActivity;
import xcu.stu.assistant.Activity.AddErshouActivity;
import xcu.stu.assistant.Activity.LoginACtivity;
import xcu.stu.assistant.Activity.MyErshouActivity;
import xcu.stu.assistant.Activity.UserInfoEditActivity;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.requestUtil;

/**
 * Created by 孙文权 on 2016/4/7.
 * 侧边栏（抽屉页面）
 * 显示用户信息
 * 商品信息发布入口页面
 */
public class drawerFragment extends baseFragment {
    private CircleImageView user_img;//用户头像信息
    private TextView username;//当前用户名
    private LinearLayout user_info_edit;//修改个人信息
    private LinearLayout my_ershou;//我的商品
    private LinearLayout add_ershou;//发布二手
    private  LinearLayout about;//关于助手
    private LinearLayout close;//退出账号
    private BmobUser currentUser;
    private final int UPDATE_INFO = 0;//修改个人信息
    public static final String USER_ID = "用户id";
    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_drawer, null);
        user_img = (CircleImageView) view.findViewById(R.id.img);
        username = (TextView) view.findViewById(R.id.username);
        user_info_edit = (LinearLayout) view.findViewById(R.id.user_info_edit);
        my_ershou = (LinearLayout) view.findViewById(R.id.my_ershou);
        add_ershou = (LinearLayout) view.findViewById(R.id.add_ershou);
        about= (LinearLayout) view.findViewById(R.id.about);
        close = (LinearLayout) view.findViewById(R.id.close);
        return view;
    }

    @Override
    protected void initData() {
        currentUser = BmobUser.getCurrentUser(mContext);
        //显示用户信息
        showUserInfo();
    }

    @Override
    protected void initListener() {
        //为修改信息设置点击事件
        user_info_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined()) {
                    toastUtil.show(mContext, "请先登录");
                } else {
                    String userId = currentUser.getObjectId();
                    Intent intent = new Intent(mContext, UserInfoEditActivity.class);
                    intent.putExtra(USER_ID, userId);
                    startActivityForResult(intent, UPDATE_INFO);
                }
            }
        });
        //为我的二手设置点击事件
        my_ershou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined()) {
                    toastUtil.show(mContext, "请先登录");
                } else {
                    //进入我的二手界面
                    Intent intent=new Intent(mContext, MyErshouActivity.class);
                    startActivity(intent);
                }
            }
        });
        //为发布二手设置点击事件
        add_ershou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined()) {
                    toastUtil.show(mContext, "请先登录");
                } else {
                    //进入商品发布页面
                    Intent intent=new Intent(mContext, AddErshouActivity.class);
                    startActivity(intent);
                }
            }
        });
        //为关于助手设置点击事件
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, AboutActivity.class);
                startActivity(intent);
            }
        });
        //为退出账号设置点击事件
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined()) {
                    toastUtil.show(mContext, "请先登录");
                } else {
                    //退出当前账号并进入登陆界面
                    BmobUser.logOut(mContext);
                    Intent intent = new Intent(mContext, LoginACtivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //为用户头像设置点击事件:登录时进入个人信息设置界面，未登陆时进入登录界面
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogined()) {
                    Intent intent = new Intent(mContext, LoginACtivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String userId = currentUser.getObjectId();
                    Intent intent = new Intent(mContext, UserInfoEditActivity.class);
                    intent.putExtra(USER_ID, userId);
                    startActivityForResult(intent, UPDATE_INFO);
                }
            }
        });
    }

    //显示用户信息
    private void showUserInfo() {
        //判断是否已登录
        if (!isLogined()) {
            username.setText("未登录");
        } else {
            loadUserInfo();
        }
    }

    //加载用户信息
    private void loadUserInfo() {
        //当前用户的id
        final String currentID = currentUser.getObjectId();
        //获取当前用户bean类
        BmobQuery<user> query = new BmobQuery<user>();
        query.getObject(mContext, currentID, new GetListener<user>() {
            @Override
            public void onSuccess(user user) {
                final user me = user;
                //加载图片
                //获取头像地址
                final String url = me.getUser_img().getFileUrl(mContext);
                loadImg(url, user_img);
                username.setText(me.getUsername());
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    //判断是否已登录
    private boolean isLogined() {
        if (currentUser == null) {
            return false;
        } else {
            return true;
        }
    }

    //加载图片
    private void loadImg(String url, final CircleImageView imageView) {
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_INFO) {
            if (resultCode == Activity.RESULT_OK) {
                //加载用户信息
                loadUserInfo();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toastUtil.show(mContext, "取消修改");
            }
        }
    }
}
