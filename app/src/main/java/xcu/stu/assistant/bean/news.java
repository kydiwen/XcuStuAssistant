package xcu.stu.assistant.bean;

/**
 * Created by 孙文权 on 2016/3/10.
 * 普通新闻数据bean类
 */
public class news {
    private String newsTitle;//新闻标题
    private String newsUrl;//新闻连接
    private String gifUrl;//最新新闻动画
    private String newsTime;//新闻时间
    private String newsType;//新闻类型

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }
}
