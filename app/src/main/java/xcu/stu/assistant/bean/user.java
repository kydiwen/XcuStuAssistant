package xcu.stu.assistant.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by 孙文权 on 2016/4/8.
 * 用户实体类
 */
public class user extends BmobUser {
    private  String qqNumber;

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }
}
