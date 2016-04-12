package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.lruImageCache;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 我的二手商品显示界面
 * 2016年4月11日
 */
public class MyErshouActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置指示
    private Context mContext;//全局可用的context
    private ListView my_ershou_list;//我的二手商品列表
    private ArrayList<goods> my_ershou = new ArrayList<goods>();//二手商品数据
    private myAdapter adapter;//适配器
    private RequestQueue queue;//请求队列
    private lruImageCache cache;//缓存对象
    private ImageLoader imageLoader;//图片加载对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MyErshouActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(MyErshouActivity.this, R.color.main_color);
        setContentView(R.layout.activity_my_ershou);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        my_ershou_list = (ListView) findViewById(R.id.my_ershou_list);
    }

    //初始化数据
    private void initData() {
        queue = Volley.newRequestQueue(mContext);
        cache = lruImageCache.instance();
        imageLoader = new ImageLoader(queue, cache);
        //显示当前位置
        location.setVisibility(View.VISIBLE);
        location.setText("我的二手");
        //设置适配器
        adapter = new myAdapter();
        my_ershou_list.setAdapter(adapter);
        //获取数据，更新列表
        loadMyErshou();
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
        //为二手商品设置点击事件
        my_ershou_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    //获取二手列表数据
    private void loadMyErshou() {
        BmobQuery<goods> query = new BmobQuery<goods>();
        query.addWhereEqualTo("userid", BmobUser.getCurrentUser(mContext).getObjectId());
        query.findObjects(mContext, new FindListener<goods>() {
            @Override
            public void onSuccess(List<goods> list) {
                my_ershou.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    //我的二手商品列表适配器
    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return my_ershou.size();
        }

        @Override
        public Object getItem(int position) {
            return my_ershou.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder = null;
            //设置缓存视图
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.goods_item, null);
                holder = new viewHolder();
                holder.main_img = (NetworkImageView) convertView.findViewById(R.id.main_img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.category = (TextView) convertView.findViewById(R.id.categery);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            //设置数据
            holder.title.setText(my_ershou.get(position).getTitle());
            holder.price.setText("￥"+my_ershou.get(position).getPrice());
            holder.category.setText(my_ershou.get(position).getCategory());
            holder.main_img.setDefaultImageResId(R.drawable.loading);
            holder.main_img.setErrorImageResId(R.drawable.loading_failed);
            holder.main_img.setImageUrl(my_ershou.get(position).getMain_img(), imageLoader);
            return convertView;
        }
    }

    //缓存视图
    class viewHolder {
        NetworkImageView main_img;
        TextView title;
        TextView price;
        TextView category;
    }
}
