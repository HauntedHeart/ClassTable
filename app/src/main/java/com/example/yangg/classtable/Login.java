package com.example.yangg.classtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yangg.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.yangg.classtable.R.id.user_key;
import static com.example.yangg.classtable.R.id.user_name;

/**
 * Created by yangg on 2018/12/16.
 */

public class Login extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private Button mRegisterBtn;
    private LinearLayout mLoginBtn;
    private CheckBox checkBox1;
    private EditText mEdit_userkey, mEdit_username;
    JSONObject object;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mLoginBtn=(LinearLayout) findViewById(R.id.login_button);
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn= (Button) findViewById(R.id.button_Register);
        mRegisterBtn.setOnClickListener(this);
        mEdit_userkey = (EditText) findViewById(user_key);
        mEdit_userkey.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mEdit_username = (EditText) findViewById(user_name);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    // /如果选中，显示密码
                    mEdit_userkey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    mEdit_userkey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

            case R.id.login_button:
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myClassTable", "网络OK");
                    if(checkEdit()) {
                        httptest(mEdit_username.getText().toString(), mEdit_userkey.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mEdit_username.getText().toString().equals(username)) {
                                Toast.makeText(Login.this, "登录成功，已更新课程表！！", Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                i.putExtra("user_name", mEdit_username.getText().toString());
                                i.putExtra("user_pwd", mEdit_userkey.getText().toString());
                                setResult(RESULT_OK, i);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "用户名或密码错误，请重新输入！！", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 1400);
                    }
                        break;
                } else {
                    Log.d("myClassTable", "网络挂了");
                    Toast.makeText(Login.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }

            case R.id.button_Register:
                Intent i = new Intent(Login.this, Register.class);
                startActivityForResult(i, 1);
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(Login.this, "失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    try {
                        username=object.getString("user_name");
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


    private void httptest(String user_name ,String user_pwd) {
        //step 1: 同样的需要创建一个OkHttpClick对象
        final OkHttpClient okHttpClient = new OkHttpClient();

        //step 2: 创建  FormBody.Builder
        FormBody formBody = new FormBody.Builder()
                .add("user_name", user_name)
                .add("user_pwd",user_pwd)
                .build();

        //step 3: 创建请求
        final Request request = new Request.Builder().url("http://192.144.177.15/index/index/hello.php")
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
                        Toast.makeText(Login.this, "请求失败", Toast.LENGTH_LONG).show();
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

    private boolean checkEdit(){
        if(mEdit_username.getText().toString().trim().equals("")){
            Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_LONG).show();
            return false;
        }
        if(mEdit_userkey.getText().toString().trim().equals("")){
            Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}