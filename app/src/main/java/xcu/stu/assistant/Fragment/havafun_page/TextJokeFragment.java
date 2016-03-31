package xcu.stu.assistant.Fragment.havafun_page;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
public class TextJokeFragment extends baseFragment {
    private SwipeRefreshLayout refresh;//下拉刷新组件
    private ListView textjoke_list;//笑话列表
    private  int currentPage=1;//当前需要请求的页面
    private ArrayList<jokeBean>jokes=new ArrayList<jokeBean>();//新获取笑话数据
    private  myAdapter adapter;//适配器对象
    @Override
    protected View initView() {
        View view=View.inflate(mContext, R.layout.joke_list,null);
        refresh= (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        textjoke_list= (ListView) view.findViewById(R.id.joke_list);
        return view;
    }

    @Override
    protected void initData() {
        //为listview设置适配器
        adapter=new myAdapter();
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
    private  void  loadJokeData(){
        final String url=mContext.getResources().getString(R.string.havefun_xh_url)
                +"&page="+currentPage+"&pagesize="+10;
        requestUtil.jsonRequest(url, new jsonCallback() {
            @Override
            public void getJson(JSONObject response) {
               //解析json数据
                try {
                    JSONObject jokesObject=response.getJSONObject("result");
                    JSONArray Alljokes=jokesObject.getJSONArray("data");
                    //遍历所有节点
                   for(int i=Alljokes.length()-1;i>=0;i--){
                       //单条数据节点
                       JSONObject object=Alljokes.getJSONObject(i);
                       jokeBean bean=new jokeBean();
                       bean.setContent(object.getString("content"));
                       bean.setUpdatetime(object.getString("updatetime"));
                       jokes.add(0,bean);
                       //更新listview数据
                       adapter.notifyDataSetChanged();
                   }
                    refresh.setRefreshing(false);
                    currentPage=currentPage+1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //listview适配器
    class  myAdapter extends BaseAdapter{

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
            View view=View.inflate(mContext,R.layout.havefun_textjoke_item,null);
            TextView time= (TextView) view.findViewById(R.id.updatetime);
            TextView content= (TextView) view.findViewById(R.id.content);
            time.setText(jokes.get(position).getUpdatetime());
            content.setText(jokes.get(position).getContent());
            return view;
        }
    }
}
