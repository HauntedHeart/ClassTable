package com.until;


import android.os.CountDownTimer;
import android.widget.Button;


public class MyCountTimer extends CountDownTimer {

	public static final int TIME_COUNT = 121000;//时间防止从119s开始显示（以倒计时120s为例子）
	private Button btn;
	//private int endStrRid;
	private int normalColor, timingColor;//未计时的文字颜色，计时期间的文字颜色
	 
	
	public MyCountTimer(long millisInFuture, long countDownInterval, Button send) {
		super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		this.btn = send;
	}
	
	 
	// 计时完毕时触发
	@Override
	public void onFinish() {
		if(normalColor > 0){
			btn.setTextColor(normalColor);
		}
		btn.setText("重新发送");
		btn.setEnabled(true);
	}
	 
	// 计时过程显示
	@Override
	public void onTick(long millisUntilFinished) {
		if(timingColor > 0){
			btn.setTextColor(timingColor);
		}
		btn.setEnabled(false);
		btn.setText(millisUntilFinished / 1000 + "s");
	}

}
