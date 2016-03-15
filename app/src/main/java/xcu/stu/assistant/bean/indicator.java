package xcu.stu.assistant.bean;

/**
 * Created by 孙文权 on 2016/3/14.
 * 所有新闻信息页面，导航栏bean类
 */
public class indicator {
    private String url;//当前位置新闻数据获取链接
    private String title;//当前导航栏标题

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
