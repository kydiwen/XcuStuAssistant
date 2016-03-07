package xcu.stu.assistant.bean;

/**
 * 市级实体类
 * Created by 孙文权 on 2016/3/5.
 */
public class city {
    private String citynum;//城市编号
    private String cityname;//城市名称
    private String privincename;//所在省份名称

    public String getCitynum() {
        return citynum;
    }

    public void setCitynum(String citynum) {
        this.citynum = citynum;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getPrivincename() {
        return privincename;
    }

    public void setPrivincename(String privincename) {
        this.privincename = privincename;
    }
}
