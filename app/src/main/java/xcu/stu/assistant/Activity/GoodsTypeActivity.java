package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import xcu.stu.assistant.Fragment.controller.businessController;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.lruImageCache;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;
import xcu.stu.assistant.widget.upToLoadLayout;

/**
 *
 * 商品分类显示界面
 * 从服务器获取分类商品信息进行显示
 * 2016年4月14日
 * 孙文权
 */
public class GoodsTypeActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部位置显示按钮
    private  String currentType;//当前需要显示的分类类型
    private RelativeLayout message_none;//数据不存在提示
    private upToLoadLayout refresh;//刷新组件
    private ListView goods_list;//商品列表
    private ArrayList<goods>data=new ArrayList<goods>();//商品列表数据
    private  String lastTime="";//最新数据发布的时间
    private  int currentData=0;//当前的数据量
    private RequestQueue queue;//请求队列
    private lruImageCache cache;//图片缓存对象
    private ImageLoader imageLoader;//图片加载对象
    private  Context mContext;
    private  myAdapter  adapter;
    public  static  final  String GOODS_ITEM="商品项";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
    //初始化视图界面
    private  void  initView(){
        mContext=GoodsTypeActivity.this;
        color_same_to_app.setTopColorSameToApp(GoodsTypeActivity.this,R.color.main_color);
        setContentView(R.layout.activity_goods_type);
        back= (ImageView) findViewById(R.id.back);
        location= (TextView) findViewById(R.id.location);
        message_none= (RelativeLayout) findViewById(R.id.message_none);
        refresh= (upToLoadLayout) findViewById(R.id.refresh);
        goods_list= (ListView) findViewById(R.id.goods_list);
    }
    //初始化数据
    private  void  initData(){
        //显示顶部位置
        location.setVisibility(View.VISIBLE);
        currentType=getIntent().getStringExtra(businessController.GOODS_TYPE);
        location.setText(currentType);
        //初始化图片加载组件
        queue= Volley.newRequestQueue(mContext);
        cache=lruImageCache.instance();
        imageLoader=new ImageLoader(queue,cache);
        //设置适配器
        adapter=new myAdapter();
        goods_list.setAdapter(adapter);
        //加载数据
        loadData();
    }
    //初始化监听事件
    private  void  initListener(){
        //为顶部回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置列表项的点击事件
        goods_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        //设置上拉加载更多
        refresh.setOnLoadListener(new upToLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                load();
            }
        });
        //监听listview的滑动事件
        goods_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        refresh.setLoading(true);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //为商品列表设置点击事件
        goods_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,GoodsDetailActivity.class);
                intent.putExtra(GOODS_ITEM,data.get(position));
                startActivity(intent);
            }
        });
    }
    //下拉刷新
    private  void  refresh(){
        if(TextUtils.isEmpty(lastTime)){//暂无数据，执行请求操作
            loadData();
        }else {
            //设置时间
            BmobQuery<goods>query=new BmobQuery<goods>();
            Date date=null;
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
            //设置请求数据类别
            query.addWhereEqualTo("category",currentType);
            // 设置每次刷新数据个数
            query.setLimit(10);
            //加载数据
            query.findObjects(mContext, new FindListener<goods>() {
                @Override
                public void onSuccess(List<goods> list) {
                    refresh.setRefreshing(false);//停止刷新
                    if (list.size() > 1) {//刷新成功
                        //移除无用项
                        list.remove(list.size() - 1);
                        for (int i = list.size() - 1; i >= 0; i--) {
                            data.add(0, list.get(i));
                            adapter.notifyDataSetChanged();
                        }
                        //更新数据量
                        currentData+=list.size();
                        //更新最新数据的时间
                        lastTime=list.get(0).getCreatedAt();
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
    //上拉加载
    private  void  load(){
            BmobQuery<goods>query=new BmobQuery<goods>();
            //加载十条数据
            query.setLimit(10);
            //跳过数据
            query.setSkip(currentData);
            //时间降序
            query.order("-createdAt");
            //设置请求的类别
            query.addWhereEqualTo("category",currentType);
            //请求数据
            query.findObjects(mContext, new FindListener<goods>() {
                @Override
                public void onSuccess(List<goods> list) {
                    refresh.setLoading(false);
                    if(list.size()>0){
                        //刷新数据
                        data.addAll(list);
                        adapter.notifyDataSetChanged();
                        currentData+=list.size();
                    }else {
                        toastUtil.show(mContext,"无更多数据");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    refresh.setLoading(false);
                    toastUtil.show(mContext,"加载失败，请重试");
                }
            });
    }
    //获取第一页数据
    private  void  loadData(){
        //提示正在加载
        progressdialogUtil.showDialog(mContext,"正在加载，请稍候...");
        BmobQuery<goods>query=new BmobQuery<goods>();
        //加载十条数据
        query.setLimit(10);
        //时间降序
        query.order("-createdAt");
        //设置请求的类别
        query.addWhereEqualTo("category",currentType);
        query.findObjects(mContext, new FindListener<goods>() {
            @Override
            public void onSuccess(List<goods> list) {
                refresh.setRefreshing(false);
                progressdialogUtil.cancelDialog();
                //数据存在，隐藏空数据提示
                if(list.size()>0){
                    message_none.setVisibility(View.INVISIBLE);
                    //刷新数据
                    data.addAll(list);
                    adapter.notifyDataSetChanged();
                    //为最新数据时间设置数据
                    lastTime=list.get(0).getCreatedAt();
                    //设置已加载数据数量
                    currentData+=list.size();
                }
            }

            @Override
            public void onError(int i, String s) {
                progressdialogUtil.cancelDialog();
                toastUtil.show(mContext,"加载失败，请重试");
            }
        });
    }
    //商品列表适配器
    class  myAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder;
            if(convertView==null){
                holder=new viewHolder();
                convertView=View.inflate(mContext,R.layout.goods_item,null);
                holder.imageview= (NetworkImageView) convertView.findViewById(R.id.main_img);
                holder.title= (TextView) convertView.findViewById(R.id.title);
                holder.price= (TextView) convertView.findViewById(R.id.price);
                holder.time= (TextView) convertView.findViewById(R.id.added_time);
                convertView.setTag(holder);
            }else {
                holder= (viewHolder) convertView.getTag();
            }
            holder.title.setText(data.get(position).getTitle());
            holder.price.setText("￥"+data.get(position).getPrice());
            holder.time.setText(data.get(position).getCreatedAt());
            holder.imageview.setDefaultImageResId(R.drawable.loading);
            holder.imageview.setErrorImageResId(R.drawable.loading_failed);
            holder.imageview.setImageUrl(data.get(position).getMain_img(),imageLoader);
            return convertView;
        }
    }
    //视图缓存对象
    class  viewHolder{
        NetworkImageView imageview;
        TextView title;
        TextView price;
        TextView time;
    }
}
