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
				// 设置播放的延迟时间
				if(flag) {
					e.setDelay(100);
					e.addFrame(pic[i]); 
					//添加到帧中
					Message msg = new Message();  
			        msg.what = PicEditActivity.MATCH_A_FRAME;  
			        handler.sendMessage(msg); 
				} 
				
			}
			e.finish();// 刷新任何未决的数据，并关闭输出文件
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
