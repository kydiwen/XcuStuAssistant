package xcu.stu.assistant.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.json.JSONObject;

import java.net.URLEncoder;

import xcu.stu.assistant.Activity.MainActivity;
import xcu.stu.assistant.Activity.WeatherDetailActivity;
import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.receiver.AlarmReceiver;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.requestUtil;

/**
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                getWeatherInfo();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60*60*1000; // 这是一小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //获取天气信息，并弹出通知
    private void getWeatherInfo() {
        //天气信息请求地址
        try {
            String currentCity = getSharedPreferences(myConstant.SP_NAME, Context.MODE_PRIVATE).getString
                    (myConstant.CURRENT_CITY, "");
            String url = getResources().getString(R.string.bd_weather_url) + URLEncoder.encode(currentCity,
                    "utf-8") + "&output=json&ak=" + getResources().getString(R.string.my_bd_ak)
                    + "&mcode=" + getResources().getString(R.string.bd_mcode);
            requestUtil.jsonRequest(url, new jsonCallback() {
                @Override
                public void getJson(JSONObject response) {
                    try {
                        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.xculogo)
                                .setAutoCancel(true)
                                .setContentTitle(response.getJSONArray("results").getJSONObject(0)
                                        .getJSONArray("weather_data").getJSONObject(0).getString("weather"))
                                .setContentText(response.getJSONArray("results").getJSONObject(0).getString
                                        ("currentCity"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(WeatherDetailActivity.class);
                        Intent intent1 = new Intent(weatherUpdateService.this, WeatherDetailActivity.class);
                        intent1.putExtra(MainActivity.WEATHER_GIVE, response.toString());
                        stackBuilder.addNextIntent(intent1);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent
                                .FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent);
                        NotificationManager manager = (NotificationManager) getSystemService(Context
                                .NOTIFICATION_SERVICE);
                        manager.notify(0, mBuilder.build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
