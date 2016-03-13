package xcu.stu.assistant.bean;

import java.io.Serializable;

/**
 * Created by 孙文权 on 2016/3/10.
 * 普通新闻数据bean类
 * 新闻数据类序列化 可使用intent传入下一个界面
 */
public class news implements Serializable {
    private String newsTitle;//新闻标题
    private String newsUrl;//新闻连接
    private String gifUrl;//最新新闻动画
    private String newsTime;//新闻时间
    private String newsType;//新闻类型
    private String imgUrl;//如果是图片新闻，设置图片加载连接

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

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
