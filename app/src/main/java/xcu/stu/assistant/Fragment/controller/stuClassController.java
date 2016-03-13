package xcu.stu.assistant.Fragment.controller;

import android.view.View;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;

/**
 * 创建点到助手的控制器
 * 继承自basefFragment,并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class stuClassController extends baseFragment {
    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_stu_class, null);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
