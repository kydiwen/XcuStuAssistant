package xcu.stu.assistant.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 创建各个模块的父类控制器
 * 创建抽象方法，只需要在子类中实现即可
 * 孙文权 on 2016/3/1.
 */
public abstract class baseFragment extends Fragment {
    public Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    //初始化视图的抽象方法
    protected abstract View initView();

    //初始化数据的抽象方法
    protected abstract void initData();

    //初始化监听事件的方法
    protected abstract void initListener();
}
