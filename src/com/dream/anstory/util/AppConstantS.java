package com.dream.anstory.util;

import java.security.PublicKey;

import android.R.integer;

public interface AppConstantS {
	public static final String FROM_ACTIVITY_NAME = "ACTIVITYNAME";
	public static final String STORY_MODE = "STORY_MODE";
	public static final String STORY_GIF_NUM = "STORYGIFNUM";
	
	//ͷ�������ӵ�����
	public static int headNumber = 20;
	public static int bodyNumber = 29;
	
	
	public static final int headOriHeightPx = 264;
	public static final int bodyOriHeightPx = 351;
	public static final int makePicOriHeightPx = 450;
	public static final int headAndBodyOverLap = 165;
	
	//�������ɵ�GIFͼ�ĳ���� 
	public static final int GIF_FRAMECOUNT = 9;
	public static final int FINAL_GIF_HEIGHT = 350;
	public static final int FINAL_GIF_WIDTH = 320;
	//gif���Ŀ¼�� asset/gif
	public static final String GIF_FOLDERNAME="gif";
	//��ȡASSET���ļ���ǰ׺�ͺ�׺  ��head3.gif ,body5.png��
	public static final String BODYNAME = "body";
	public static final String HEADNAME = "head";
	public static final String FRAMENAME = "frame";
	public static final String HEADCHSBTN_FILENAME = "headchsbtn";
	public static final String BODYCHSBTN_FILENAME = "bodychsbtn";
	public static final String FRAMECHSBTN_FILENAME = "framechsbtn";
	public static final String GIF_ENDNAME=".gif";
	public static final String JPG_ENDNAME=".jpg";
	public static final String PNG_ENDNAME=".png";
	//��ʱ�洢���ļ���
	public static final String GIF_STORENAME = "result.gif";
	public static final String JPG_STORENAME = "result.jpg";
	public static final String PNG_STORENAME = "result.png";
	//GIFͼƬĬ�ϱ�����Ŀ¼���ļ���
	public static final String GIFBG_FOLDERNAME = "background";
	public static final String GIFBG_FILENAME = "bg_320_350.png";
	//�����Ŀ¼,������ز����ɹ������δ����ֱ�ӷ���
	public static final String CAMPIC_ROOT = "mnt/sdcard/DCIM/Camera/";
	public static final int CAM_RETURN_OK = -1;
	public static final int CAM_RETURN_EMPTY = 0;
	//����΢����Ȩ�޲���APP KEY
	public static final String APP_KEY="3415170594";
	
	//����΢����REDIRECT_URL��������������ԡ�����LINK��ַ��
	//public static final String REDIRECT_URL = "http://www.grocamp.com/showgif.html";
	public static final String REDIRECT_URL = "http://115.28.4.190";  
	//����΢����Ȩ�޲���
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
