package xcu.stu.assistant.bean;

/**
 * Created by 孙文权 on 2016/3/9.
 * 图片新闻bean类
 */
public class imgNews {
    private String newstitle;//新闻标题
    private String news_url;//新闻连接地址
    private String img_url;//图片链接地址

    public String getNewstitle() {
        return newstitle;
    }

    public void setNewstitle(String newstitle) {
        this.newstitle = newstitle;
    }

    public String getNews_url() {
        return news_url;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
