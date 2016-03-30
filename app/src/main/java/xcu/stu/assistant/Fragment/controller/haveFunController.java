package xcu.stu.assistant.Fragment.controller;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.Fragment.havafun_page.ImgJokeFragment;
import xcu.stu.assistant.Fragment.havafun_page.TextJokeFragment;
import xcu.stu.assistant.R;

/**
 * 创建糗事百科控制器
 * 继承自baseFragment，并实现其抽象方法
 * 孙文权 on 2016/3/1.
 */
public class haveFunController extends baseFragment {
    //使用radiogroup结合radiobutton切换fragment
    private RadioGroup havefun_rg;
    private RadioButton rb_joke_text;
    private  RadioButton rb_joke_img;
    private FragmentManager manager;//fragment管理对象
    private FragmentTransaction transaction;//fragment事务
    private  TextJokeFragment tjFragment;//文字笑话
    private ImgJokeFragment ijFragment;//图片笑话
    @Override
    protected View initView() {
        View view=View.inflate(mContext, R.layout.fragment_havefun,null);
        havefun_rg= (RadioGroup) view.findViewById(R.id.havefun_rg);
        rb_joke_text= (RadioButton) view.findViewById(R.id.rb_joke_text);
        rb_joke_img= (RadioButton) view.findViewById(R.id.rb_joke_img);
        return view;
    }

    @Override
    protected void initData() {
        //初始化
        manager=getFragmentManager();
        transaction=manager.beginTransaction();
        tjFragment=new TextJokeFragment();
        ijFragment=new ImgJokeFragment();
        //添加fragment到容器中
        transaction.add(R.id.joke_container,tjFragment);
        transaction.add(R.id.joke_container,ijFragment);
        //默认显示第一项
        havefun_rg.check(R.id.rb_joke_text);
        transaction.show(tjFragment);
        transaction.hide(ijFragment);
        transaction.commit();
    }

    @Override
    protected void initListener() {
        //监听radiogroup的切换事件
        havefun_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_joke_text:
                        transaction=manager.beginTransaction();
                        transaction.show(tjFragment);
                        transaction.hide(ijFragment);
                        transaction.commit();
                        break;
                    case R.id.rb_joke_img:
                        transaction=manager.beginTransaction();
                        transaction.show(ijFragment);
                        transaction.hide(tjFragment);
                        transaction.commit();
                        break;
                }
            }
        });
    }
}
