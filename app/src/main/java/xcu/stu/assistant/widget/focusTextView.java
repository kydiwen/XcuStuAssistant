package xcu.stu.assistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 孙文权
 * 16年3月20日
 * 获取焦点的textview
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

    //始终获取焦点，实现滚动效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
