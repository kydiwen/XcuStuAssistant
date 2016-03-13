package xcu.stu.assistant.Fragment.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import xcu.stu.assistant.Fragment.baseFragment;
import xcu.stu.assistant.R;
import xcu.stu.assistant.adapter.newscenter_newslistAdapter;
import xcu.stu.assistant.bean.imgNews;
import xcu.stu.assistant.bean.news;
import xcu.stu.assistant.utils.callback.BitmapCallback;
import xcu.stu.assistant.utils.callback.HtmlCallback;
import xcu.stu.assistant.utils.requestUtil;
import xcu.stu.assistant.widget.CustomViewPager;
import xcu.stu.assistant.widget.customListview;

/**
 * 创建新闻中心的控制器
 * 继承自baseFragment，并实现其抽象方法
 * 孙文权 on 2016/3/1.
 * 2016年3月13上午
 * 完成新闻中心界面布局，以及数据显示
 */
public class newsCenterController extends baseFragment {
    private customListview news_list;
    private ArrayList<news> newsList = new ArrayList<news>();//新闻数据
    private View headView;//listview的头布局
    private CustomViewPager autorun_img;//轮播图
    private ProgressBar news_loading;//新闻正在加载进度条
    private ArrayList<imgNews> imgNewses = new ArrayList<imgNews>();//图片新闻数据
    private autoAdapter adapter;
    private static final int UPDATE_NEWS = 0;//更新新闻信息
    private static final int IMG_RUN = 1;//设置图片轮播
    private int currentItem = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_NEWS://更新信息
                    Document response = (Document) msg.obj;
                    //顶部viewpager数据
                    Elements viewpagerelements = response.getElementsByClass("img-news");
                    //初始化viewpager
                    initHeaderView(viewpagerelements.get(0));
                    //初始化通知通告数据
                    //通知通告列表数据
                    Elements notifyElements = response.getElementsByClass("mt-news");
                    initNotify_list(notifyElements.get(0));
                    //初始化新闻动态数据
                    Elements dynamicElements = response.getElementsByClass("news-dt");
                    initNewsDynamic(dynamicElements.get(0).select("li"));
                    //初始化外媒报道数据
                    initMediaList(dynamicElements.get(0).select("li"));
                    //通知适配器数据获得更新
                    newscenter_newslistAdapter.notifyDataSetChanged();
                    //设置进度条不可见
                    news_loading.setVisibility(View.INVISIBLE);
                    break;
                case IMG_RUN:
                    autorun_img.setCurrentItem((autorun_img.getCurrentItem() + 1) % (imgNewses.size()));
                    break;
            }
        }
    };
    private newscenter_newslistAdapter newscenter_newslistAdapter;
    private String CurrentNewsType;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_newscenter, null);
        news_list = (customListview) view.findViewById(R.id.news_list);
        news_loading = (ProgressBar) view.findViewById(R.id.news_loading);
        //初始化头布局
        return view;
    }

    @Override
    protected void initData() {
        //初始化viewpager的数据
        getMainPageNews();
    }

    @Override
    protected void initListener() {

    }

    //初始化头布局
    private void initHeaderView(Element element) {
        //设置进度条可见
        news_loading.setVisibility(View.VISIBLE);
        //获取头布局的viewpager
        headView = news_list.getHeaderView();
        //获取viewpager对象
        autorun_img = (CustomViewPager) headView.findViewById(R.id.img_autorun);
        //为数据对象赋值
        for (int i = 0; i < element.getElementsByClass("c50987").size(); i++) {
            imgNews news = new imgNews();
            Element newsElement = element.getElementsByClass("c50987").get(i);
            news.setNews_url(newsElement.select("a").attr("href"));
            news.setNewstitle(newsElement.select("a").attr("title"));
            news.setImg_url(mContext.getResources().getString(R.string.news_url) + newsElement.select
                    ("img").attr("src"));
            imgNewses.add(news);
        }
        //设置适配器
        adapter = new autoAdapter();
        autorun_img.setAdapter(adapter);
        //为新闻列表设置适配器
        //为新闻数据设置适配器
        newscenter_newslistAdapter = new newscenter_newslistAdapter(newsList, R.layout
                .newscenter_newslist_item, mContext);
        news_list.setAdapter(newscenter_newslistAdapter);
        //开启定时器，设置图片轮播
        setimg_autorun();
    }

    //获取新闻主页数据
    private void getMainPageNews() {
        //访问链接
        String url = mContext.getResources().getString(R.string.news_url);
        requestUtil.requestHtmlData(url, new HtmlCallback() {
            @Override
            public void getHtml(Document response) {
                Message message = new Message();
                message.what = UPDATE_NEWS;
                message.obj = response;
                //发送消息，更新ui
                handler.sendMessage(message);
            }
        });
    }

    //初始化通知通告数据
    private void initNotify_list(Element element) {
        //需要的新闻列表数据
        Elements notifyList = element.getElementsByClass("winstyle51078").get(1).select("tr");
        //设置当前新闻数据类型为通知通告
        CurrentNewsType = "通知通告";
        for (int i = 0; i < notifyList.size(); i++) {
            news mNews = new news();
            //单条新闻数据
            Element newsItem = notifyList.get(i);
            mNews.setNewsType(CurrentNewsType);
            mNews.setNewsUrl(mContext.getResources().getString(R.string.news_url) + newsItem.select("a").attr
                    ("href"));
            mNews.setNewsTitle(newsItem.select("a").attr("title").trim());
            mNews.setNewsTime(newsItem.getElementsByClass("timestyle51078").text().trim());
            if (!TextUtils.isEmpty(newsItem.select("img").attr("src"))) {
                mNews.setGifUrl(mContext.getResources().getString(R.string.news_url) + newsItem.select
                        ("img").attr("src"));
            } else {
                mNews.setGifUrl("");
            }
            newsList.add(mNews);
        }
    }

    //初始化新闻动态数据
    private void initNewsDynamic(Elements elements) {
        //当前新闻数据类型为新闻动态
        CurrentNewsType = "新闻动态";
        //遍历所有节点
        for (Element e : elements) {
            //判断是否是新闻动态数据
            if (e.select("span").attr("class").equals("c51077_date")) {
                news mNews = new news();
                mNews.setNewsType(CurrentNewsType);
                mNews.setNewsTime("2016-" + e.select("span").text().trim());
                mNews.setNewsUrl(mContext.getResources().getString(R.string.news_url) + e.select("a").attr
                        ("href"));
                mNews.setNewsTitle(e.select("a").attr("title").trim());
                //判断是否是最新消息
                if (e.select("img") != null) {
                    mNews.setGifUrl(e.select("img").attr("src"));
                }
                newsList.add(mNews);
            }
        }
    }

    //初始化外媒报道数据
    private void initMediaList(Elements elements) {
        //当前新闻数据类型为新闻动态
        CurrentNewsType = "外媒报道";
        //遍历所有节点
        for (Element e : elements) {
            //判断是否是新闻动态数据
            if (e.select("span").attr("class").equals("c51075_date")) {
                news mNews = new news();
                mNews.setNewsType(CurrentNewsType);
                mNews.setNewsTime("2016-" + e.select("span").text().trim());
                mNews.setNewsUrl(mContext.getResources().getString(R.string.news_url) + e.select("a").attr
                        ("href"));
                mNews.setNewsTitle(e.select("a").attr("title").trim());
                //判断是否是最新消息
                if (e.select("img") != null) {
                    mNews.setGifUrl(e.select("img").attr("src"));
                }
                newsList.add(mNews);
            }
        }
    }

    //设置图片轮播
    private void setimg_autorun() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(IMG_RUN);
            }
            //延迟三秒，每三秒执行一次
        }, 3000, 3000);
    }

    //轮播图适配器
    class autoAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgNewses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = View.inflate(mContext, R.layout.imgnews_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgsnews_img);
            TextView title = (TextView) view.findViewById(R.id.imgnews_title);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            loadImg(imgNewses.get(position).getImg_url(), imageView);
            title.setText(imgNewses.get(position).getNewstitle());
            container.addView(view);
            //设置图片的点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击打开详情页面
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //加载图片
    private void loadImg(String url, final ImageView imageView) {
        requestUtil.getBitmap(url, new BitmapCallback() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                //对图片进行缩放后显示出来
                //获取屏幕宽度
                WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int screenWidth = manager.getDefaultDisplay().getWidth();
                //获取图片的宽高
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                //设置缩放比例
                float scale = screenWidth / bitmap.getWidth();
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix,
                        true);
                imageView.setImageBitmap(scaleBitmap);
            }
        });
    }
}
