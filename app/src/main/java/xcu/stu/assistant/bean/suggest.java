package xcu.stu.assistant.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 孙文权 on 2016/5/3.
 * 用户反馈意见实体类
 */
public class suggest extends BmobObject {
    private String suggest;

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }
}
