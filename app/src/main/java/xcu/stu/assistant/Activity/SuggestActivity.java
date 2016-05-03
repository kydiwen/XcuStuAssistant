package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.suggest;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;

/**
 * 反馈建议界面，发送反馈数据到服务器
 * 孙文权   2016年5月3日
 */
public class SuggestActivity extends Activity {
    private ImageView back;
    private TextView location;
    private Context mContext;
    private EditText mSuggest;
    private Button ensure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        mContext = SuggestActivity.this;
        color_same_to_app.setTopColorSameToApp(SuggestActivity.this, R.color.main_color);
        setContentView(R.layout.activity_suggest);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        mSuggest = (EditText) findViewById(R.id.suggest);
        ensure = (Button) findViewById(R.id.ensure);
    }

    //初始化数据
    private void initData() {
        location.setVisibility(View.VISIBLE);
        location.setText("反馈建议");
    }

    //初始化监听事件
    private void initListener() {
        //顶部回退按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //提交建议
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mSuggest.getText().toString())) {
                    toastUtil.show(mContext, "请输入您的宝贵建议");
                } else {
                    String message = mSuggest.getText().toString();
                    suggest s = new suggest();
                    s.setSuggest(message);
                    s.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toastUtil.show(mContext, "上传成功");
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }
        });
    }
}
