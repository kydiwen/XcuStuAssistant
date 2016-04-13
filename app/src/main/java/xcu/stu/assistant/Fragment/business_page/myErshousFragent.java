package xcu.stu.assistant.Fragment.business_page;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.goods;
import xcu.stu.assistant.bean.lruImageCache;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.progressdialogUtil;
import xcu.stu.assistant.widget.upToLoadLayout;

/**
 * Created by 孙文权 on 2016/4/13.
 * 我发布的的二手商品显示界面
 */
public class myErshousFragent extends baseFragment {
    private upToLoadLayout refresh;//刷新组件
    private ListView my_ershou_list;//我的二手商品列表
    private ArrayList<goods> my_ershou = new ArrayList<goods>();//二手商品数据
    private myAdapter adapter;//适配器
    private RequestQueue queue;//请求队列
    private lruImageCache cache;//缓存对象
    private ImageLoader imageLoader;//图片加载对象
    private final static int DOWN_REFRESH = 0;//下拉刷新
    private final static int LOAD_MORE = 1;//加载更多
    private String newsestTime;//最新一条数据的时间
    private int currentData = 0;//当前的数据量
    private  int selected_item;//需要删除的项

    @Override
    protected View initView() {
        View view=View.inflate(mContext, R.layout.myershou_needs_list,null);
        my_ershou_list= (ListView) view.findViewById(R.id.my_list);
        refresh = (upToLoadLayout) view.findViewById(R.id.refresh);
        return view;
    }

    @Override
    protected void initData() {
        //提示正在加载
        progressdialogUtil.showDialog(mContext,"正在加载，请稍候...");
        queue = Volley.newRequestQueue(mContext);
        cache = lruImageCache.instance();
        imageLoader = new ImageLoader(queue, cache);
        //设置适配器
        adapter = new myAdapter();
        my_ershou_list.setAdapter(adapter);
        //获取数据，更新列表
        loadMyErshou();
        //注册上下文菜单
        registerForContextMenu(my_ershou_list);
    }
    //获取二手列表数据
    private void loadMyErshou() {
        BmobQuery<goods> query = new BmobQuery<goods>();
        query.order("-createdAt");//按时间降序
        query.addWhereEqualTo("userid", BmobUser.getCurrentUser(mContext).getObjectId());
        query.setLimit(10);
        query.findObjects(mContext, new FindListener<goods>() {
            @Override
            public void onSuccess(List<goods> list) {
                //提示用户正在加载，更新数据
                progressdialogUtil.cancelDialog();
                my_ershou.addAll(list);
                adapter.notifyDataSetChanged();
                currentData += list.size();
                newsestTime = list.get(0).getCreatedAt();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    @Override
    protected void initListener() {
        //设置刷新组建的下拉刷新事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryData(DOWN_REFRESH);
            }
        });
        //设置刷新组件的加载更多事件
        refresh.setOnLoadListener(new upToLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                queryData(LOAD_MORE);
            }
        });
        //设置长按事件
        my_ershou_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selected_item=position;
                return false;
            }
        });
    }
    //加载数据方法封装
    private void queryData(final int action) {
        BmobQuery<goods> query = new BmobQuery<goods>();
        // 按时间降序查询
        query.order("-createdAt");
        query.addWhereEqualTo("userid",BmobUser.getCurrentUser(mContext).getObjectId());
        //如果是加载更多
        if (action == LOAD_MORE) {
            // 跳过之前页数并去掉重复数据
            query.setSkip(currentData);
        } else {
            //查询最新数据
            //处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(newsestTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //只查询大于最新一条数据发布时间的数据
            query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
        }
        // 设置每页数据个数
        query.setLimit(10);
        //查找数据
        query.findObjects(mContext, new FindListener<goods>() {
            @Override
            public void onSuccess(List<goods> list) {
                switch (action) {
                    case DOWN_REFRESH:
                        if (list.size() > 1) {
                            list.remove(list.size() - 1);
                            for (int i = list.size() - 1; i >= 0; i--) {
                                my_ershou.add(0, list.get(i));
                                adapter.notifyDataSetChanged();
                                refresh.setRefreshing(false);
                            }
                            currentData += list.size();
                            newsestTime = list.get(0).getCreatedAt();
                        } else {
                            toastUtil.show(mContext, "暂无更新");
                            refresh.setRefreshing(false);
                        }
                        break;
                    case LOAD_MORE:
                        if (list.size() > 0) {
                            refresh.setLoading(false);
                            currentData += list.size();
                            my_ershou.addAll(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            toastUtil.show(mContext, "无更多数据");
                            refresh.setLoading(false);
                        }
                        break;
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("kydiwen", "错误信息：" + s);
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
                holder.added_time = (TextView) convertView.findViewById(R.id.added_time);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            //设置数据
            holder.title.setText(my_ershou.get(position).getTitle());
            holder.price.setText("￥"+my_ershou.get(position).getPrice());
            holder.added_time.setText(my_ershou.get(position).getCreatedAt());
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
        TextView added_time;
    }
    //上下文菜单

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //提示用户正在删除
        progressdialogUtil.showDialog(mContext,"正在删除...");
        if(item.getItemId()==0){
            final goods good_to_delete=my_ershou.get(selected_item);
            String id=good_to_delete.getObjectId();
            good_to_delete.delete(mContext, id, new DeleteListener() {
                @Override
                public void onSuccess() {
                    my_ershou.remove(good_to_delete);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
        return super.onContextItemSelected(item);
    }
}
