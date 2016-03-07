package xcu.stu.assistant.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 孙文权 on 2016/3/5.
 */
public class CitySqliteOpenHelper extends SQLiteOpenHelper {
    private static Context mContext;
    public static final String CITYTABLE = "citytable";//城市表
    public static final String PROVINVE_NAME = "provincename";//省份名称
    public static final String CITY_NAME = "cityname";//城市名称
    String CREATE_TABLE = "create  table " + CITYTABLE + "(_id integer  primary key autoincrement," +
            "" + PROVINVE_NAME + " text," + CITY_NAME + " text" + ")";

    private CitySqliteOpenHelper() {
        super(mContext, "City.db", null, 1);
    }

    //单例模式
    public static CitySqliteOpenHelper getInstanse(Context context) {
        mContext = context;
        CitySqliteOpenHelper helper = new CitySqliteOpenHelper();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);//创建数据库
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
