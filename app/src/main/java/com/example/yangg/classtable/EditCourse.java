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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by yangg on 2018/12/16.
 */

public class EditCourse extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private LinearLayout mSaveCourseBtn;
    private LinearLayout mDeleteCourseBtn;


    private EditText mEditcourse_name,mEditcourse_room;

    JSONObject object;
    private String user_name;
    private String username;

    private String Cur_Day;
    private String Cur_Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcourse);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mSaveCourseBtn= (LinearLayout) findViewById(R.id.editcourse_savebutton);
        mSaveCourseBtn.setOnClickListener(this);

        mDeleteCourseBtn= (LinearLayout) findViewById(R.id.editcourse_deletebutton);
        mDeleteCourseBtn.setOnClickListener(this);
        mEditcourse_name = (EditText) findViewById(R.id.editcourse_name);
        mEditcourse_room = (EditText) findViewById(R.id.editcourse_room);

        Intent re_class = getIntent();
        Cur_Day = re_class.getStringExtra("class_column");
        Cur_Time = re_class.getStringExtra("class_row");
        user_name= re_class.getStringExtra("user_name");

        Log.d("myWeather", Cur_Day+"     "+Cur_Time+"      "+user_name);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

            case R.id.editcourse_savebutton:
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myClassTable", "网络OK");
                    if(mEditcourse_name.getText().toString().trim().equals("")) {
                        Toast.makeText(EditCourse.this, "课程名不能为空", Toast.LENGTH_LONG).show();
                        break;
                    }
                    httptest(user_name,mEditcourse_room.getText().toString(),mEditcourse_name.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {                                  //延时
                            if (user_name.equals(username)) {
                                Toast.makeText(EditCourse.this, "修改成功，已更新课程表！！", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(EditCourse.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(EditCourse.this, "修改失败，请稍后再试！！" , Toast.LENGTH_LONG).show();}
                        }
                    }, 1400);
                    break;
                } else {
                    Log.d("myClassTable", "网络挂了");
                    Toast.makeText(EditCourse.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }

            case R.id.editcourse_deletebutton:
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    httptest(user_name,"","");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (user_name.equals(username)) {
                                Toast.makeText(EditCourse.this, "删除成功，已更新课程表！！", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditCourse.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(EditCourse.this, "删除失败，请稍后再试！！" , Toast.LENGTH_LONG).show();}
                        }
                    }, 1400);
                    break;
                } else {
                    Toast.makeText(EditCourse.this, "网络挂了！", Toast.LENGTH_LONG).show();}
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(EditCourse.this, "失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    try {
                        username=object.getString("user_name");
                        Log.d("myWeather1", user_name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
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
                .add("class_num",Cur_Day+Cur_Time)
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
                        Toast.makeText(EditCourse.this, "请求失败", Toast.LENGTH_LONG).show();
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

}