package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.news;
import xcu.stu.assistant.utils.MyThumbUtil;
import xcu.stu.assistant.utils.TimeFormatUtil;
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
    private ArrayList<Bitmap> imgs_todeliver = new ArrayList<Bitmap>();//向图片详情页面传送的数据
    private TextView locaton;
    private  ImageView share;//分享按钮
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
                    newsfrom_time.requestFocus();
                    //显示新闻数据
                    initNewsContent(newsElements);
                    break;
            }
        }
    };

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
        //共享按钮点击事件
        share.setVisibility(View.VISIBLE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String title=newsToLoad.getNewsTitle();
                String url=newsToLoad.getNewsUrl();
                intent.putExtra(Intent.EXTRA_SUBJECT,title);
                intent.putExtra(Intent.EXTRA_TEXT,title+url);
                startActivity(Intent.createChooser(intent,"共享新闻"));
            }
        });
    }

    //初始化视图界面
    private void initView() {
        //改变状态栏颜色与app风格一致
        color_same_to_app.setTopColorSameToApp(NewsDetailActivity.this, R.color.main_color);
        setContentView(R.layout.activity_classes_list_);
        //显示进度条
        progressdialogUtil.showDialog(mContext, "正在加载...");
        setContentView(R.layout.activity_news_detail);
        news_title = (TextView) findViewById(R.id.news_title);
        newsfrom_time = (TextView) findViewById(R.id.newsfrom_time);
        news_content_container = (LinearLayout) findViewById(R.id.news_content_container);
        back = (ImageView) findViewById(R.id.back);
        locaton = (TextView) findViewById(R.id.location);
        share= (ImageView) findViewById(R.id.share);
    }

    //初始化数据
    private void initData() {
        //初始化新闻数据
        newsToLoad = (news) getIntent().getSerializableExtra(myConstant.NEWS_GIVE);
        initNews();
        locaton.setVisibility(View.VISIBLE);
        locaton.setText("新闻详情");
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNewsContent(Elements elements) {
        for (int i = 1; i < elements.size(); i++) {
            //去除上一条和下一条数据
            if (TextUtils.isEmpty(elements.get(i).attr("align"))) {
                //加载图片信息
                if (elements.get(i).select("img") != null) {//当前是图片
                    String url = mContext.getResources().getString(R.string.news_url) + elements.get(i).select
                            ("img").attr("src");
                    loadImg(url);
                }
                //加载视频信息
                if (!TextUtils.isEmpty(elements.get(i).select("script").attr("vurl"))) {
                    String videourl = mContext.getResources().getString(R.string.news_url) + elements.get(i)
                            .select("script").attr("vurl").substring(6);
                    loadVideo(videourl);
                }
                //加载文字信息
                if (!TextUtils.isEmpty(elements.get(i).text()) && elements.get(i).text().length() > 1) {
                    elements.get(i).text().replace("&nbsp;", "");
                    loadText(elements.get(i).text());
                }
            }
        }
        //隐藏进度条
        progressdialogUtil.cancelDialog();
    }

    //加载文字信息
    private void loadText(String message) {
        TextView tv = (TextView) View.inflate(mContext, R.layout.newsdetail_newsitem, null);
        tv.setText(message);
        news_content_container.addView(tv);
    }

    //加载图片信息
    private void loadImg(String url) {
        final ImageView imageView = (ImageView) View.inflate(mContext, R.layout.newsdetail_newsimg,
                null);
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                imgs_todeliver.add(bitmap);
                //显示图片
                imageView.setImageBitmap(bitmap);
            }
        });
        news_content_container.addView(imageView);
    }

    //加载视频信息
    private void loadVideo(final String url) {
        final View view = View.inflate(mContext, R.layout
                .newsdetail_videoview, null);
        final VideoView videoView = (VideoView) view.findViewById(R.id.videoview);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        final ImageView pause = (ImageView) view.findViewById(R.id.pause);
        final ImageView img_thumb = (ImageView) view.findViewById(R.id.img_thumb);
        final SeekBar time_to = (SeekBar) view.findViewById(R.id.time_to);
        videoView.setVideoURI(Uri.parse(url));
        //获取视频缩略图
        new myAsynsTask(url, img_thumb, progressBar).execute();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //控制视频的播放
                videoController(videoView, pause, time_to, img_thumb, view);
            }
        });
        news_content_container.addView(view);
    }

    //控制视频播放方法封装
    private void videoController(final VideoView videoView, final ImageView pause, final SeekBar time_to,
                                 final ImageView thumb_img, final View view) {
        //显示视频时常
        TextView timeAll = (TextView) view.findViewById(R.id.time_all);
        timeAll.setText(TimeFormatUtil.format(videoView.getDuration()));
        final TextView timeEsc = (TextView) view.findViewById(R.id.time_esc);
        RelativeLayout parent_layout= (RelativeLayout) view.findViewById(R.id.parent_layout);
        //设置seekbar长度为视频时常
        time_to.setMax(videoView.getDuration());
        final int UPDATE_TIME = 1;
        final  int CANCELCONTROLL=2;
        final LinearLayout controllContain= (LinearLayout) view.findViewById(R.id.controll_container);
        final Handler time_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        timeEsc.setText(TimeFormatUtil.format(videoView.getCurrentPosition()));
                        time_to.setProgress(videoView.getCurrentPosition());
                        break;
                    case  CANCELCONTROLL:
                        if(pause.getVisibility()==View.VISIBLE&&videoView.isPlaying()){
                            pause.setVisibility(View.INVISIBLE);
                            controllContain.setVisibility(View.INVISIBLE);
                        }
                        break;
                }
            }
        };
        //控制视频播放
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏缩略图
                thumb_img.setVisibility(View.INVISIBLE);
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pause.setImageDrawable(getResources().getDrawable(R.drawable.media_play));
                } else {
                    videoView.start();
                    pause.setImageDrawable(getResources().getDrawable(R.drawable.media_pause));
                }
            }
        });
        //开启新线程，更新视频进度
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATE_TIME;
                time_handler.sendMessage(message);
                time_handler.postDelayed(this, 1000);
            }
        };
        //实现每秒执行
        time_handler.postDelayed(runnable, 1000);
        //通过seekbar控制视频进度
        time_to.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //更新已播放视频时常
                timeEsc.setText(TimeFormatUtil.format(progress));
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //监听videoview的播放完成事件
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完成，显示视频缩略图
                thumb_img.setVisibility(View.VISIBLE);
                //进度条归零
                videoView.seekTo(0);
                //显示播放图标
                pause.setImageDrawable(getResources().getDrawable(R.drawable.media_play));
            }
        });
        //设置videoview点击事件
        parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pause.getVisibility()==View.VISIBLE){
                    //隐藏控制面板
                    pause.setVisibility(View.INVISIBLE);
                    controllContain.setVisibility(View.INVISIBLE);
                }else {
                    //显示控制面板
                    pause.setVisibility(View.VISIBLE);
                    controllContain.setVisibility(View.VISIBLE);
                }
            }
        });
        //两秒自动隐藏控制面板
        Runnable cancelRun=new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=CANCELCONTROLL;
                time_handler.sendMessage(message);
                time_handler.postDelayed(this,5000);
            }
        };
        time_handler.postDelayed(cancelRun,5000);
    }

    //异步执行缩略图下载
    class myAsynsTask extends AsyncTask<Void, Void, Bitmap> {
        private String thumbUrl;
        private ImageView thumbImg;
        private ProgressBar MprogressBar;

        public myAsynsTask(String url, ImageView imageView, ProgressBar progressBar) {
            thumbUrl = url;
            thumbImg = imageView;
            MprogressBar = progressBar;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return MyThumbUtil.createVideoThumbnail(thumbUrl, 350, 198);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            thumbImg.setImageBitmap(bitmap);
            MprogressBar.setVisibility(View.INVISIBLE);
        }
    }
}
