package xcu.stu.assistant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.news;

/**
 * Created by 孙文权 on 2016/3/10.
 */
public class newscenter_newslistAdapter extends BaseAdapter {
    private ArrayList<news> data;//新闻数据
    private int layout;//布局文件
    private Context mContext;
    private LinearLayout newstype;

    //自定义构造方法，传入需要的参数
    public newscenter_newslistAdapter(ArrayList<news> newses, int news_item_layout, Context context) {
        data = newses;
        layout = news_item_layout;
        mContext = context;
    }

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
        View view = View.inflate(mContext, layout, null);
        //新闻类型显示栏
        newstype = (LinearLayout) view.findViewById(R.id.newstype);
        //根据position获取新闻类型
        if (position == getPositionForSection(data.get(position).getNewsType())) {
            newstype.setVisibility(View.VISIBLE);
            //新闻类型
            TextView type = (TextView) view.findViewById(R.id.newscenter_newstype);
            type.setText(data.get(position).getNewsType());
        } else {
            newstype.setVisibility(View.GONE);
        }
        //标题
        TextView title = (TextView) view.findViewById(R.id.news_title);
        //时间
        TextView time = (TextView) view.findViewById(R.id.news_time);
        //显示数据
        title.setText(data.get(position).getNewsTitle());
        time.setText(data.get(position).getNewsTime());
        return view;
    }

    /**
     * 根据分类的新闻类型值获取其第一次出现该新闻类型的位置
     */
    public int getPositionForSection(String newsType) {
        for (int i = 0; i < getCount(); i++) {
            String firstString = data.get(i).getNewsType();
            String firstChar = firstString;
            if (firstChar == newsType) {
                return i;
            }
        }

        return -1;
    }
}
