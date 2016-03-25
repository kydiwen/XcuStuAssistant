package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.news;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.callback.HtmlCallback;
import xcu.stu.assistant.utils.color_same_to_app;
import xcu.stu.assistant.utils.progressdialogUtil;
import xcu.stu.assistant.utils.requestUtil;

/**
 * 新闻数据详情页面
 * 显示详细新闻信息
 * 根据当前新闻信息类型
 * 加载普通文字信息，图片信息，以及视频信息
 * 2016年3月13日
 * 新闻详情页面新闻获取显示处理完成
 * 3月15日
 * 实现新闻视频信息的播放，对不同类型数据做了统一处理，节省了代码量，新闻模块完成
 */
public class NewsDetailActivity extends Activity {
    private TextView news_title;//新闻标题
    private news newsToLoad;//需要显示的新闻数据
    private ImageView back;//返回按钮
    private TextView newsfrom_time;//需要显示的新闻来源和发布时间
    private LinearLayout news_content_container;//新闻内容容器
    private static final int SHOWNEWS = 0;//显示新闻数据消息
    private Context mContext;//全局可用的context
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOWNEWS:
                    Document newsData = (Document) msg.obj;
                    //开始解析html数据
                    Elements newsElements = newsData.getElementsByClass("currentaddress").get(0).select("p");
                    //显示新闻来源和发布时间
                    newsfrom_time.setText(newsElements.get(0).select("span").get(1).text() + "  " +
                            newsElements.get(0).select("span").get(4).text());
                    //显示新闻数据
                    initNewsContent(newsElements);
                    break;
            }
        }
    };
    private RelativeLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = NewsDetailActivity.this;
        initView();
        initData();
        initListener();
    }

    //初始化监听事件
    private void initListener() {
        //为返回按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化视图界面
    private void initView() {
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(NewsDetailActivity.this,R.color.main_color);
        setContentView(R.layout.activity_classes_list_);
        //显示进度条
        progressdialogUtil.showDialog(mContext);
        setContentView(R.layout.activity_news_detail);
        news_title = (TextView) findViewById(R.id.news_title);
        newsfrom_time = (TextView) findViewById(R.id.newsfrom_time);
        news_content_container = (LinearLayout) findViewById(R.id.news_content_container);
        back = (ImageView) findViewById(R.id.back);
    }

    //初始化数据
    private void initData() {
        //初始化新闻数据
        newsToLoad = (news) getIntent().getSerializableExtra(myConstant.NEWS_GIVE);
        initNews();
    }


    //初始化普通新闻
    private void initNews() {
        //设置新闻标题为加粗字体
        news_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//字体加粗
        //显示新闻标题
        news_title.setText(newsToLoad.getNewsTitle());
        //加载数据
        loadNews();
    }

    //加载新闻数据
    private void loadNews() {
        //请求html数据
        requestUtil.requestHtmlData(newsToLoad.getNewsUrl(), new HtmlCallback() {
            @Override
            public void getHtml(Document response) {
                Message message = new Message();
                message.what = SHOWNEWS;
                message.obj = response;
                handler.sendMessage(message);
            }
        });
    }

    //显示新闻信息具体内容
    private void initNewsContent(Elements elements) {
        for (int i = 1; i < elements.size(); i++) {
            //去除上一条和下一条数据
            if (TextUtils.isEmpty(elements.get(i).attr("align"))) {
                if (elements.get(i).select("img") != null) {//当前是图片
                    String url = mContext.getResources().getString(R.string.news_url) + elements.get(i).select
                            ("img").attr("src");
                    ImageView imageView = (ImageView) View.inflate(mContext, R.layout.newsdetail_newsimg,
                            null);
                    //加载图片
                    loadImg(url, imageView);
                    news_content_container.addView(imageView);
                }
                //判断是否是视频信息
                if (!TextUtils.isEmpty(elements.get(i).select("script").attr("vurl"))) {
                    String videourl = mContext.getResources().getString(R.string.news_url) + elements.get(i)
                            .select("script").attr("vurl").substring(6);
                    RelativeLayout videoViewcontainer = (RelativeLayout) View.inflate(mContext, R.layout
                            .newsdetail_videoview, null);
                    final VideoView videoView = (VideoView) videoViewcontainer.findViewById(R.id.videoview);
                    //获取屏幕宽度，重新设置videview的宽度，否则无法显示
                    final WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int width = manager.getDefaultDisplay().getWidth();
                    params = new RelativeLayout.LayoutParams(width, width);
                    videoView.setLayoutParams(params);
                    MediaController controller = new MediaController(mContext);
                    videoView.setMediaController(controller);
                    videoView.setVideoURI(Uri.parse(videourl));
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.pause();
                        }
                    });
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            return true;
                        }
                    });
                    news_content_container.addView(videoViewcontainer);
                    TextView tv = new TextView(mContext);
                    tv.setText("点击播放视频");
                    tv.setTextSize(18);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    news_content_container.addView(tv);
                }
                if (!TextUtils.isEmpty(elements.get(i).text())) {
                    TextView tv = (TextView) View.inflate(mContext, R.layout.newsdetail_newsitem, null);
                    tv.setText("      " + elements.get(i).text().trim());
                    news_content_container.addView(tv);
                }
            }
        }
        //隐藏进度条
        progressdialogUtil.cancelDialog();
    }

    //加载图片
    private void loadImg(String url, final ImageView imageView) {
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                //显示图片
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
