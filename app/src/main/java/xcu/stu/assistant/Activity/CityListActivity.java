package xcu.stu.assistant.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xcu.stu.assistant.DB.CitySqliteOpenHelper;
import xcu.stu.assistant.R;
import xcu.stu.assistant.adapter.SortAdapter;
import xcu.stu.assistant.bean.SortModel;
import xcu.stu.assistant.bean.city;
import xcu.stu.assistant.custom.CharacterParser;
import xcu.stu.assistant.custom.PinyinComparator;
import xcu.stu.assistant.widget.SideBar;

/**
 * 2016年3月7日
 * 城市选择界面
 * 实现微信联系人界面下效果
 */
public class CityListActivity extends Activity {
    public static final String RETURN_DATA = "city_return";
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private EditText mClearEditText;
    private Context mContext;//全局可用的context对象
    private CitySqliteOpenHelper helper;//获取数据库帮助类
    private SQLiteDatabase database;//获取数据库对象
    private ArrayList<city> cities = new ArrayList<city>();
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    //初始化视图
    private void initView() {
        setContentView(R.layout.activity_city_list);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        mClearEditText = (EditText) findViewById(R.id.filter_edit);
    }

    //初始化数据
    private void initData() {
        mContext = CityListActivity.this;
        new myAsynsTask().execute();
    }

    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<SortModel> filledData(ArrayList<city> cities) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < cities.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(cities.get(i).getCityname());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(cities.get(i).getCityname());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    //初始化监听事件
    private void initListener() {
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Intent intent = new Intent();
                intent.putExtra(RETURN_DATA, ((SortModel) adapter.getItem(position)).getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith
                        (filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    //从数据库获取城市数据
    private ArrayList<city> getAllcity() {
        helper = CitySqliteOpenHelper.getInstanse(mContext);
        database = helper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.query(CitySqliteOpenHelper.CITYTABLE, new String[]{CitySqliteOpenHelper
                .PROVINVE_NAME, CitySqliteOpenHelper.CITY_NAME}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            city c = new city();
            c.setPrivincename(cursor.getString(cursor.getColumnIndex(CitySqliteOpenHelper.PROVINVE_NAME)));
            c.setCityname(cursor.getString(cursor.getColumnIndex(CitySqliteOpenHelper.CITY_NAME)));
            cities.add(c);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        return cities;
    }

    //异步查询数据库
    class myAsynsTask extends AsyncTask<Void, Void, ArrayList<city>> {

        @Override
        protected ArrayList<city> doInBackground(Void... params) {
            return getAllcity();
        }

        @Override
        protected void onPostExecute(ArrayList<city> cities) {
            super.onPostExecute(cities);
            SourceDateList = filledData(cities);
            // 根据a-z进行排序源数据
            Collections.sort(SourceDateList, pinyinComparator);
            adapter = new SortAdapter(mContext, SourceDateList);
            sortListView.setAdapter(adapter);
        }
    }
}
