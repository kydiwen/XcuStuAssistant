package xcu.stu.assistant.utils.callback;

import org.json.JSONObject;

/**
 * 创建用于json数据回掉的接口
 * Created by 孙文权 on 2016/3/2.
 */
public interface jsonCallback {
    void getJson(JSONObject response);
}
