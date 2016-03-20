package xcu.stu.assistant.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import xcu.stu.assistant.DB.ClassSqliteOpenHelper;

/**
 * Created by 孙文权 on 16-3-20.
 * 获取班级列表的类
 */
public class classesList {
    public static Cursor getClasses(Context context) {
        SQLiteDatabase database = ClassSqliteOpenHelper.getInstanse(context).getWritableDatabase();
        Cursor cursor = database.query(ClassSqliteOpenHelper.CLASSES_TABLE, null, null, null, null, null,
                null);
        //返回查询到的数据
        return cursor;
    }
}
