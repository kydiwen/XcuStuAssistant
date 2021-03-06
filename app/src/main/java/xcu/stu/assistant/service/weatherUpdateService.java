package xcu.stu.assistant.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import xcu.stu.assistant.receiver.AlarmReceiver;

/**
 * 使用定时器实现在后台自动更新天气，并在固定时间弹出通知提醒用户
 * Created by 孙文权 on 2016/3/7.
 */
public class weatherUpdateService extends Service {

    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();//获取calendar对象，用于设定执行的时间
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;
        Intent receivewIntent = new Intent(this, AlarmReceiver.class);
        //定义延迟执行的intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receivewIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        //定义定时器
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //定时器执行的周期
        long oneDay = 24 * 60 * 60 * 1000;
        //设置定时器重复执行
        manager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, oneDay, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

}
