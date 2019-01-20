package com.example.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import com.until.HttpUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
public class MainActivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	private Handler handler;
	
	private Button log;
	private Button res;
	
	CheckBox savePwd;
	CheckBox auto;
	SharedPreferences sp;
	
	private EditText name;
	private EditText pwd;
	
	private boolean flag;//标识是否记住密码
	
	private String url = "";//服务端的地址  网址+方法名
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					try {
						String res = msg.getData().getString("res");
						JSONObject result = new JSONObject(res);
						int status = Integer.parseInt(result.getString("status"));
						switch(status){
							case 0:
								flag = savePwd.isChecked();
								Editor editor = sp.edit(); 
								if(flag){
									//记住用户名、密码、  
				                    editor.putString("USER_NAME", result.getString("userName"));  
				                    editor.putString("PASSWORD",result.getString("password")); 
								}
								editor.putBoolean("ISCHECK", flag);
								editor.putBoolean("ISAUTO",auto.isChecked());
								editor.commit();
								Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
								break;
							case -1:
								Toast.makeText(MainActivity.this, "输入的账号错误！", Toast.LENGTH_SHORT).show();
								break;
							case -2:
								Toast.makeText(MainActivity.this, "输入的密码错误！", Toast.LENGTH_SHORT).show();
								break;
							default:
								Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
								break;
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		};

		name = (EditText)findViewById(R.id.userName);
		pwd = (EditText)findViewById(R.id.pssword);
		
		savePwd = (CheckBox)findViewById(R.id.savePwd);//记住密码
		auto = (CheckBox)findViewById(R.id.auto);//自动登录
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);//它是采用xml文件存放数据的
		
		 //判断自动登陆多选框状态  
		if(sp.getBoolean("ISAUTO", false)){
			//设置默认是自动登录状态  
            auto.setChecked(true); 
            //Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
		}
		//判断记住密码多选框的状态   
		if(sp.getBoolean("ISCHECK", false))  {  
		    //设置默认是记录密码状态  
			savePwd.setChecked(true);  
		    name.setText(sp.getString("USER_NAME", ""));  
		    pwd.setText(sp.getString("PASSWORD", ""));  
		   
		}
		auto.setOnCheckedChangeListener(this);//自动登录
		savePwd.setOnCheckedChangeListener(this);//记住密码
		log = (Button)findViewById(R.id.login);
		res = (Button)findViewById(R.id.regsiter);
		
		log.setOnClickListener(this);
		res.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login:
				new Thread(){
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject();
							json.put("UserName", name.getText().toString().trim());
							json.put("PassWord", pwd.getText().toString().trim());
							if(chkLogin(name.getText().toString().trim(), pwd.getText().toString().trim())){
								HttpUtils.httpPostMethod(url, json, handler);
							}else{
								showToast("用户名或者密码不能为空！");
							}
							
						} catch (JSONException e) {
							showToast("解析JSON出错");
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							showToast("服务器连接不上");
							e.printStackTrace();
						} catch (IOException e) {
							showToast("服务器连接不上1");
							e.printStackTrace();
						}
					}
				}.start();
				break;
			case R.id.regsiter:
				Intent in = new Intent(this, RegisterActivity.class);
				startActivity(in);
				finish();
				break;
			default:
				break;
		}
		
	}
	
	public void showToast(String message) {
		Looper.prepare();
		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
		Looper.loop();
	}	

	private boolean chkLogin(String userName, String password) {
		if("".equals(userName)){
			return false;
		}
		if("".equals(password)){
			return false;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.auto:
			if(auto.isChecked()){
				savePwd.setChecked(true);
			}
			
			break;
		case R.id.savePwd:
			
			if(!savePwd.isChecked()){
				auto.setChecked(false);
			}
			
			break;
		default:
			break;
		}
		
	}
}
