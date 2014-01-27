package com.dream.anstory.ui.activity;


import com.dream.anstory.R;
import com.dream.anstory.util.AppConstantS;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * ���ܣ�ʹ��ViewPagerʵ�ֳ��ν���Ӧ��ʱ������ҳ
 * 
 * (1)�ж��Ƿ����״μ���Ӧ��--��ȡ��ȡSharedPreferences�ķ���
 * (2)�ǣ����������activity���������MainActivity
 * (3)1s��ִ��(2)����
 * 
 * @author wangwy
 *
 */
public class MainActivity extends Activity {
	Button makeGif;
	Button makeStory;
	Button feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //�Զ�����
        UmengUpdateAgent.update(this);
        //����ģʽ
        makeGif = (Button)findViewById(R.id.main_btn_facemode);
        makeGif.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MobclickAgent.onEvent(MainActivity.this, AppConstantS.UMENG_MAKE_EMOJ);
				Intent in = new Intent(MainActivity.this,PicEditActivity.class);
				in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, MainActivity.this.getClass().getName());
				MainActivity.this.startActivity(in);
				MainActivity.this.finish();
			}
		});
        //����ģʽ
        makeStory = (Button)findViewById(R.id.main_btn_storymode);
        makeStory.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MobclickAgent.onEvent(MainActivity.this, AppConstantS.UMENG_MAKE_STORY);
				Intent in = new Intent(MainActivity.this,StoryEditActivity.class);
				MainActivity.this.startActivity(in);
				MainActivity.this.finish();
			}
		});
        
//        feedback = (Button)findViewById(R.id.main_btn_fb);
//        feedback.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
//			    agent.startFeedbackActivity();
//			}
//		});
    }  
    
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
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

}


