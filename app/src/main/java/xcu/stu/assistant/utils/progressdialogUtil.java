package xcu.stu.assistant.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by 孙文权 on 2016/3/16.
 * 弹出对话框的类
 */
public class progressdialogUtil {
    private static ProgressDialog dialog;

    //显示进度条的方法
    public static void showDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("正在加载....");
        dialog.setCancelable(false);
        dialog.show();
    }

    //隐藏进度条的方法
    public static void cancelDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
