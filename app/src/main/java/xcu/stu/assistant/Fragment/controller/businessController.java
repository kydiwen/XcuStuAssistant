package xcu.stu.assistant.Fragment.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.lruImageCache;
import xcu.stu.assistant.utils.callback.toastUtil;

/**
 * 创建跳骚市场控制器
 * 继承自baseFragment，并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class businessController extends baseFragment {
    private SwipeRefreshLayout refresh;//刷新组件
    private GridView goods_type;//类型商品列表
    private ArrayList<String> types = new ArrayList<String>();//商品分类
    private typeAdapter typeAdapter;//商品类型列表适配器
    private ListView goods_list;//商品列表
    private ArrayList<goods> newestErshous = new ArrayList<goods>();//最新商品数据
    private myErshouAdapter goodsAdapter;
    private RequestQueue queue;//请求队列
    private ImageLoader imageLoader;
    private lruImageCache cache;//缓存对象
    private String lastTime = "";//最新一条数据的时间
    private int currentGoods = 0;//当前的商品数量
    private  RelativeLayout mess_none;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragmentbusiness, null);
        goods_type = (GridView) view.findViewById(R.id.goods_type);
        goods_list = (ListView) view.findViewById(R.id.goods_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mess_none= (RelativeLayout) view.findViewById(R.id.message_none);
        return view;
    }

    @Override
    protected void initData() {
        //添加类型数据
        initType();
        //初始化商品列表
        initGoods();
        //获取最新数据
        loadGoods();
    }

    //获取最新数据
    private void loadGoods() {
        BmobQuery<goods> query = new BmobQuery<goods>();
        //按时间降序
        query.order("-createdAt");
        //每次请求30条数据,并且主界面只显示最新的30条数据，有更新执行刷新
        query.setLimit(30);
        query.findObjects(mContext, new FindListener<goods>() {
            @Override
            public void onSuccess(List<goods> list) {
                if (list.size() > 0) {
                    //将无数据的提示界面隐藏
                    mess_none.setVisibility(View.INVISIBLE);
                    //更新列表
                    newestErshous.addAll(list);
                    goodsAdapter.notifyDataSetChanged();
                    lastTime = list.get(0).getCreatedAt();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    //初始化商品列表
    private void initGoods() {
        cache = lruImageCache.instance();
        queue = Volley.newRequestQueue(mContext);
        imageLoader = new ImageLoader(queue, cache);
        goodsAdapter = new myErshouAdapter();
        //设置适配器
        goods_list.setAdapter(goodsAdapter);
    }

    //初始化类型列表
    private void initType() {
        types.add("电脑");
        types.add("手机");
        types.add("数码");
        types.add("书籍");
        types.add("运动");
        types.add("美妆");
        types.add("衣服");
        types.add("其它");
        typeAdapter = new typeAdapter(types);
        goods_type.setAdapter(typeAdapter);
    }

    @Override
    protected void initListener() {
        //设置类型列表的点击事件
        goods_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        //设置下拉刷新事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    //下拉刷新逻辑
    private void refresh() {
        if (TextUtils.isEmpty(lastTime)) {//无数据
            loadGoods();//执行请求方法，从服务器重新获取
            refresh.setRefreshing(false);//停止刷新
        } else {
            BmobQuery<goods> query = new BmobQuery<goods>();
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 按时间降序查询
            query.order("-createdAt");
            //只查询大于最新一条数据发布时间的数据
            query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
            // 设置每次刷新数据个数
            query.setLimit(50);
            query.findObjects(mContext, new FindListener<goods>() {
                @Override
                public void onSuccess(List<goods> list) {
                    refresh.setRefreshing(false);//停止刷新
                    if (list.size() > 1) {//刷新成功
                        //移除无用项
                        list.remove(list.size() - 1);
                        for (int i = list.size() - 1; i >= 0; i--) {
                            newestErshous.add(0, list.get(i));
                            goodsAdapter.notifyDataSetChanged();
                        }

                    } else {
                        toastUtil.show(mContext, "暂无更新");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    refresh.setRefreshing(false);
                    toastUtil.show(mContext, "刷新失败，请重试");
                }
            });
        }
    }

    //商品类型适配器
    class typeAdapter extends BaseAdapter {
        private ArrayList<String> datas;

        public typeAdapter(ArrayList<String> data) {
            datas = data;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder;
            if (convertView == null) {
                holder = new viewHolder();
                convertView = View.inflate(mContext, R.layout.goodstype_item, null);
                holder.type = (TextView) convertView.findViewById(R.id.goods_type);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.type.setText(datas.get(position));
            return convertView;
        }
    }

    //商品分类缓存对象
    class viewHolder {
        TextView type;
    }

    //商品列表适配器
    class myErshouAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newestErshous.size();
        }

        @Override
        public Object getItem(int position) {
            return newestErshous.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            goodsHolder holder;
            if (convertView == null) {
                holder = new goodsHolder();
                convertView = View.inflate(mContext, R.layout.goods_item, null);
                holder.imageView = (NetworkImageView) convertView.findViewById(R.id.main_img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.time = (TextView) convertView.findViewById(R.id.added_time);
                //设置缓存视图
                convertView.setTag(holder);
            } else {
                holder = (goodsHolder) convertView.getTag();
            }
            //设置标题
            holder.title.setText(newestErshous.get(position).getTitle());
            //设置价格
            holder.price.setText("￥" + newestErshous.get(position).getPrice());
            //设置发布时间
            holder.time.setText(newestErshous.get(position).getCategory());
            //显示图片
            holder.imageView.setDefaultImageResId(R.drawable.loading);
            holder.imageView.setErrorImageResId(R.drawable.loading_failed);
            holder.imageView.setImageUrl(newestErshous.get(position).getMain_img(), imageLoader);
            return convertView;
        }
    }

    //最新商品列表缓存视图
    class goodsHolder {
        NetworkImageView imageView;
        TextView title;
        TextView price;
        TextView time;
    }
}
