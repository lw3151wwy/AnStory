package com.showgif.jpg2gif;

import com.dream.anstory.ui.activity.PicEditActivity;
import com.weibo.sdk.android.api.WeiboAPI.SRC_FILTER;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class JpgToGif {
	private boolean flag = true;
	private int curFrameNum = 0;
	Handler handler;
	
	public void stopJpgToGif() {
		this.flag = false;
	}
	
	public void startJpgToGif() {
		curFrameNum = 0;
		this.flag = true;
	}
	
	public boolean getFlag() {
		return flag;
	}
	
	public int getCurFrameNum() {
		return curFrameNum;
	}
	
	public JpgToGif() {}
	
	public JpgToGif(Handler handler) {
		this.handler = handler;
	}

	public void jpgToGif(Bitmap pic[], String newPic) {
		try {
			AnimatedGifEncoder1 e = new AnimatedGifEncoder1();
			e.setRepeat(0);
			e.start(newPic);
			for (int i = 0; i < pic.length; i++) {
				curFrameNum = i;
				// ���ò��ŵ��ӳ�ʱ��
				if(flag) {
					e.setDelay(100);
					e.addFrame(pic[i]); 
					//��ӵ�֡��
					Message msg = new Message();  
			        msg.what = PicEditActivity.MATCH_A_FRAME;  
			        handler.sendMessage(msg); 
				} 
				
			}
			e.finish();// ˢ���κ�δ�������ݣ����ر�����ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
