package xcu.stu.assistant.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xcu.stu.assistant.utils.callback.toastUtil;

/**
 * Created by 孙文权 on 2016/4/6.
 * 监听蓝牙状态
 */
public class bluetoothStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取蓝牙状态
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:
                toastUtil.show(context, "蓝牙正在开启");
                break;
            case BluetoothAdapter.STATE_ON:
                toastUtil.show(context, "蓝牙设备已开启");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                toastUtil.show(context, "蓝牙设备正在关闭");
                break;
            case BluetoothAdapter.STATE_OFF:
                toastUtil.show(context, "蓝牙设备已关闭");
                break;
        }
    }
}
