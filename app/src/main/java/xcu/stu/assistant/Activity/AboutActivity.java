package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.version;
import xcu.stu.assistant.utils.callback.toastUtil;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;

public class AboutActivity extends Activity {
    private ImageView back;//顶部回退按钮
    private TextView location;//顶部指示位置
    private TextView current_version;//显示当前版本
    private TextView check_update;//检查更新
    private TextView suggest;//反馈建议
    private Context mContext;
    private int currentCode;//当前版本号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = AboutActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化视图界面
    private void initView() {
        color_same_to_app.setTopColorSameToApp(AboutActivity.this, R.color.main_color);
        setContentView(R.layout.activity_about);
        back = (ImageView) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);
        current_version = (TextView) findViewById(R.id.current_version);
        check_update = (TextView) findViewById(R.id.check_update);
        suggest = (TextView) findViewById(R.id.suggest);
    }

    //初始化数据
    private void initData() {
        location.setVisibility(View.VISIBLE);
        location.setText("关于助手");
        initCurrentVersuion();
    }

    //初始化监听事件
    private void initListener() {
        //顶部回退按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        //检查更新按钮
        check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressdialogUtil.showDialog(mContext, "正在检查更新，新稍候...");
                BmobQuery<version> query = new BmobQuery<version>();
                // 按时间降序查询
                query.order("-createdAt");
                query.findObjects(mContext, new FindListener<version>() {
                    @Override
                    public void onSuccess(List<version> list) {
                        //获取最新版
                        final version newestVersion = list.get(0);
                        Log.d("kydiwen", newestVersion.getVersionName());
                        progressdialogUtil.cancelDialog();
                        if (newestVersion.getVersionCode() == currentCode) {
                            toastUtil.show(mContext, "已是最新版本");
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("提示信息");
                            builder.setMessage("有新版本，点击确定下载更新");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    BmobFile apk = newestVersion.getApk();
                                    String url = apk.getFileUrl(mContext);
                                    //创建文件保存路径
                                    File FileDirs = new File(Environment.getExternalStorageDirectory(),
                                            "xcustuassistant/apk");
                                    if (!FileDirs.exists()) {
                                        FileDirs.mkdirs();
                                    }
                                    String path = Environment.getExternalStorageDirectory()
                                            + "/xcustuassistant/apk/许院学梓助手" + newestVersion.getVersionName
                                            () + ".apk";
                                    HttpUtils utils = new HttpUtils();
                                    HttpHandler handler = utils.download(url, path,
                                            true,//如果目标文件存在，继续断点下载
                                            true,//如果从服务器获取到文件名，下载完成后重新命名
                                            new RequestCallBack<File>() {
                                                ProgressDialog progressDialog;

                                                @Override
                                                public void onStart() {
                                                    super.onStart();
                                                    progressDialog = new ProgressDialog(mContext);
                                                    progressDialog.setProgressStyle(ProgressDialog
                                                            .STYLE_SPINNER);
                                                    progressDialog.setTitle("正在下载");
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();
                                                }

                                                @Override
                                                public void onLoading(long total, long current, boolean
                                                        isUploading) {
                                                    //格式化数据
                                                    DecimalFormat df = new DecimalFormat("#.0");
                                                    String pro = df.format(100 * (double) current / (double)
                                                            total);
                                                    String totalSize = df.format(total / (double) 1024 /
                                                            (double)
                                                                    1024) + "M";
                                                    progressDialog.setMessage(pro + "%" + "(" + totalSize +
                                                            ")");
                                                }

                                                @Override
                                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                                    toastUtil.show(mContext, "下载成功");
                                                    progressDialog.dismiss();
                                                    File file = responseInfo.result;
                                                    //安装更新
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(file),
                                                            "application/vnd.android.package-archive");
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onFailure(HttpException error, String msg) {
                                                    toastUtil.show(mContext, "下载失败，请检查网络连接后再试");
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });
        //反馈建议按钮
        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SuggestActivity.class);
                startActivity(intent);
            }
        });
    }

    //显示当前版本号
    private void initCurrentVersuion() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            currentCode = info.versionCode;
            current_version.setText("当前版本：" + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
