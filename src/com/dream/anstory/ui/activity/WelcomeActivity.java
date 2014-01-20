package com.dream.anstory.ui.activity;

import com.dream.anstory.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

/**
 * 功能：使用ViewPager实现初次进入应用时的引导页
 * 
 * (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入引导activity；否，则进入MainActivity
 * (3)1s后执行(2)操作
 * 
 * @author wangwy
 *
 */
public class WelcomeActivity extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
 
        
        boolean mFirst = isFirstEnter(WelcomeActivity.this,WelcomeActivity.this.getClass().getName());
        if(mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,2000);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,2000);
    }   
     
    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************

    private boolean isFirstEnter(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className)) {
        	 return false;
		}
		String mResultStr = context.getSharedPreferences(GuideActivity.SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
				.getString(GuideActivity.KEY_GUIDE_ACTIVITY, "");// 取得所有类名 如 com.my.MainActivity
		if (mResultStr.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}
     
    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
            case SWITCH_MAINACTIVITY:
                Intent mIntent = new Intent();
                mIntent.setClass(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(mIntent);
                WelcomeActivity.this.finish();
//                mIntent.setClass(WelcomeActivity.this, PicEditActivity.class);
//                WelcomeActivity.this.startActivity(mIntent);
//                WelcomeActivity.this.finish();
                break;
            case SWITCH_GUIDACTIVITY:
                mIntent = new Intent();
                mIntent.setClass(WelcomeActivity.this, GuideActivity.class);
                WelcomeActivity.this.startActivity(mIntent);
                WelcomeActivity.this.finish();
                break;
            }
            super.handleMessage(msg);
        }
    };
    
	@Override
	protected void onResume() {
		super.onResume();
	    MobclickAgent.onResume(this);
	}

	@Override
    protected void onPause() {  
        super.onPause();  
        MobclickAgent.onPause(this);
    }  
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				this.finish();
				System.exit(0);
			}
		return super.onKeyDown(keyCode, event);
	}
}


