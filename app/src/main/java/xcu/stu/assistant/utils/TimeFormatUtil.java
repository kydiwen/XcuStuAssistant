package xcu.stu.assistant.utils;

/**
 * Created by 孙文权 on 2016/4/27.
 * 格式化时间方法
 * 给定时间long值，格式化为可读时间
 */
public class TimeFormatUtil {
    public static String format(int duration) {
        String minuteString;
        String millionString;
        int oneMillion = 1000;//一秒时长
        int oneMinute = 60 * 1000;//一分钟时长
        int minute = duration / oneMinute;//视频分钟数
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = minute + "";
        }
        int million = duration % oneMinute / oneMillion;//剩余秒数
        if (million < 10) {
            millionString = "0" + million;
        } else {
            millionString = million + "";
        }
        return minuteString + ":" + millionString;
    }
}
