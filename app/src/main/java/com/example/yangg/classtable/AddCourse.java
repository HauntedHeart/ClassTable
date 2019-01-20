package com.example.yangg.classtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yangg.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yangg on 2018/12/16.
 */

public class AddCourse extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;

    private LinearLayout mSaveCourseBtn;

    private List<String> dataList1,dataList2;
    private ArrayAdapter<String> spinnerAdapter1,spinnerAdapter2;
    private Spinner spinner1,spinner2;

    private EditText mAddcourse_name,mAddcourse_room;

    JSONObject object, object2;
    private String username;
    private String cur_name;

    private String getDay;
    private String getTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcourse);

        Intent re_name = getIntent();
        cur_name= re_name.getStringExtra("user_name");

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mSaveCourseBtn= (LinearLayout) findViewById(R.id.addcourse_savebutton);
        mSaveCourseBtn.setOnClickListener(this);

        spinner1 = (Spinner) findViewById(R.id.switchDay);
        spinner2 = (Spinner) findViewById(R.id.switchTime);
        fillDataList();
        spinnerAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_checked_text1, dataList1);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_checked_text1, dataList2);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerAdapter1);
        spinner2.setAdapter(spinnerAdapter2);

         mAddcourse_name = (EditText) findViewById(R.id.addcourse_name);
         mAddcourse_room = (EditText) findViewById(R.id.addcourse_room);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

            case R.id.addcourse_savebutton:
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myClassTable", "网络OK");
                    if(mAddcourse_name.getText().toString().trim().equals("")) {
                        Toast.makeText(AddCourse.this, "课程名不能为空", Toast.LENGTH_LONG).show();
                        break;
                    }
                    switch ((String)spinner1.getSelectedItem()) {
                        case "星期一": getDay="1";break;
                        case "星期二": getDay="2";break;
                        case "星期三": getDay="3";break;
                        case "星期四": getDay="4";break;
                        case "星期五": getDay="5";break;
                        case "星期六": getDay="6";break;
                        case "星期日": getDay="7";break;
                    }
                    switch ((String)spinner2.getSelectedItem()) {
                        case "上午": getTime="1";break;
                        case "下午": getTime="2";break;
                        case "晚上": getTime="3";break;
                    }

                    httptest(cur_name,mAddcourse_room.getText().toString(),mAddcourse_name.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (cur_name.equals(username)) {
                                Toast.makeText(AddCourse.this, "加课成功，已更新课程表！！", Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();

                            } else {
                                Toast.makeText(AddCourse.this, "加课失败，请稍后再试！！" , Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 1400);
                    break;
                } else {
                    Log.d("myClassTable", "网络挂了");
                    Toast.makeText(AddCourse.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(AddCourse.this, "失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    // Toast.makeText(AddCourse.this, "成功", Toast.LENGTH_LONG).show();
                    try {
                        username=object.getString("user_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    // Toast.makeText(AddCourse.this, "错误jjjj", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };


    private void httptest(String user_name ,String class_room,String class_name) {

        //step 1: 同样的需要创建一个OkHttpClick对象
        final OkHttpClient okHttpClient = new OkHttpClient();

        //step 2: 创建  FormBody.Builder
        FormBody formBody = new FormBody.Builder()
                .add("user_name", user_name)
                .add("class_num",getDay+getTime)
                .add("class_name",class_name+class_room)
                .build();

        //step 3: 创建请求
        final Request request = new Request.Builder().url("http://192.144.177.15/index/test/addcourse.php")
                .post(formBody)
                .build();

        //step 4： 建立联系 创建Call对象

        new Thread(new Runnable(){
            @Override
            public void run(){
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // TODO:  请求失败
                        handler.sendEmptyMessage(0);
                        // Thread.sleep(2000);
                        Toast.makeText(AddCourse.this, "请求失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // TODO:  请求成功
                        String a = response.body().string();
                        Log.d("httpok", a);
                        try {
                            JSONArray jsonArray = new JSONArray(a);
                            object = jsonArray.getJSONObject(0);
                            handler.sendEmptyMessage(1);
                        } catch (Exception e) {
                            handler.sendEmptyMessage(2);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void fillDataList() {
        dataList1 = new ArrayList<>();
        dataList1.add("星期一");
        dataList1.add("星期二");
        dataList1.add("星期三");
        dataList1.add("星期四");
        dataList1.add("星期五");
        dataList1.add("星期六");
        dataList1.add("星期日");

        dataList2 = new ArrayList<>();
        dataList2.add("上午");
        dataList2.add("下午");
        dataList2.add("晚上");
    }
}