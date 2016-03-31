package xcu.stu.assistant.bean;

/**
 * Created by 孙文权 on 2016/3/31.
 * 笑话bean类
 */
public class jokeBean {
    private  String content;//笑话内容
    private  String updatetime;//更新时间
    private  String imgurl;//图片链接

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
