package xcu.stu.assistant.utils.callback;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by 孙文权 on 2016/3/15.
 * 弹出土司的类
 */
public class toastUtil {
    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        //设置土司显示的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
