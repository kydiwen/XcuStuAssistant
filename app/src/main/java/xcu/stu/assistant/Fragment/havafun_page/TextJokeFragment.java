package xcu.stu.assistant.Fragment.havafun_page;

import android.view.View;
import android.widget.ListView;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;

/**
 * Created by 孙文权 on 2016/3/30.
 * 最新笑话显示页面
 */
public class TextJokeFragment extends baseFragment {
    private ListView textjoke_list;//笑话列表
    @Override
    protected View initView() {
        View view=View.inflate(mContext, R.layout.joke_list,null);
        textjoke_list= (ListView) view.findViewById(R.id.joke_list);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
