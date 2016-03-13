package xcu.stu.assistant.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import xcu.stu.assistant.R;

/**
 * 为listview添加头布局
 * Created by 孙文权 on 2016/3/11.
 */
public class customListview extends ListView {
    private Context mContext;
    private static View headerView;

    public customListview(Context context) {
        super(context);
        mContext = context;
        initHeaderView();
    }

    public customListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initHeaderView();
    }

    public customListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initHeaderView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public customListview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initHeaderView();
    }

    //初始化头布局
    private void initHeaderView() {
        headerView = View.inflate(mContext, R.layout.newscenter_header, null);
        //添加头布局
        addHeaderView(headerView);
    }

    //获取头布局
    public static View getHeaderView() {
        return headerView;
    }
}
