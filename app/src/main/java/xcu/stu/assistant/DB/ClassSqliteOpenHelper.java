package xcu.stu.assistant.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 孙文权 on 2016/3/20.
 * 用来保存班级数据
 */
public class ClassSqliteOpenHelper extends SQLiteOpenHelper {
    private static Context mContext;
    public static final String CLASSES_TABLE = "班级表";//存放班级表名称的表
    public static final String CLASS_NAME = "班级名称";//班级名称列
    public static final String STU_NUM = "班级人数";//班级人数列
    String CREATE_NAME_TABLE = "create table " + CLASSES_TABLE + "(_id integer primary key autoincrement," +
            CLASS_NAME + " text," + STU_NUM + " integer" + ")";

    private ClassSqliteOpenHelper() {
        super(mContext, "Class.db", null, 1);
    }

    //单例模式
    public static ClassSqliteOpenHelper getInstanse(Context context) {
        mContext = context;
        ClassSqliteOpenHelper helper = new ClassSqliteOpenHelper();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建班级名称，用来存放不同的班级名称与人数
        db.execSQL(CREATE_NAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
