package com.example.account;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.until.MyCountTimer;
import com.until.HttpUtils;

import android.app.Activity;
import android.app.PendingIntent;

import android.content.Intent;

import android.view.View.OnClickListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnFocusChangeListener,OnClickListener{
	private Handler handler;
	
	private EditText userName;
	private EditText pwd1;
	private EditText pwd2;
	private EditText name;
	private EditText phone;
	private EditText yzm;
	
	private Button send;
	
	private Button res;
	
	private String url = "";//服务端的地址  网址+方法名
	
	private Boolean autoFlag = false;//判断
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		userName = (EditText)findViewById(R.id.userName);
		userName.setOnFocusChangeListener(this);
		
		pwd1 = (EditText)findViewById(R.id.password);
		pwd1.setOnFocusChangeListener(this);
		
		pwd2 = (EditText)findViewById(R.id.com_pwd);
		pwd2.setOnFocusChangeListener(this);
		
		yzm = (EditText)findViewById(R.id.vzm);
		yzm.setOnFocusChangeListener(this);
		
		name = (EditText)findViewById(R.id.name);
		
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
						String strFlag = result.getString("flag");
						switch(status){
							case 0:
								if("reg".equals(strFlag)){
									showToast1("注册成功", null);
								}else{
									
								    autoFlag = true;
									showToast1(autoFlag.toString(), null);
								}
								break;
							case -1:
								if("userName".equals(strFlag)){
									showToast1("账号已存在", userName);
								}else if("phone".equals(strFlag)){
									showToast1("手机号已存在", phone);
								}else{
									showToast1("注册失败", null);
								}
								break;
							default:
								showToast1("服务器错误", userName);
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
		
		phone = (EditText)findViewById(R.id.phone);
		phone.setInputType(InputType.TYPE_CLASS_NUMBER); 
		phone.setOnFocusChangeListener(this);
		
		
		send=(Button)findViewById(R.id.send);
		send.setOnClickListener(this);
		
		res = (Button)findViewById(R.id.regsiter);
		res.setOnClickListener(this);
		
		
	}
	//15555215554
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
	        SmsManager sms = SmsManager.getDefault();
	        if(checkph()){
	        	sms.sendTextMessage(phone.getText().toString().trim(), null, "1234", pi, null);
				MyCountTimer timeCount = new MyCountTimer(60000, 1000,send);//传入了文字颜色值
				timeCount.start();
	        }
	        
			break;
		case R.id.regsiter:
			System.out.println(checkEdit());
			if(checkEdit()){
				if(!autoFlag){
					new Thread(){
						@Override
						public void run() {
							JSONObject json = new JSONObject();
							try {
								json.put("UserName", userName.getText().toString().trim());
								json.put("phone", phone.getText().toString().trim());
								json.put("name", name.getText().toString().trim());
								json.put("password", pwd1.getText().toString().trim());
								json.put("flag", "reg");
								try {
									HttpUtils.httpPostMethod(url, json, handler);
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								} catch (ClientProtocolException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}.start();
				}else{
					showToast1("检测用户名和手机号的唯一性", null);
				}
			}
			break;

		default:
			break;
		}
		
	}
	
	public void showToast1(String str, EditText edt){
		//edt.setCursorVisible(true);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		
	}
	
	public void showToast(String message) {
		Looper.prepare();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		Looper.loop();
	}	
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.userName:
			
			if(!hasFocus){
				if(userName.getText().toString().trim().length()<3){
					showToast1("用户名不能小于4个字符",userName);
				}else{
					new Thread(){
						@Override
						public void run() {
							JSONObject json = new JSONObject();
							try {
								json.put("UserName", userName.getText().toString().trim());
								json.put("flag", "UserName");
								try {
									HttpUtils.httpPostMethod(url, json, handler);
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								} catch (ClientProtocolException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}.start();	
				}
				
			}
			
			break;
		case R.id.password:
			if(!hasFocus){
				if(pwd1.getText().toString().trim().length()<6){
					showToast1("密码不能小于6个字符",pwd1);
				}
			}
			break;
		case R.id.com_pwd:
			if(!hasFocus){
				if(!pwd1.getText().toString().trim().equals(pwd2.getText().toString().trim())){
					showToast1("两次密码输入不一致",pwd2);
				}
			}
			break;
		case R.id.phone:
			boolean flag = true;
			if(!hasFocus){
				if(phone.getText().toString().trim().length() < 11){
					showToast1("手机号长度输入错误",phone);
					flag = false;
				}else{
					String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
					if(telRegex.matches(phone.getText().toString().trim())){
						showToast1("手机号格式输入错误",phone);
						flag = false;
					}
				}
				
				if(flag){
					new Thread(){
						@Override
						public void run() {
							JSONObject json = new JSONObject();
							try {
								json.put("phone", phone.getText().toString().trim());
								json.put("flag", "phone");
								try {
									HttpUtils.httpPostMethod(url, json, handler);
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								} catch (ClientProtocolException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}.start();	
				}
			   
			}
			break;
		case R.id.vzm:
			if(!hasFocus){
				if(yzm.getText().toString().trim().equals("")){
					showToast1("验证码输入错误",yzm);
				}
			}
			break;
		default:
			break;
		}
		
	}
	private boolean checkph(){
		if(phone.getText().toString().trim().length() < 11){
			showToast1("手机号长度输入错误",phone);
			return false;
		}else{
			String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
			if(telRegex.matches(phone.getText().toString().trim())){
				showToast1("手机号格式输入错误",phone);
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	private boolean checkEdit(){
		if(userName.getText().toString().trim().equals("")){
			showToast1("用户名不能为空",userName);
			return false;
		}
 
		if(pwd1.getText().toString().trim().equals("")){
			showToast1("密码不能为空",pwd1);
			return false;
		}
		if(!pwd1.getText().toString().trim().equals(pwd2.getText().toString().trim())){
			showToast1("两次密码输入不一致",pwd2);
			return false;
		}
		
		if(phone.getText().toString().trim().equals("")){
			showToast1("手机号不能为空",phone);
			return false;
		}
		if(yzm.getText().toString().trim().equals("")){
			showToast1("验证码不能为空",yzm);
			return false;
		}
		return true;
	}

	

	

	
}
