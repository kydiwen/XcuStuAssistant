package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import de.hdodenhof.circleimageview.CircleImageView;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.user;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.requestUtil;
import xcu.stu.assistant.widget.CustomViewPager;

/**
 * 商品详情页面
 * 显示商品详细
 * 2016年4月14日
 * 孙文权
 */
public class GoodsDetailActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示按钮
    private goods currentGood;//当前需要显示的商品
    private CustomViewPager good_imgs;//商品图片
    private CircleImageView user_img;//用户头像
    private TextView user_name;//用户名
    private TextView price;//价格
    private TextView categery;//商品类别
    private TextView title;//标题
    private TextView describe;//描述信息
    private TextView place;//交易地点
    private ImageView qqnumber;//qq联系
    private ImageView phone;//电话联系
    private Context mContext;
    private String phone_number;//用户留下的联系电话
    private ArrayList<String> imgsUrl = new ArrayList<String>();//商品图片
    private myAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = GoodsDetailActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(GoodsDetailActivity.this, R.color.main_color);
        setContentView(R.layout.activity_goods_detail);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        good_imgs = (CustomViewPager) findViewById(R.id.good_imgs);
        user_img = (CircleImageView) findViewById(R.id.user_img);
        user_name = (TextView) findViewById(R.id.user_name);
        categery = (TextView) findViewById(R.id.categery);
        title = (TextView) findViewById(R.id.title);
        describe = (TextView) findViewById(R.id.describe);
        place = (TextView) findViewById(R.id.place);
        qqnumber = (ImageView) findViewById(R.id.qqnumber);
        phone = (ImageView) findViewById(R.id.phone);
        price = (TextView) findViewById(R.id.price);
    }

    //初始化数据
    private void initData() {
        //显示顶部位置指示
        location.setVisibility(View.VISIBLE);
        location.setText("商品详情");
        //获取需要显示的商品
        currentGood = (goods) getIntent().getSerializableExtra(GoodsTypeActivity.GOODS_ITEM);
        //如果用户没有留下qq号码
        if (TextUtils.isEmpty(currentGood.getQq())) {
            qqnumber.setVisibility(View.GONE);
        }
        //获取电话号码
        phone_number = currentGood.getPhone();
        //显示文字数据
        shownormal();
        //显示网络获取的数据
        loadNetworkInfo();
        //设置图片适配器
        imgsUrl.addAll(currentGood.getImgs());
        adapter = new myAdapter();
        good_imgs.setAdapter(adapter);
    }

    //显示普通信息
    private void shownormal() {
        //显示价格
        price.setText("￥" + currentGood.getPrice());
        //显示分类
        categery.setText(currentGood.getCategory());
        //显示标题
        title.setText(currentGood.getTitle());
        //显示描述信息
        describe.setText(currentGood.getDescribe());
        //显示交易地点
        place.setText(currentGood.getLocation());
    }

    //加载网络信息
    private void loadNetworkInfo() {
        //获取发布者
        BmobQuery<user> query = new BmobQuery<user>();
        query.getObject(mContext, currentGood.getUserid(), new GetListener<user>() {
            @Override
            public void onSuccess(user user) {
                loadImg(user.getUser_img().getFileUrl(mContext), user_img);
                user_name.setText(user.getUsername());
                //设置在用户未设置联系电话时的电话号码
                phone_number = user.getMobilePhoneNumber();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    //初始化监听事件
    private void initListener() {
        //顶部回退按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置qq联系点击事件
        qqnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + currentGood.getQq();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        //设置电话联系点击事件
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("请选择联系方式");
                builder.setPositiveButton("拨打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone_number));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("发送短信", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone_number));
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }

    //加载图片
    private void loadImg(String url, final ImageView imageView) {
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    //图片适配器
    class myAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgsUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.imgnews_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgsnews_img);
            loadImg(imgsUrl.get(position), imageView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
