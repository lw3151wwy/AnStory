package com.dream.anstory.util;

import java.security.PublicKey;

import android.R.integer;

public interface AppConstantS {
	public static final String FROM_ACTIVITY_NAME = "ACTIVITYNAME";
	public static final String STORY_MODE = "STORY_MODE";
	public static final String STORY_GIF_NUM = "STORYGIFNUM";
	
	//头部和身子的数量
	public static int headNumber = 20;
	public static int bodyNumber = 29;
	
	
	public static final int headOriHeightPx = 264;
	public static final int bodyOriHeightPx = 351;
	public static final int makePicOriHeightPx = 450;
	public static final int headAndBodyOverLap = 165;
	
	//最终生成的GIF图的长宽高 
	public static final int GIF_FRAMECOUNT = 9;
	public static final int FINAL_GIF_HEIGHT = 350;
	public static final int FINAL_GIF_WIDTH = 320;
	//gif存放目录名 asset/gif
	public static final String GIF_FOLDERNAME="gif";
	//读取ASSET的文件名前缀和后缀  如head3.gif ,body5.png等
	public static final String BODYNAME = "body";
	public static final String HEADNAME = "head";
	public static final String FRAMENAME = "frame";
	public static final String HEADCHSBTN_FILENAME = "headchsbtn";
	public static final String BODYCHSBTN_FILENAME = "bodychsbtn";
	public static final String FRAMECHSBTN_FILENAME = "framechsbtn";
	public static final String GIF_ENDNAME=".gif";
	public static final String JPG_ENDNAME=".jpg";
	public static final String PNG_ENDNAME=".png";
	//临时存储的文件名
	public static final String GIF_STORENAME = "result.gif";
	public static final String JPG_STORENAME = "result.jpg";
	public static final String PNG_STORENAME = "result.png";
	//GIF图片默认背景的目录和文件名
	public static final String GIFBG_FOLDERNAME = "background";
	public static final String GIFBG_FILENAME = "bg_320_350.png";
	//相机根目录,相机返回参数成功，相机未照相直接返回
	public static final String CAMPIC_ROOT = "mnt/sdcard/DCIM/Camera/";
	public static final int CAM_RETURN_OK = -1;
	public static final int CAM_RETURN_EMPTY = 0;
	//新浪微博：权限参数APP KEY
	public static final String APP_KEY="3415170594";
	
	//新浪微博：REDIRECT_URL（分享下面的来自……的LINK地址）
	//public static final String REDIRECT_URL = "http://www.grocamp.com/showgif.html";
	public static final String REDIRECT_URL = "http://115.28.4.190";  
	//新浪微博：权限参数
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
			"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
				"follow_app_official_microblog";

	public static String APP_STORE_FOLDERNAME = "SHOWGIF";
	
	public static final String CLIENT_ID = "client_id";
	
	public static final String RESPONSE_TYPE = "response_type";
	
	public static final String USER_REDIRECT_URL = "redirect_uri";
	
	public static final String DISPLAY = "display";
	
	public static final String USER_SCOPE = "scope";
	
	public static final String PACKAGE_NAME = "packagename";
	
	public static final String KEY_HASH = "key_hash";
}
