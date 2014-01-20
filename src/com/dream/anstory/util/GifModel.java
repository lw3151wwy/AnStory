package com.dream.anstory.util;

import java.io.Serializable;

import android.R.integer;
import android.graphics.Bitmap;

public class GifModel {
	/**
	 * 
	 */
	public static final String TAG = "com.dream.anstory.util";
	//头身子
	//Bitmap head;
	//Bitmap[] bitmaps = new Bitmap[AppConstantS.GIF_FRAMECOUNT];
	Bitmap bitmap;
	int headNum;
	int bodyNum;
	//private Bitmap[] bodys;
	//移动缩放数据
	float left;
	float right;
	float top;
	float bottom;
	//背景图片
	Bitmap bgBitmap;
	//底部文字
	String botWord;
	/**
	*/
	/*
	public GifModel(int width,int height){
		for (int i=0;i<AppConstantS.GIF_FRAMECOUNT;i++){
			bitmaps[i]=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		}
	}
	*
	*/
	public void recycleBitmap(){
		bitmap.recycle();
		bitmap = null;
	}
	public void recycleHead(int i){
		//if (i == 8){
		//head.recycle();
		//head = null;
		//}
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		//System.arraycopy(bitmaps, 0, this.bitmaps, 0, 9);
		
		//this.bitmaps = bitmaps;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	/**
	 * @return the headNum
	 */
	public int getHead() {
		return headNum;
	}
	/**
	 * @param headNum the headNum to set
	 */
	public void setHead(int head) {
		this.headNum = head;
	}
	/**
	 * @return the body
	 */
	public int getBody() {
		return bodyNum;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(int body) {
		//System.arraycopy(bodys, 0, this.bodys, 0, 9);
		this.bodyNum = body;
	}
	/**
	 * @return the left
	 */
	public float getLeft() {
		return left;
	}
	/**
	 * @param left the left to set
	 */
	public void setLeft(float left) {
		this.left = left;
	}
	/**
	 * @return the right
	 */
	public float getRight() {
		return right;
	}
	/**
	 * @param right the right to set
	 */
	public void setRight(float right) {
		this.right = right;
	}
	/**
	 * @return the top
	 */
	public float getTop() {
		return top;
	}
	/**
	 * @param top the top to set
	 */
	public void setTop(float top) {
		this.top = top;
	}
	/**
	 * @return the bottom
	 */
	public float getBottom() {
		return bottom;
	}
	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	/**
	 * @return the bgBitmap
	 */
	public Bitmap getBgBitmap() {
		return bgBitmap;
	}
	/**
	 * @param bgBitmap the bgBitmap to set
	 */
	public void setBgBitmap(Bitmap bgBitmap) {
		this.bgBitmap = bgBitmap;
	}
	/**
	 * @return the botWord
	 */
	public String getBotWord() {
		return botWord;
	}
	/**
	 * @param botWord the botWord to set
	 */
	public void setBotWord(String botWord) {
		this.botWord = botWord;
	}
	
}
