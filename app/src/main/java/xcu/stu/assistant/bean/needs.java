package xcu.stu.assistant.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 孙文权 on 2016/4/13.
 * 需求信息类
 */
public class needs extends BmobObject {
    private  String location;//交易地点
    private  String qq;//qq号码
    private  String micromsg;//微信号码
    private  String phone;//电话号码
    private  String price;//价格范围
    private String  describe;//描述信息
    private  String userid;//发布者
    private  String title;//标题

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMicromsg() {
        return micromsg;
    }

    public void setMicromsg(String micromsg) {
        this.micromsg = micromsg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
