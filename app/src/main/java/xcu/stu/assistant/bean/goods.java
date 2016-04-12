package xcu.stu.assistant.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 孙文权 on 2016/4/12.
 * 商品信息bean类
 */
public class goods extends BmobObject {
    private  String title;//商品标题
    private  String category;//商品分类
    private  String price;//商品价格
    private  String phone;//电话号码
    private  String micromsg;//微信号码
    private  String qq;//qq号码
    private  String location;//交易地点
    private  String describe;//描述信息
    private String main_img;//主图
    private  String userid;//发布者id
    private ArrayList<String>imgs;//商品所有图片

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMicromsg() {
        return micromsg;
    }

    public void setMicromsg(String micromsg) {
        this.micromsg = micromsg;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getMain_img() {
        return main_img;
    }

    public void setMain_img(String main_img) {
        this.main_img = main_img;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }
}
