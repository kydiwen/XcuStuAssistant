package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.classbean;

/**
 *
 * 点到详情页面
 * 显示点到每个人记录
 * 孙文权  2015年3月25日
 *
 */
public class RecordDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        //获取传递的数据
        Intent intent=getIntent();
        classbean bean= (classbean) intent.getSerializableExtra(classes_list_Activity.CLASS_CLICKED);
        Log.d("kydiwen",bean.getClassName());
    }
}
