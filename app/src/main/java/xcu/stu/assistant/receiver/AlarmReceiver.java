package xcu.stu.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xcu.stu.assistant.service.weatherUpdateService;

/**
 * Created by 孙文权 on 2016/3/7.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, weatherUpdateService.class);
        context.startService(i);
    }
}
