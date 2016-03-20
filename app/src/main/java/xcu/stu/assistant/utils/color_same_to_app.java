package xcu.stu.assistant.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import xcu.stu.assistant.R;
import xcu.stu.assistant.custom.SystemBarTintManager;

/**
 * Created by sunwenquan on 16-3-20.
 */
public class color_same_to_app {
    //设置状态栏颜色与app一直
    public static void setTopColorSameToApp(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, context);
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.main_color);//通知栏所需颜色
        }
    }

    @TargetApi(19)
    public static void setTranslucentStatus(boolean on, Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
