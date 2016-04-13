package xcu.stu.assistant.Fragment.business_page;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import xcu.stu.assistant.bean.needs;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.progressdialogUtil;
import xcu.stu.assistant.widget.upToLoadLayout;

/**
 * Created by 孙文权 on 2016/4/13.
 * 我发布的需求显示页面
 */
public class myNeedsFragment extends baseFragment {
    private upToLoadLayout refresh;//刷新组件
    private ListView my_needs_list;//需求列表
    private ArrayList<needs> data = new ArrayList<needs>();//需求数据
    private myAdapter adapter;
    private final static int DOWN_REFRESH = 0;//下拉刷新
    private final static int LOAD_MORE = 1;//加载更多
    private String newsestTime="";//最新一条数据的时间
    private int currentData = 0;//当前的数据量
    private int selectedItem;//长按删除的项

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.myershou_needs_list, null);
        my_needs_list = (ListView) view.findViewById(R.id.my_list);
        refresh = (upToLoadLayout) view.findViewById(R.id.refresh);
        return view;
    }

    @Override
    protected void initData() {
        //设置适配器
        adapter = new myAdapter();
        my_needs_list.setAdapter(adapter);
        //加载数据
        loadMyNeeds();
        registerForContextMenu(my_needs_list);
    }

    //获取需求数据
    private void loadMyNeeds() {
        //加载第一页数据
        BmobQuery<needs> query = new BmobQuery<needs>();
        //时间降序
        query.order("-createdAt");
        query.addWhereEqualTo("userid", BmobUser.getCurrentUser(mContext).getObjectId());
        //一次查询十条数据
        query.setLimit(10);
        query.findObjects(mContext, new FindListener<needs>() {
            @Override
            public void onSuccess(List<needs> list) {
                if(list.size()>0) {
                    data.addAll(list);
                    adapter.notifyDataSetChanged();
                    currentData += list.size();
                    newsestTime = list.get(0).getCreatedAt();
                }
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
        //监听listview的长按事件
        my_needs_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                return false;
            }
        });
    }

    //加载数据方法封装
    private void queryData(final int action) {
        BmobQuery<needs> query = new BmobQuery<needs>();
        // 按时间降序查询
        query.order("-createdAt");
        query.addWhereEqualTo("userid", BmobUser.getCurrentUser(mContext).getObjectId());
        //如果是加载更多
        if (action == LOAD_MORE) {
            // 跳过之前页数并去掉重复数据
            query.setSkip(currentData);
        } else {
            if(TextUtils.isEmpty(newsestTime)){
                loadMyNeeds();//数据为空，重新加载，循环执行
            }else {
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
        }
        // 设置每页数据个数
        query.setLimit(10);
            //查找数据
            query.findObjects(mContext, new FindListener<needs>() {
                @Override
                public void onSuccess(List<needs> list) {
                    refresh.setRefreshing(false);
                    refresh.setLoading(false);
                    switch (action) {
                        case DOWN_REFRESH:
                            if (list.size() > 1) {
                                list.remove(list.size() - 1);
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    data.add(0, list.get(i));
                                    adapter.notifyDataSetChanged();
                                }
                                currentData += list.size();
                                newsestTime = list.get(0).getCreatedAt();
                            } else {
                                toastUtil.show(mContext, "暂无更新");
                            }
                            break;
                        case LOAD_MORE:
                            refresh.setLoading(false);
                            if (list.size() > 0) {
                                currentData += list.size();
                                data.addAll(list);
                                adapter.notifyDataSetChanged();
                            } else {
                                toastUtil.show(mContext, "无更多数据");
                            }
                            break;
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if(action==DOWN_REFRESH){
                        toastUtil.show(mContext,"刷新失败，请重试...");
                        refresh.setRefreshing(false);
                    }else {
                        toastUtil.show(mContext,"加载失败，请重试...");
                        refresh.setLoading(false);
                    }
                }
            });
    }

    //适配器对象
    class myAdapter extends BaseAdapter {

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
            if (convertView == null) {
                holder = new viewHolder();
                convertView = View.inflate(mContext, R.layout.needs_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.describe = (TextView) convertView.findViewById(R.id.describe);
                holder.added_time = (TextView) convertView.findViewById(R.id.added_time);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.title.setText(data.get(position).getTitle());
            holder.describe.setText(data.get(position).getDescribe());
            holder.added_time.setText(data.get(position).getCreatedAt());
            return convertView;
        }
    }

    //视图缓存对象
    class viewHolder {
        private TextView title;//标题
        private TextView describe;//描述信息
        private TextView added_time;//发布时间
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //提示正在删除
        progressdialogUtil.showDialog(mContext, "正在删除...");
        if (item.getItemId() == 0) {
            final needs need_todelete = data.get(selectedItem);
            String id = need_todelete.getObjectId();
            need_todelete.delete(mContext, id, new DeleteListener() {
                @Override
                public void onSuccess() {
                    data.remove(need_todelete);
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
