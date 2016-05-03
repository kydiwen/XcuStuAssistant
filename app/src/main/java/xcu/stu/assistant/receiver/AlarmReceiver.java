package xcu.stu.assistant.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import xcu.stu.assistant.Activity.MainActivity;
import xcu.stu.assistant.Activity.WeatherDetailActivity;
import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.utils.callback.jsonCallback;
import xcu.stu.assistant.utils.requestUtil;

/**
 * Created by 孙文权 on 2016/3/8.
 * 收到广播后自动更新天气信息，并震动提示用户
 */
public class AlarmReceiver extends BroadcastReceiver {

    private NotificationCompat.Builder mBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        //收到广播后更新天气信息，并弹出通知
        getWeatherInfo(context);
    }

    //获取天气信息，并弹出通知
    private void getWeatherInfo(final Context context) {
        //天气信息请求地址
        try {
            String currentCity = context.getSharedPreferences(myConstant.SP_NAME, Context.MODE_PRIVATE)
                    .getString(myConstant.CURRENT_CITY, "");
            String url = context.getResources().getString(R.string.bd_weather_url) + URLEncoder.encode
                    (currentCity, "utf-8") + "&output=json&ak=" + context.getResources().getString(R.string
                    .my_bd_ak) + "&mcode=" + context.getResources().getString(R.string.bd_mcode);
            requestUtil.jsonRequest(url, new jsonCallback() {
                @Override
                public void getJson(JSONObject response) {
                    try {
                        mBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                                .setSmallIcon(R.drawable.xculogo)
                                .setAutoCancel(true)
                                .setContentTitle(response.getJSONArray("results").getJSONObject
                                        (0).getJSONArray("weather_data").getJSONObject(0).getString
                                        ("weather") + "   " + response.getJSONArray("results").getJSONObject
                                        (0).getJSONArray("weather_data").getJSONObject(0).getString
                                        ("temperature"))
                                .setContentText(response
                                        .getJSONArray("results").getJSONObject(0).getString("currentCity"));
                        mBuilder.setPriority(Notification.PRIORITY_HIGH);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context
                                .getApplicationContext());
                        stackBuilder.addParentStack(WeatherDetailActivity.class);
                        Intent intent1 = new Intent(context, WeatherDetailActivity.class);
                        intent1.putExtra(MainActivity.WEATHER_GIVE, response.toString());
                        stackBuilder.addNextIntent(intent1);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent);
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context
                                .NOTIFICATION_SERVICE);
                        manager.notify(0, mBuilder.build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
