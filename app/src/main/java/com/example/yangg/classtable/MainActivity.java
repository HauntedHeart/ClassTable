package com.example.yangg.classtable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangg.classtable.UI.AbsGridAdapter;
import com.example.yangg.util.NetUtil;
//import com.example.yangg.classtable.UI.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinner,spinner1;
    private ArrayAdapter<String> spinnerAdapter,spinnerAdapter1;
    private List<String> dataList,dataList1;

    private GridView detailCource;
    private String[][] contents;
    private AbsGridAdapter MyAdapter;
    private String[][] Allclass;
    private ImageView mAddCourse;


    private TextView tvid;
    JSONObject object;

    private String cur_name, cur_pwd;
    private SharedPreferences.Editor editor;
    private SharedPreferences SP ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//取消状态栏
        setContentView(R.layout.main);

        Allclass = new String[7][3];            //初始化课程表内容
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                Allclass[i][j] = "";
            }
        }
        fillStringArray(Allclass);              //调用函数将Allclass的内容传给contents
        detailCource = (GridView) findViewById(R.id.courceDetail);
        MyAdapter = new AbsGridAdapter(this);
        MyAdapter.setContent(contents, 3, 7,cur_name);
        detailCource.setAdapter(MyAdapter);
        //////////////创建Spinner数据
        spinner = (Spinner) findViewById(R.id.switchWeek);
        spinner1 = (Spinner) findViewById(R.id.switchTerm);
        fillDataList();
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_checked_text, dataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new spinnerSelectedListenner());      //监听事件

        spinnerAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_checked_text, dataList1);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerAdapter1);
        spinner1.setOnItemSelectedListener(new spinnerSelectedListenner());  //监听事件

        mAddCourse = (ImageView) findViewById(R.id.addcourse);
        mAddCourse.setOnClickListener(this);


        tvid = (TextView) this.findViewById(R.id.login);
        tvid.setOnClickListener(this);


        editor= getSharedPreferences("data",MODE_PRIVATE).edit();
        SP = getSharedPreferences("data",MODE_PRIVATE);
        cur_name = SP.getString("user_name","登录");
        cur_pwd = SP.getString("user_pwd","");
        int k=SP.getInt("term_position",0);
        int j=SP.getInt("week_position",0);
        spinner.setSelection(j,true);
        spinner1.setSelection(k,true);

        tvid.setText(cur_name);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myClassTable", "网络OK");
                    httptest(cur_name, cur_pwd);
                } else {
                    Log.d("myClassTable", "网络挂了");
                    Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }
        }
             private Handler handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                        case 0:
                            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            try {
                                Allclass = new String[7][3];
                                for (int i = 0; i < 7; i++) {
                                    for (int j = 0; j < 3; j++) {
                                        int i2 = i + 1, j2 = j + 1;
                                        Allclass[i][j] = object.getString("class" + i2 + j2);}
                                }
                                tvid.setText(object.getString("user_name"));    //用户名展示
                                fillStringArray(Allclass);
                                MyAdapter.setContent(contents, 3, 7,cur_name);
                                detailCource.setAdapter(MyAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            break;
                        default:
                            break;}}
            };

            private void httptest(String user_name, String user_pwd) {
                //step 1: 创建一个OkHttpClick对象
                final OkHttpClient okHttpClient = new OkHttpClient();
                //step 2: 创建  FormBody.Builder
                FormBody formBody = new FormBody.Builder()
                        .add("user_name",user_name)
                        .add("user_pwd",user_pwd)
                        .build();
                //step 3: 创建请求
                final Request request = new Request.Builder()
                        .url("http://192.144.177.15/index/index/hello.php" )
                        .post(formBody)
                        .build();
                //step 4： 建立联系 创建Call对象
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                // TODO:  请求失败
                                handler.sendEmptyMessage(0);
                                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_LONG).show();
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
                                    e.printStackTrace();}}});}}).start();
            }


            public void onClick(View view) {

                if (view.getId() == R.id.login) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(i, 1);
                }

                if (view.getId() == R.id.addcourse) {
                    Intent i = new Intent(MainActivity.this, AddCourse.class);
                    i.putExtra("user_name", cur_name);
                    startActivityForResult(i, 2);
                }
            }

    private class spinnerSelectedListenner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            switch (parent.getId()) {
                case R.id.switchTerm:
                    int k= parent.getSelectedItemPosition();//取得被选中的列表项的ID
                    editor.putInt("term_position",k);     //将ID保存入SharedPreferences
                    editor.apply();
                    break;
                case R.id.switchWeek:
                    int j= parent.getSelectedItemPosition();//取得被选中的列表项的ID
                    editor.putInt("week_position",j);  //将ID保存入SharedPreferences
                    editor.apply();
                    break;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    }

            public void fillStringArray(String Allclass[][])
            {
                contents = new String[7][3];
                for (int i = 0; i < 7; i++)
                {
                    for (int j = 0; j < 3; j++)
                    {
                        contents[i][j] = Allclass[i][j];
                    }
                }
            }

            public void fillDataList() {
                dataList = new ArrayList<>();
                for (int i = 1; i < 21; i++) {
                    dataList.add("第" + i + "周");
                }
                dataList1 = new ArrayList<>();
                dataList1.add("第一学期");
                dataList1.add("第二学期");
                dataList1.add("第三学期");
                dataList1.add("第四学期");
            }

            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == 1 && resultCode == RESULT_OK) {
                    cur_name = data.getStringExtra("user_name");
                    cur_pwd = data.getStringExtra("user_pwd");
                    httptest(cur_name, cur_pwd);
                    MyAdapter = new AbsGridAdapter(this);
                    MyAdapter.setContent(contents, 3, 7,cur_name);
                    detailCource.setAdapter(MyAdapter);
                    editor.putString("user_name",cur_name);
                    editor.putString("user_pwd",cur_pwd);
                    editor.apply();
                }

                if (requestCode == 2 && resultCode == RESULT_OK) {
                    httptest(cur_name, cur_pwd);
                    MyAdapter = new AbsGridAdapter(this);
                    MyAdapter.setContent(contents, 3, 7,cur_name);
                    detailCource.setAdapter(MyAdapter);
                }
            }
    }
