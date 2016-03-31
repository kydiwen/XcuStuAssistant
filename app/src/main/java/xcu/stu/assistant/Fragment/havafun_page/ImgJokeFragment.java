package xcu.stu.assistant.Fragment.havafun_page;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.jokeBean;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.requestUtil;

/**
 * Created by 孙文权 on 2016/3/30.
 * 最新笑话显示页面
 */
public class ImgJokeFragment extends baseFragment {
    private SwipeRefreshLayout refresh;//下拉刷新组件
    private ListView textjoke_list;//笑话列表
    private int currentPage = 1;//当前需要请求的页面
    private ArrayList<jokeBean> jokes = new ArrayList<jokeBean>();//新获取笑话数据
    private myAdapter adapter;//适配器对象
    private  ImageLoader imageLoader;//加载大量图片
    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.joke_list, null);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        textjoke_list = (ListView) view.findViewById(R.id.joke_list);
        return view;
    }

    @Override
    protected void initData() {
        RequestQueue queue= Volley.newRequestQueue(mContext);
        imageLoader=new ImageLoader(queue,new myBitmapCache(4*1024*1024));
        //为listview设置适配器
        adapter = new myAdapter();
        textjoke_list.setAdapter(adapter);
        //加载数据
        loadJokeData();
        refresh.setRefreshing(true);
    }

    @Override
    protected void initListener() {
        //为刷新组件设置监听事件，实现下拉刷新效果
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //加载下一页数据
                loadJokeData();
            }
        });
    }

    //获取最新笑话数据
    private void loadJokeData() {
        final String url = mContext.getResources().getString(R.string.havefun_img_url)
                + "&page=" + currentPage + "&pagesize=" + 10;
        requestUtil.jsonRequest(url, new jsonCallback() {
            @Override
            public void getJson(JSONObject response) {
                //解析json数据
                try {
                    JSONObject jokesObject = response.getJSONObject("result");
                    JSONArray Alljokes = jokesObject.getJSONArray("data");
                    //遍历所有节点
                    for (int i = Alljokes.length() - 1; i >= 0; i--) {
                        //单条数据节点
                        JSONObject object = Alljokes.getJSONObject(i);
                        jokeBean bean = new jokeBean();
                        bean.setContent(object.getString("content"));
                        bean.setUpdatetime(object.getString("updatetime"));
                        bean.setImgurl(object.getString("url"));
                        jokes.add(0, bean);
                        //更新listview数据
                        adapter.notifyDataSetChanged();
                    }
                    refresh.setRefreshing(false);
                    currentPage = currentPage + 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //listview适配器
    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return jokes.size();
        }

        @Override
        public Object getItem(int position) {
            return jokes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder=null;
            //判断是否缓存
            if(convertView==null){
                holder=new viewHolder();
                convertView=View.inflate(mContext, R.layout.havefun_imgjoke_item, null);
                holder.time= (TextView) convertView.findViewById(R.id.updatetime);
                holder.content= (TextView) convertView.findViewById(R.id.content);
                holder.image= (NetworkImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            }else {
                //通过tag找到缓存的视图
                holder= (viewHolder) convertView.getTag();
            }
            holder.time.setText(jokes.get(position).getUpdatetime());
            holder.content.setText(jokes.get(position).getContent());
            //设置默认图片
            holder.image.setDefaultImageResId(R.drawable.loading);
            //设置加载失败时显示的图片
            holder.image.setErrorImageResId(R.drawable.loading_failed);
            holder.image.setImageUrl(jokes.get(position).getImgurl(),imageLoader);
            return convertView;
        }
    }
    //视图缓存对象
    class  viewHolder {
        TextView time;
        TextView content;
        NetworkImageView image;
    }
    //图片缓存类
    class  myBitmapCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache{
        private LruCache<String, Bitmap> mCache;

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public myBitmapCache(int maxSize) {
            super(maxSize);
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            if (bitmap != null) {
                mCache.put(url, bitmap);
            }
        }
    }
}
