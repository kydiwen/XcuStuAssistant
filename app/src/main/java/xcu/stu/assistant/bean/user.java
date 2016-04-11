package xcu.stu.assistant.bean;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 孙文权 on 2016/4/8.
 * 用户实体类
 */
public class user extends BmobUser {
    private  String qqNumber;//qq号码
    private BmobFile user_img;//用户头像
    private  List<String>my_ershou;//我发布的二手商品

    public List<String> getMy_ershou() {
        return my_ershou;
    }

    public void setMy_ershou(List<String> my_ershou) {
        this.my_ershou = my_ershou;
    }

    public BmobFile getUser_img() {
        return user_img;
    }

    public void setUser_img(BmobFile user_img) {
        this.user_img = user_img;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }
}
