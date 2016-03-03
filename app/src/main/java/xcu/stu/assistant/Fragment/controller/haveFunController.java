package xcu.stu.assistant.Fragment.controller;

import android.view.View;
import android.widget.TextView;

import xcu.stu.assistant.Fragment.baseFragment;

/**
 * 创建糗事百科控制器
 * 继承自baseFragment，并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class haveFunController extends baseFragment {
    @Override
    protected View initView() {
        TextView tv = new TextView(mContext);
        tv.setTextSize(25);
        tv.setText("糗事百科");
        return tv;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
