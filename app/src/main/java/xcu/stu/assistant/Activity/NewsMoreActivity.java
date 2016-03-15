package xcu.stu.assistant.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.TitlePageIndicator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import xcu.stu.assistant.Constant.myConstant;
import xcu.stu.assistant.R;
import xcu.stu.assistant.bean.indicator;
import xcu.stu.assistant.bean.news;
import xcu.stu.assistant.custom.SystemBarTintManager;
import xcu.stu.assistant.utils.callback.HtmlCallback;
import xcu.stu.assistant.utils.requestUtil;
import xcu.stu.assistant.widget.LazyViewPager;
import xcu.stu.assistant.widget.upToLoadLayout;

/**
 * 全部新闻信息显示界面，可实现不同类型信息的切换
 */
public class NewsMoreActivity extends Activity {
    private ImageView back;//回退按钮
    private TextView news_type;//新闻类型显示
    private Context mContext;//全局可用的context对象
    private String indicatorUrl;//导航栏的获取链接
    private ArrayList<indicator> indicators = new ArrayList<indicator>();//导航栏数据
    private static final int INDICATORDATA = 0;//获取到导航栏消息
    private TitlePageIndicator pagerindicator;//导航栏
    private LazyViewPager viewpager;//新闻信息容器
    private ArrayList<news> currentNewsData = new ArrayList<news>();//当前页面新闻列表
    private String currentLoadUrl;//当前上拉加载链接
    private static final int LOADNEWS = 1;//显示新闻数据
    private boolean existNextPage = true;//判断是否存在下一页
    //用来处理更新ui操作的消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INDICATORDATA:
                    Document indicatorDocument = (Document) msg.obj;
                    //初始化viewpager
                    Element indicatorElement = indicatorDocument.getElementsByClass("left-sec-menu").get(0);
                    initViewpager(indicatorElement);
                    break;
                case LOADNEWS:
                    Document newsDocument = (Document) msg.obj;
                    //初始化新闻列表
                    //新闻列表数据
                    Element newsElement = newsDocument.getElementsByClass("news-list").get(1);
                    //获取是上拉加载链接
                    currentLoadUrl = mContext.getResources().getString(R.string.single_news) + "/list.jsp"
                            + newsDocument.getElementsByClass("Next").select("a").attr("href");
                    if (currentLoadUrl.length() > 40) {
                        existNextPage = true;
                    } else {
                        existNextPage = false;
                    }
                    initNewslistData(newsElement);
                    break;
            }
        }
    };
    private ListView news_list;//新闻列表控件
    private NewsMoreActivity.newsAdapter newsAdapter;//新闻列表适配器
    private upToLoadLayout refresh;//刷新组件
    private boolean upToloadState = false;//判断是否是上拉加载动作


    //初始化新闻列表数据
    private void initNewslistData(Element element) {
        if (upToloadState) {
            //加载完成，取消加载状态
            upToloadState = false;
            refresh.setLoading(false);
        } else {
            currentNewsData.clear();
        }
        Elements newslist = element.select("li");
        for (Element e : newslist) {
            news mNews = new news();
            mNews.setNewsTitle(e.select("a").get(1).attr("title"));
            mNews.setNewsUrl(mContext.getResources().getString(R.string.single_news) + e.select("a").get(1)
                    .attr("href"));
            mNews.setNewsTime(e.getElementsByClass("c50962_date").get(0).text());
            currentNewsData.add(mNews);
            newsAdapter.notifyDataSetChanged();
        }
        //停止刷新
        refresh.setRefreshing(false);
    }

    //初始化viewpager的数据
    private void initViewpager(Element element) {
        //解析html，为当行栏数据赋值
        parseHtml_initIndicators(element);
        //设置适配器
        viewpager.setAdapter(new myPagerAdapter());
        pagerindicator.setViewPager(viewpager);
    }

    //解析html数据，为导航栏数据赋值
    private void parseHtml_initIndicators(Element element) {
        //获取导航栏数据
        for (Element e : element.select("li")) {
            indicator in = new indicator();
            in.setTitle(e.select("a").attr("title"));
            in.setUrl(mContext.getResources().getString(R.string.single_news) + e.select("a").attr("href"));
            indicators.add(in);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化布局
    private void initView() {
        mContext = NewsMoreActivity.this;
        setTopColorSameToApp();
        setContentView(R.layout.activity_news_more);
        back = (ImageView) findViewById(R.id.back);
        news_type = (TextView) findViewById(R.id.news_type);
        pagerindicator = (TitlePageIndicator) findViewById(R.id.pagerindicator);
        viewpager = (LazyViewPager) findViewById(R.id.viewpager);
    }

    //初始化数据
    private void initData() {
        news newsToLoad = (news) getIntent().getSerializableExtra(myConstant.NEWS_GIVE);
        //显示当前新闻数据类型
        news_type.setText(newsToLoad.getNewsType());
        //初始化数据
        initViewpagerAndIndicator(newsToLoad);
    }

    //初始化监听事件
    private void initListener() {
        //为回退按钮设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭当前页面
                finish();
            }
        });
        //监听viewpager的滑动事件
        viewpager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //设置状态栏颜色与app一直
    private void setTopColorSameToApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.main_color);//通知栏所需颜色
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //根部不同的新闻类型加载不同的新闻数据，为viewpager的导航栏加载不同的数据
    private void initViewpagerAndIndicator(news mNews) {
        String newsType = mNews.getNewsType();
        if (newsType.equals("通知通告")) {
            initNotify();
        } else if (newsType.equals("新闻动态")) {
            initDynatic();
        } else if (newsType.equals("外媒报道")) {
            initMedia();
        }
    }

    //当新闻数据类型是通知通告时加载的数据
    private void initNotify() {
        //http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1017
        //加载路径
        indicatorUrl = "http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1017";
        //加载数据
        loadIndicatorData(indicatorUrl);
    }

    //当新闻数据类型是新闻动态时加载的数据
    private void initDynatic() {
        //http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1003
        //加载路径
        indicatorUrl = "http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1003";
        //加载数据
        loadIndicatorData(indicatorUrl);
    }

    //当新闻数据类型是外媒报道是加载的数据
    private void initMedia() {
        //加载路径
        //http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1024
        indicatorUrl = "http://news.xcu.edu.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1024";
        //加载数据
        loadIndicatorData(indicatorUrl);
    }

    //获取indicator数据
    private void loadIndicatorData(String url) {
        requestUtil.requestHtmlData(url, new HtmlCallback() {
            @Override
            public void getHtml(Document response) {
                Message message = new Message();
                message.what = INDICATORDATA;
                message.obj = response;
                handler.sendMessage(message);
            }
        });
    }

    //viewpager的适配器
    class myPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return indicators.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.newsmore_item, null);
            refresh = (upToLoadLayout) view.findViewById(R.id.refresh);
            //进刷新组件设置为正在刷新的状态，加载完成后停止刷新状态
            refresh.setRefreshing(true);
            news_list = (ListView) view.findViewById(R.id.news_list);
            newsAdapter = new newsAdapter();
            //设置适配器
            news_list.setAdapter(newsAdapter);
            String url = indicators.get(position).getUrl();//获取新闻请求链接
            requestUtil.requestHtmlData(url, new HtmlCallback() {
                @Override
                public void getHtml(Document response) {
                    Message message = new Message();
                    message.what = LOADNEWS;
                    message.obj = response;
                    handler.sendMessage(message);
                }
            });
            initIndicatorListener(refresh, news_list);
            container.addView(view);
            return view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return indicators.get(position).getTitle();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //新闻列表适配器
    class newsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return currentNewsData.size();
        }

        @Override
        public Object getItem(int position) {
            return currentNewsData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.news_more_newsitem, null);
            TextView news_title = (TextView) view.findViewById(R.id.news_title);
            TextView news_time = (TextView) view.findViewById(R.id.news_time);
            //显示数据
            news mNews = currentNewsData.get(position);
            news_title.setText(mNews.getNewsTitle());
            news_time.setText(mNews.getNewsTime());
            return view;
        }
    }

    //初始化每个新闻类型页面监听事件
    private void initIndicatorListener(final upToLoadLayout refresh, ListView listView) {
        //为刷新组件设置上拉加载监听事件
        refresh.setOnLoadListener(new upToLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                //判断是否存在下一页
                if (existNextPage) {
                    //设置为上拉加载状态
                    upToloadState = true;
                    refresh.setLoading(true);
                    //请求下一页新闻数据
                    requestUtil.requestHtmlData(currentLoadUrl, new HtmlCallback() {
                        @Override
                        public void getHtml(Document response) {
                            Message message = new Message();
                            message.what = LOADNEWS;
                            message.obj = response;
                            handler.sendMessage(message);
                        }
                    });
                } else {
                    refresh.setLoading(false);
                    Toast toast = Toast.makeText(mContext, "无更多信息", Toast.LENGTH_SHORT);
                    //设置土司显示的位置
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        //为刷新组件设置下拉刷新监听事件
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //由于一般并无数据刷新，设置为假刷新状态
                //停止刷新状态
                refresh.setRefreshing(false);
            }
        });
        //为listview设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //打开单条新闻详细页面
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra(myConstant.NEWS_GIVE, currentNewsData.get(position));
                startActivity(intent);
            }
        });
    }
}
