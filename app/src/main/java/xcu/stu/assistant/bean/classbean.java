package xcu.stu.assistant.bean;

/**
 * Created by 孙文权 on 16-3-20.
 * 班级信息bean类，用来存储班级名称，班级人数
 */
public class classbean {
    private String className;//班级名称
    private int stuNum;//学生人数

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStuNum() {
        return stuNum;
    }

    public void setStuNum(int stuNum) {
        this.stuNum = stuNum;
    }
}
