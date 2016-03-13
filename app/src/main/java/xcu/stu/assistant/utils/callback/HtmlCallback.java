package xcu.stu.assistant.utils.callback;

import org.jsoup.nodes.Document;

/**
 * 将从网络获取的数据回调
 * Created by 孙文权 on 2016/3/9.
 */
public interface HtmlCallback {
    void getHtml(Document response);
}
