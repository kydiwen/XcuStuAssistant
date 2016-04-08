package xcu.stu.assistant.Fragment.business_page;

import android.util.Log;
import android.view.View;

import cn.bmob.v3.BmobUser;
import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;

/**
 * Created by 孙文权 on 2016/4/7.
 */
public class drawerFragment extends baseFragment {
    @Override
    protected View initView() {
        View view=View.inflate(mContext, R.layout.fragment_drawer,null);
        return view;
    }

    @Override
    protected void initData() {
        //获取当前登录用户
        BmobUser user=BmobUser.getCurrentUser(mContext);
    }

    @Override
    protected void initListener() {

    }
}
