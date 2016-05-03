package xcu.stu.assistant.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 孙文权 on 2016/5/3.
 * 检查更新实体类
 */
public class version extends BmobObject {
    private int versionCode;//软件版本号
    private String versionName;//软件版本名称
    private BmobFile Apk;//新版本安装包

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public BmobFile getApk() {
        return Apk;
    }
}
