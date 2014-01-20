package com.dream.anstory.listener;

import com.dream.anstory.R;
import com.dream.anstory.ui.activity.PicEditActivity;
import com.dream.anstory.util.Util;
import com.showgif.gifview.GifView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
	public static final String TAG = "com.showgif.listener.MyGestureListener";
	public static final int TYPE_CHANGEHEADBODY = 1;
	public static float finalLeft = 0;
	public static float finalTop = 0;
	public static float finalRight = 0;
	public static float finalBottom = 0;
	private View curView;
	private ImageView gifHead;
	private GifView gifBody;
	private Handler handler;
	private float basevalue = 0;
	private Context context;
	public MyGestureListener(Handler paramHandler, View paramView, ImageView headView, GifView bodyView, Context context) {
		this.handler = paramHandler;
		this.curView = paramView;
		this.gifHead = headView;
		this.gifBody = bodyView;
		this.context = context;
	}

	public boolean onDoubleTap(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onDown(MotionEvent paramMotionEvent) {
		basevalue = 0;
		return true;
	}

	public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		Log.v("com.showgif.listener.MyGestureListener", "进入手势运行类ONFLING的回调代码");
		Log.v("com.showgif.listener.MyGestureListener", "当前调用页面为换头和身子的切换，开始执行onfling内容");
		Message msg = new Message();
		//没有重写新的页面，就把原来的滑动身体的方法暂时给注销掉了
		/*if (this.curView != null) {
			float line = this.curView.getHeight() * Util.getHeadAndBodyScaleAfterOverLap();
			System.out.println("cutOff" + line);
			System.out.println("curH" + this.curView.getHeight());
			System.out.println("scale" + Util.getHeadAndBodyScaleAfterOverLap());
	
		
			if (paramMotionEvent1.getY() < line) {
				Log.v("com.showgif.listener.MyGestureListener", "用户在头部滑动，发送头部切换的handlermsg");
				if (paramMotionEvent1.getX() - paramMotionEvent2.getX() > 100) {
					Log.v("com.showgif.listener.MyGestureListener","手势向左FLING");
					msg.what = PicEditActivity.HEAD_CHANGENEXT;
					this.handler.sendMessage(msg);
				} else if (paramMotionEvent2.getX() - paramMotionEvent1.getX() > 100){
					Log.v("com.showgif.listener.MyGestureListener","手势向右FLING");
					msg.what = PicEditActivity.HEAD_CHANGEPRE;
					this.handler.sendMessage(msg);
				}
			} else {
				Log.v("com.showgif.listener.MyGestureListener", "用户在身子滑动，发送身子切换的handlermsg");
				if (paramMotionEvent1.getX() - paramMotionEvent2.getX() > 100.0) {
					Log.v("com.showgif.listener.MyGestureListener","手势向左FLING");
					msg.what = PicEditActivity.BODY_CHANGENEXT;
					this.handler.sendMessage(msg);
				} else if (paramMotionEvent2.getX() - paramMotionEvent1.getX() > 100.0) {
					Log.v("com.showgif.listener.MyGestureListener","手势向右FLING");
					msg.what = PicEditActivity.BODY_CHANGEPRE;
					handler.sendMessage(msg);
				}
			}		
			return true;
		} else {
			return false;
		}*/
		return true;
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
		super.onLongPress(paramMotionEvent);
	}

	//实现缩放与移动
	public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		int width = curView.getWidth();
		int height = curView.getHeight();
		int nowLeft = gifBody.getLeft()-curView.getLeft();
		int nowTop = gifBody.getTop()-curView.getTop();
		int nowRight = gifBody.getRight()-curView.getLeft();
		int nowBottom = gifBody.getBottom()-curView.getTop();
		//RelativeLayout.LayoutParams nowLayoutParams = new LayoutParams(gifHead.getLayoutParams());
		RelativeLayout.LayoutParams setLayoutParams = new LayoutParams(width, height);
		int pointerCount = paramMotionEvent2.getPointerCount();
		if (pointerCount == 1 && Math.abs(paramFloat1/width) < 0.2 &&  Math.abs(paramFloat2/height) < 0.2){
			//gifHead.setPadding(nowLeft-(int) paramFloat1,nowTop-(int) paramFloat2,nowRight+(int) paramFloat1,nowBottom+(int) paramFloat2);
			setLayoutParams.leftMargin = nowLeft-(int) paramFloat1;
			setLayoutParams.topMargin = nowTop-(int) paramFloat2;
			setLayoutParams.rightMargin = width-nowRight+(int) paramFloat1;
			setLayoutParams.bottomMargin = height-nowBottom+(int) paramFloat2;
			setLayoutParams.width = gifHead.getWidth();
            setLayoutParams.height = gifHead.getHeight();
			gifHead.setLayoutParams(setLayoutParams);
			gifBody.setLayoutParams(setLayoutParams);
			//finalLeft与finalTop为静态变量传递参数以在合成时使用
			finalLeft = (float) setLayoutParams.leftMargin / (float) curView.getWidth();
			finalTop = (float) setLayoutParams.topMargin / (float) curView.getHeight();
			finalRight = ((float) curView.getWidth()-(float) setLayoutParams.rightMargin) / (float) curView.getWidth();
			finalBottom = ((float) curView.getHeight()-(float) setLayoutParams.bottomMargin) / (float) curView.getHeight();
			//gifHead.setLeft(nowLeft-(int) paramFloat1);
			//gifHead.setTop(nowTop-(int) paramFloat2);
			//gifHead.setRight(nowRight-(int) paramFloat1);
			//gifHead.setBottom(nowBottom-(int) paramFloat2);
			//gifBody.setPadding(nowLeft-(int) paramFloat1,nowTop-(int) paramFloat2,nowRight+(int) paramFloat1,nowBottom+(int) paramFloat2);
			//gifBody.setLeft(nowLeft-(int) paramFloat1);
			//gifBody.setTop(nowTop-(int) paramFloat2);
			//gifBody.setRight(nowRight-(int) paramFloat1);
			//gifBody.setBottom(nowBottom-(int) paramFloat2);
		}else if(pointerCount == 2){
			float x = paramMotionEvent2.getX(0) - paramMotionEvent2.getX(1);
            float y = paramMotionEvent2.getY(0) - paramMotionEvent2.getY(1);
            float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
            if (basevalue == 0) {
            	basevalue = value;
            }
            else {  
                if (value - basevalue >= 10 || value - basevalue <= -10) {  
                    float scale = value / basevalue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                    basevalue = value;
                    float w = gifHead.getWidth()/2;
                    float h = gifHead.getHeight()/2;
                    setLayoutParams.leftMargin = nowLeft-(int) ((scale-1)*w);
                    setLayoutParams.topMargin = nowTop-(int) ((scale-1)*h);
                    setLayoutParams.rightMargin = width-nowRight-(int) ((scale-1)*w);
                    setLayoutParams.bottomMargin = height-nowBottom-(int) ((scale-1)*h);
                    setLayoutParams.width = (int) (2*w*scale);
                    setLayoutParams.height =  (int) (2*h*scale);
                    gifHead.setLayoutParams(setLayoutParams);
        			gifBody.setLayoutParams(setLayoutParams);
        			//gifHead.setScaleX(scale);
        			//gifHead.setScaleY(scale);
        			//gifBody.setScaleX(scale);
        			//gifBody.setScaleY(scale);
        			//scaleTotal为静态变量传递参数以在合成时使用
        			//scaleTotal = scaleTotal*scale;
        			//finalLeft与finalTop为静态变量传递参数以在合成时使用
        			finalLeft = (float) setLayoutParams.leftMargin / (float) curView.getWidth();
        			finalTop = (float) setLayoutParams.topMargin / (float) curView.getHeight();
        			finalRight = ((float) curView.getWidth()-(float) setLayoutParams.rightMargin) / (float) curView.getWidth();
        			finalBottom = ((float) curView.getHeight()-(float) setLayoutParams.bottomMargin) / (float) curView.getHeight();
                }
            }
		}
		Log.v("com.showgif.listener.MyGestureListener", "NEW End"+curView.getHeight());
		Log.v("com.showgif.listener.MyGestureListener", "NEW End"+gifHead.getHeight());
		//Log.v("com.showgif.listener.MyGestureListener", "Now Right"+" "+(width-nowRight)+" "+scale+" "+setLayoutParams.rightMargin+" "+gifHead.getWidth()*(scale-1));
		SharedPreferences preferences;
		SharedPreferences.Editor editors;
		preferences = context.getSharedPreferences("help_showtimes", Context.MODE_PRIVATE);
		editors = preferences.edit();
		int times = 1;		
		editors.putInt("times", times);
		editors.commit();
		return true;
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
		super.onShowPress(paramMotionEvent);
	}

	public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		return true;
	}
}
