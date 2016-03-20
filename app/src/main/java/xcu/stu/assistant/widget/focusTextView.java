package xcu.stu.assistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sunwenquan on 16-3-20.
 */
public class focusTextView extends TextView {
    public focusTextView(Context context) {
        super(context);
    }

    public focusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public focusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public focusTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //    始终获取焦点，实现滚动效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
