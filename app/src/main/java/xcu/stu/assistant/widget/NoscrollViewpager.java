package xcu.stu.assistant.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 创建不可滑动的viewpager
 * 孙文权 on 2016/3/1.
 */
public class NoscrollViewpager extends ViewPager {
    public NoscrollViewpager(Context context) {
        super(context);
    }

    public NoscrollViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //拦截滑动事件，设置viewpager为不可滑动
    //不消费touch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    //不消费事件，留给子控件处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
