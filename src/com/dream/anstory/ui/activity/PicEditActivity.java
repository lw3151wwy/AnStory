package com.dream.anstory.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout.Alignment;
import android.text.InputFilter;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ZoomButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.dream.anstory.R;
import com.dream.anstory.listener.MyGestureListener;
import com.dream.anstory.listener.ShakeListener;
import com.dream.anstory.listener.ShakeListener.OnShakeListener;
import com.dream.anstory.util.AppConstantS;
import com.dream.anstory.util.GifModel;
import com.dream.anstory.util.Util;
import com.showgif.gifview.GifImageType;
import com.showgif.gifview.GifView;
import com.showgif.jpg2gif.JpgToGif;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;

public class PicEditActivity extends Activity implements OnClickListener {

	public static final String TAG = "com.showgif.gif.ui.PicEditActivity";

	//头部的一级菜单栏容量
	private final int headLayer1num = 5;
	//身子的一级菜单栏容量
	private final int bodyLayer1num = 9;
	//背景的一级菜单栏容量
	private final int frameLayer1num = 5;
	//头部和身子二级菜单容量，目前都是4
	private final int layer2num = 4;
	
	// 摇一摇的监听类
	private ShakeListener mShakeListener;
	public static boolean isFromWX = false;
	// gif背景图
	private ImageView gifBg;
	// 头，身子，背景，用于画图
	private Bitmap backgroundbitm;
	// 手势类
	GestureDetector gestureMyIvDetector;
	// 用于线程控制
	public static final int MATCH_A_FRAME = 6; //拼接完一帧图片，HandlerMsg
	//handler屏幕晃动MSG
	public static final int SENSOR_SHAKE = 10;
	private final static int TAKE_PHOTO = 11;//照相返回码
	private final static int CHS_PHOTO = 12;//相册返回码
	//handler探出软键盘MSG 
	final static public int OUTPUT_KEYBOARD = 20;
	//按钮状态，100为打开，200为关闭
	public static final int BUTTON_OPEN = 100;
	public static final int BUTTON_CLOSE = 200;
	
	public WindowManager wm;
	public static int devWid;
	public static int devHei;
	private Bundle bundle;

	private Button topbar_btnLeft;//公用topbar的左按钮
	private Button topbar_btnRight;//公用topbar的右按钮

	private Button ibCam;
	private Button ibDelCam;
	private Button addWord;
	
	private Bitmap headBitm;
	private Bitmap bodyBitm;
	private Button changehead;	//选择头部按钮
	private Button changebody;	//选择身子按钮
	private Button changeframe;	//选择边框按钮
	
	HorizontalScrollView chsHeadHsv;//头部的1级菜单父容器
	LinearLayout chsHeadLay;	//头部1级菜单子容器
	
	HorizontalScrollView chsBodyHsv;//身子的1级菜单父容器
	LinearLayout chsBodyLay;	//身子1级菜单子容器
	
	HorizontalScrollView chsFrameHsv;//边框的1级菜单父容器
	LinearLayout chsFrameLay;	//边框1级菜单子容器
	
	LinearLayout chsHeadBodyLay2;	//选择头部和身子2级菜单父容器
	ArrayList<ImageView> lay2Ivs;	//存放当前显示的头部或身子的2级菜单图片
	
	private TextView gifShowWord;
	private File mPhotoFile;//拍照后保存的照片文件
	private String mPhotoPath;//拍照后保存的文件路径
	private ImageView help_hand1;
	private ImageView help_hand2;
	
	private IWXAPI mmAPI;
	private int curGifNumini = -1;
	int headClassNum = 0;
	int bodyClassNum = 0;
	int bodyPressed = 0;
	int headPressed = 0;
	ImageView gifHead;
	GifView gifBody;
	Context context;
	GifModel gm = new GifModel();
	ProgressDialog makeGifTimeDialog;//动图生成时的进度条
	int count = 0; //动图生成时的进度数
	
	boolean isFromPicShareActivity = false;	
	private boolean isFromStoryMode = false;//是否来自故事模式 
	private boolean isEdit = false;	//是否来自编辑模式 
	private int curGifNum; 	//当前编辑的图片序号
	MakeGifTask mgTask;//合成图片的异步任务
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundle = intent.getExtras();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picedit);
		System.out.println("oncreate!");
		bundle = getIntent().getExtras();
		//判断来自哪个ACTIVITY
		if(bundle.getString(AppConstantS.FROM_ACTIVITY_NAME).equals(StoryEditActivity.class.getName())) {
			isFromStoryMode = true;
			curGifNum = bundle.getInt(AppConstantS.STORY_GIF_NUM);
			
			if (bundle.getInt(AppConstantS.STORY_MODE)==StoryEditActivity.STORY_EDIT) {
				isEdit = true;
			}
		}else if(bundle.getString(AppConstantS.FROM_ACTIVITY_NAME).equals(PicShareActivity.class.getName())){
			isFromPicShareActivity= true;
			gifBody.restartGifAnimation();
		}	
		
		context = this;
		wm =  (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		devWid = wm.getDefaultDisplay().getWidth();//屏幕宽度

		initData();
		initButton();
		
		//初始化移动和缩放参数
		MyGestureListener.finalLeft = 0;
		MyGestureListener.finalTop = 0;
		MyGestureListener.finalRight = 0;
		MyGestureListener.finalBottom = 0;
		//初始状态设定
		int inihead;
		int inibody;
		int left;
		int right;
		int top;
		int bottom;
		
		if (isFromStoryMode){			
			//故事模式
			if(isEdit){
				//编辑模式
				curGifNumini=curGifNum;
			}else{
				//插入模式
				if (curGifNum != 0){
					//采用上一张的
					curGifNumini=curGifNum-1;
				}
			}
		}
		if (curGifNumini == -1){
			//采用默认设置
			ranChsPic();
		}else{
			System.out.println("记忆设计");
			//采用记忆设置
			inihead = Util.gmList.get(curGifNumini).getHead();
			inibody = Util.gmList.get(curGifNumini).getBody();
			gifHead = (ImageView) findViewById(R.id.edit_head);
			gifHead.setImageBitmap(Util.getImageFromAssetFile(this, "head",
					"head"+inihead+".png"));
			for (int i = 0;i < AppConstantS.GIF_FRAMECOUNT;i++) {
				Util.head[i] = Util.getImageFromAssetFile(this, AppConstantS.HEADNAME
						,AppConstantS.HEADNAME + inihead + AppConstantS.PNG_ENDNAME);
			}
			gifBody = (GifView) findViewById(R.id.edit_body);
			//gifBody.setScaleType(ScaleType.CENTER);
		
			gifBody.setGifImage(Util.getInputStreamFromAsset(this, "body"+inibody,
					"gif"+AppConstantS.GIF_ENDNAME));
			Util.curShowingHead = inihead;
			Util.curShowingBody = inibody;
			if (isEdit){
				//以记忆参数初始化移动与缩放
				int widthini = devWid-devWid/10;
				int heightini = (devWid-devWid/10)*AppConstantS.FINAL_GIF_HEIGHT/AppConstantS.FINAL_GIF_WIDTH;
				//Log.v(TAG,"ini width height"+right+bottom);
				RelativeLayout.LayoutParams setLayoutParams = new LayoutParams((int) widthini, (int) heightini);
				float inileft = Util.gmList.get(curGifNumini).getLeft();
				float iniright = Util.gmList.get(curGifNumini).getRight();
				Log.v(TAG,"ini width height"+inileft+iniright);
				if (inileft != iniright){
					float initop = Util.gmList.get(curGifNumini).getTop();
					float inibottom = Util.gmList.get(curGifNumini).getBottom();
					left = (int)(inileft*widthini);
					top = (int)(initop*heightini);
					right = (int)(widthini-iniright*widthini);
					bottom = (int)(heightini-inibottom*heightini);
					Log.v(TAG,"ini num"+curGifNumini);
					Log.v(TAG,"ini margin"+left+top+right+bottom+inileft+initop+iniright+inibottom);
					setLayoutParams.width = widthini-left-right;
					setLayoutParams.height = heightini-top-bottom;
					setLayoutParams.setMargins(left, top, right, bottom);
					gifHead.setLayoutParams(setLayoutParams);
					gifBody.setLayoutParams(setLayoutParams);
					//初始化移动和缩放参数
					MyGestureListener.finalLeft = inileft;
					MyGestureListener.finalTop = initop;
					MyGestureListener.finalRight = iniright;
					MyGestureListener.finalBottom = inibottom;				
				}							
			}
		}
		//gifHead.setGifImageType(GifImageType.COVER);
		//gifHead.setLoopAnimation();
		gifBody.setGifImageType(GifImageType.COVER);
		gifBody.setLoopAnimation();
		

		
		//帮助动画
		SharedPreferences preferences;
		SharedPreferences.Editor editors;
		preferences = getSharedPreferences("help_showtimes", Context.MODE_PRIVATE);
		editors = preferences.edit();
		int times = preferences.getInt("times", 0);
		Log.v(TAG,"times1:"+times);
		help_hand1 = (ImageView) findViewById(R.id.help_anim_hand1);
		help_hand2 = (ImageView) findViewById(R.id.help_anim_hand2);
		if (times==0 || times>=5){
			Log.v(TAG,"times2:"+times);
			Animation helpAnim1 = AnimationUtils.loadAnimation(this, R.anim.help_translation_1);
			Animation helpAnim2 = AnimationUtils.loadAnimation(this, R.anim.help_translation_2);
			Animation helpAnim3 = AnimationUtils.loadAnimation(this, R.anim.help_translation_3);
			Animation helpAnim4 = AnimationUtils.loadAnimation(this, R.anim.help_translation_3);
			helpAnim1.setAnimationListener(new Animation.AnimationListener(){
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					help_hand1.setVisibility(View.INVISIBLE);
					help_hand2.setVisibility(View.INVISIBLE);
				}
	
				public void onAnimationRepeat(Animation animation) {
				}
	
				public void onAnimationStart(Animation animation) {
				}});
			help_hand1.startAnimation(helpAnim1);
			help_hand2.startAnimation(helpAnim2);
			gifHead.startAnimation(helpAnim3);
			gifBody.startAnimation(helpAnim4);
			times = 0;			
		}else{
			help_hand1.setVisibility(View.INVISIBLE);
			help_hand2.setVisibility(View.INVISIBLE);
		}
		times = times +1;
		editors.putInt("times", times);
		editors.commit();
		Log.v(TAG,"times3:"+preferences.getInt("times", 0));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//直接点返回键
		if(resultCode==0) {
			return;
		}
		//拍照
		if (requestCode == TAKE_PHOTO) {
			Bitmap bitmap = Util.calPicFromPath(mPhotoPath);
			if (bitmap != null) {
				gifBg.setImageBitmap(bitmap);
				Util.background = bitmap;
			}
		}   
		// 调用Gallery返回的  
		else if(requestCode ==  CHS_PHOTO) {
			//外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            ContentResolver resolver = getContentResolver();
        	//获得图片的URI
            Uri originalUri = data.getData();       
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
        
	        Bitmap bitmap = Util.calPicFromPath(path);
			if (bitmap!= null) {
				gifBg.setImageBitmap(bitmap);
				Util.background = bitmap;
			}  
        }  
	}
	
	//初始化数据
	private void initData() {
		Log.v(TAG, "初始化数据");
		//通过微信直接打开程序时，分享完后直接关闭程序并不进入分享页面，所以需要这里直接注册微信。
		mmAPI = WXAPIFactory.createWXAPI(PicEditActivity.this,
				"wxf563365665427632", false);
		mmAPI.registerApp("wxf563365665427632");
		// 创建存储目录
		File file = new File(Util.getAppStorePath());
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void initButton() {
		gifHead = (ImageView)findViewById(R.id.edit_head);
		gifBody = (GifView) findViewById(R.id.edit_body);
		
		// 初始化topbar右边按钮
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		if (isFromStoryMode){
			topbar_btnRight.setBackgroundResource(R.drawable.picedit_topbar_btn_right1_sel);
		}else{
			topbar_btnRight.setBackgroundResource(R.drawable.picedit_topbar_btn_right_sel);
		}
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(this);
		// 初始化topbar按钮
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(this);
		//灵活计算中间gif图片的放置位置
		RelativeLayout tLayout = (RelativeLayout) this.findViewById(R.id.picedit_rellayout);
		LayoutParams params = new LayoutParams(devWid-devWid/10,(devWid-devWid/10)*AppConstantS.FINAL_GIF_HEIGHT
															/AppConstantS.FINAL_GIF_WIDTH);
		params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		tLayout.setLayoutParams(params);
		
		//照背景的按钮
		ibCam = (Button)this.findViewById(R.id.camera);
		ibCam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog a = new AlertDialog.Builder(PicEditActivity.this)
				.setTitle("选择")
				.setCancelable(true)
				.setPositiveButton("相册",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
				        intent.setType("image/*");  
				        startActivityForResult(intent, CHS_PHOTO);  
					}
				}).setNegativeButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
							mPhotoPath = AppConstantS.CAMPIC_ROOT + Util.getPhotoFileName();
							mPhotoFile = new File(AppConstantS.CAMPIC_ROOT + Util.getPhotoFileName());
							if (!mPhotoFile.exists()) {
								mPhotoFile.createNewFile();
							}
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
							startActivityForResult(intent, TAKE_PHOTO);
						} catch (Exception e) {
							Log.v(TAG,"系统照相过程出错,原因：" + e.getMessage());
						}
					}
				}).create();
				a.show();
			}
		});

		//删除拍照背景的按钮
		ibDelCam = (Button)this.findViewById(R.id.delCamera);
		ibDelCam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gifBg.setImageBitmap(Util.getImageFromAssetFile(PicEditActivity.this, 
						 AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME));
				ibDelCam.setVisibility(View.INVISIBLE);
				if(Util.background!=null) {
					Util.background = null;
					backgroundbitm =null;
				}
			}
		});
		
		//添加文字按钮事件
		addWord = (Button) this.findViewById(R.id.addword);		
		addWord.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
				//自动使用上次输入的内容
				if(gifShowWord!=null) {
					gifEditWord.setText(gifShowWord.getText().toString());
				}
				gifShowWord.setMaxWidth(devWid-devWid/10);
				AlertDialog aDlg = new AlertDialog.Builder(PicEditActivity.this)
				.setTitle("添加文字(不超过30字符)")
				.setView(addWordDig)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//刷新显示gif_showword的ui
						gifShowWord.setText(gifEditWord.getText().toString());
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
				handler.sendEmptyMessage(OUTPUT_KEYBOARD);
			}
		});
		//文字框显示
		Typeface mFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/fangzhengjianzhi.ttf");
		gifShowWord = (TextView) this.findViewById(R.id.gif_showword);
		gifShowWord.setTypeface(mFace);
		if (curGifNumini!=-1&&isEdit){
			//以记忆参数初始化文字
			//Log.v(TAG,"记忆文字："+Util.gmList.get(curGifNumini).getBotWord());				
			gifShowWord.setText(Util.gmList.get(curGifNumini).getBotWord());
		}
		//进度条初始化
		makeGifTimeDialog = (ProgressDialog)new ProgressDialog(PicEditActivity.this);
		makeGifTimeDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {		
				//取消进度条时，取消JPG2GIF的异步任务
				mgTask.cancel(true);
			}
		});
		// 选择头部和身子的按钮
		changehead = (Button) this.findViewById(R.id.changehead);
		changebody = (Button) this.findViewById(R.id.changebody);
		changeframe = (Button) this.findViewById(R.id.changeframe);
		//头和身子1级菜单 ，因为SCROLLVIEW里无法放多个子类，因此先放一个LINEARLAYOUT再放子类
		chsHeadHsv= (HorizontalScrollView)this.findViewById(R.id.head_chs_list);
		chsBodyHsv= (HorizontalScrollView)this.findViewById(R.id.body_chs_list);
		chsFrameHsv = (HorizontalScrollView)this.findViewById(R.id.frame_chs_list);
		chsHeadLay = (LinearLayout)findViewById(R.id.headchslayout);
		chsBodyLay = (LinearLayout)this.findViewById(R.id.bodychslayout);
		chsFrameLay = (LinearLayout)this.findViewById(R.id.framechslayout);
		//头部和身子的2级菜单
		chsHeadBodyLay2 = (LinearLayout) this.findViewById(R.id.headbody_chs_lay2);
		chsHeadBodyLay2.setVisibility(View.INVISIBLE);
		// ARRLIST存放子容器，子容器用来显示不同的头身子图选项
		lay2Ivs = new ArrayList<ImageView>();
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img1));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img2));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img3));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img4));
		//设置2级菜单的子容器的大小与背景
		lay2Ivs.get(0).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(0).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(1).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(1).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(2).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(2).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(3).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));		
		//lay2Ivs.get(3).setBackgroundResource(R.drawable.picedit_select);
		// 以两个按钮ID记录两个按钮的开关属性，初始默认关闭，点击后开启并改变按钮ID
		changehead.setTag(BUTTON_CLOSE);
		changebody.setTag(BUTTON_CLOSE);
		changeframe.setTag(BUTTON_CLOSE);
		// 头身按钮点击初始化
		initChangeHeadBtn();
		initChangeBodyBtn();
		initChangeFrameBtn();
		// 1级头身菜单初始化
		initHeadBodyLayer1Btn(AppConstantS.HEADNAME,headLayer1num);
		initHeadBodyLayer1Btn(AppConstantS.BODYNAME,bodyLayer1num);
		initHeadBodyLayer1Btn(AppConstantS.FRAMENAME,frameLayer1num);
		// 2级头身菜单初始化
		initHeadBodyLayer2Btn();
		
		//GIF背景图，被身子和头覆盖，有触摸时自动触发手势监听类里的listener		
		gifBg = (ImageView) findViewById(R.id.gifBg);
		if (curGifNumini == -1){
			gifBg.setImageBitmap(Util.getImageFromAssetFile(PicEditActivity.this, 
					AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME));			
		}else{
			if (Util.gmList.get(curGifNumini).getBgBitmap() == null){
				gifBg.setImageBitmap(Util.getImageFromAssetFile(PicEditActivity.this, 
						AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME));
			}else{
				gifBg.setImageBitmap(Util.gmList.get(curGifNumini).getBgBitmap());
				Util.background = Util.gmList.get(curGifNumini).getBgBitmap();
			}
		}
		Handler handlergesture = new Handler();
		gestureMyIvDetector = new GestureDetector(this, new MyGestureListener(
				handler, gifBg, gifHead, gifBody, context), handlergesture, false);
		
		//背景的触摸事件
		gifBg.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.v(TAG, "gesture pointer count:"+event.getPointerCount());
				chsHeadHsv.setVisibility(View.INVISIBLE);
				chsBodyHsv.setVisibility(View.INVISIBLE);
				chsHeadBodyLay2.setVisibility(View.INVISIBLE);
				changehead.setTag(BUTTON_CLOSE);
				changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
				changebody.setTag(BUTTON_CLOSE);
				changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
				gifBody.restartGifAnimation();
				return gestureMyIvDetector.onTouchEvent(event);
			}
		});
	}
	
	//头部菜单按钮的初始化事件
	private void initChangeHeadBtn() {
		changehead.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// 当点击头部按钮操作为：关闭--〉打开
					// 1、改变头部TAG属性为打开
					// 2、 显示第一行头部菜单
					changehead.setTag(BUTTON_OPEN);					
					changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_pressed);
					chsHeadHsv.setVisibility(View.VISIBLE);
					// 3、关掉身子和背景的选择菜单
					closeChangeBody();
					closeChangeFrame();
					// 4、清除之前头部相关按钮的选中状态
					cancelLayer1HeadChs();
					cancelLayer2ChsState();
				} else {
					// 当点击头部按钮操作为：打开--〉关闭
					// １、点击后，关闭两行头部菜单
					// ２、重启身子的动画
					changehead.setTag(BUTTON_CLOSE);
					chsHeadHsv.setVisibility(View.INVISIBLE);
					chsHeadBodyLay2.setVisibility(View.INVISIBLE);
					changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
					gifBody.restartGifAnimation();
				}
			}
		});
	}
	
	//身子按钮初始化
	private void initChangeBodyBtn() {
		//身子菜单按钮的事件
		changebody.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// 当点击身子按钮操作为：关闭--〉打开
					// 1、改变身子按钮TAG属性为打开
					// 2、 显示第一行身子菜单					
					changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_pressed);
					changebody.setTag(BUTTON_OPEN);
					chsBodyHsv.setVisibility(View.VISIBLE);
					
					// 3、 如果此时头和身子菜单为打开状态
					closeChangeHead();
					closeChangeFrame();
					// 4、清除之前头和身子相关按钮的选中状态
					cancelLayer1BodyChs();
					cancelLayer2ChsState();
				} else {
					// 当点击身子按钮操作为：打开--〉关闭
					// １、点击后，关闭两行身子菜单
					// ２、设置身子按钮ＴＡＧ属性为关闭
					// ３、重启身子的动画
					changebody.setTag(BUTTON_CLOSE);
					chsBodyHsv.setVisibility(View.INVISIBLE);
					chsHeadBodyLay2.setVisibility(View.INVISIBLE);
					changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
					gifBody.restartGifAnimation();
				}
			}
		});
	}
	
	//背景菜单初始化
	private void initChangeFrameBtn() {
		// 身子菜单按钮的事件
		changeframe.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// 当点击身子按钮操作为：关闭--〉打开
					// 1、改变身子按钮TAG属性为打开
					// 2、 显示第一行身子菜单
					changeframe.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_pressed);
					changeframe.setTag(BUTTON_OPEN);
					chsFrameHsv.setVisibility(View.VISIBLE);
					
					// 3、 如果此时头或身子部菜单为打开状态
					closeChangeBody();
					closeChangeHead();
					// 4、清除之前身子相关按钮的选中状态
					cancelLayer1BodyChs();
					cancelLayer2ChsState();
				} else {
					// 当点击身子按钮操作为：打开--〉关闭
					// １、点击后，关闭两行身子菜单
					// ２、设置身子按钮ＴＡＧ属性为关闭
					// ３、重启身子的动画
					changeframe.setTag(BUTTON_CLOSE);
					chsFrameHsv.setVisibility(View.INVISIBLE);
					chsHeadBodyLay2.setVisibility(View.INVISIBLE);
					changeframe.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
					gifBody.restartGifAnimation();
				}
			}
		});
	}
		
	private void closeChangeHead() {
		// 3、 如果此时头部菜单为打开状态
		// （１）设置头部按钮ＴＡＧ属性为关闭
		// （２）隐藏头部的两个菜单
		if ((Integer) changehead.getTag() == BUTTON_OPEN) {
			changehead.setTag(BUTTON_CLOSE);
			chsHeadHsv.setVisibility(View.INVISIBLE);
			chsHeadBodyLay2.setVisibility(View.INVISIBLE);
			changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
		}
	}
	
	private void closeChangeBody() {
		// 3、 如果此时身子菜单为打开状态
		// （１）设置头部按钮ＴＡＧ属性为关闭
		// （２）隐藏头部的两个菜单
		if ((Integer) changebody.getTag() == BUTTON_OPEN) {
			changebody.setTag(BUTTON_CLOSE);
			chsBodyHsv.setVisibility(View.INVISIBLE);
			chsHeadBodyLay2.setVisibility(View.INVISIBLE);
			changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
		}
	}
	
	private void closeChangeFrame() {
		// 3、 如果此时背景菜单为打开状态
		// （１）设置背景按钮ＴＡＧ属性为关闭
		// （２）隐藏头部的两个菜单
		if ((Integer) changeframe.getTag() == BUTTON_OPEN) {
			changeframe.setTag(BUTTON_CLOSE);
			chsFrameHsv.setVisibility(View.INVISIBLE);
			changeframe.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
		}
	}
	
	//初始化头和身子第一层按钮和对应点击事件,count为菜单包含的选项数
	private void initHeadBodyLayer1Btn(final String s,int layer1num) {
		System.out.println("string"+s);
		//遍历初始化1级菜单的每一个按钮
		for(int i=0;i<layer1num;i++) {
			//1、计算其对应的图片编号
			final int  num = i*layer2num+1;
			//2、初始化自身的图片
			final RelativeLayout picLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.simpleitem, null);
			picLayout.setLayoutParams(new RelativeLayout.LayoutParams((int)(devWid/4.5), (int)(devWid/4.5)));
			picLayout.setBackgroundResource(R.drawable.picedit_class_select);
			final ImageView siv = (ImageView) picLayout.findViewById(R.id.img);
			//picLayout.setId(i);
			//3、为自身加入点击事件
			//	（1）、计算 2级子菜单的显示图片
			//　	（2）、显示２级子菜单　
			//	（3）、清除所有变色按钮
			//  （4）、选中按钮变色
			siv.setOnClickListener(new OnClickListener() {	
				public void onClick(View v) {
					if(s.equals(AppConstantS.HEADNAME)) {
						setHeadAndBodyLayer2(num);
						cancelLayer1HeadChs();
						chsHeadBodyLay2.setVisibility(View.VISIBLE);
					}
					if(s.equals(AppConstantS.BODYNAME)) {
						setHeadAndBodyLayer2(num);
						cancelLayer1BodyChs();
						chsHeadBodyLay2.setVisibility(View.VISIBLE);
					}
					if(s.equals(AppConstantS.FRAMENAME)) {
						cancelLayer1BodyChs();
						cancelLayer1HeadChs();
						cancelLayer2ChsState();
						
						if(gifBg!=null){
							Util.background = Util.getImageFromAssetFile(context,
									AppConstantS.FRAMENAME,AppConstantS.FRAMENAME+ (((num-1)/layer2num)+1) + AppConstantS.PNG_ENDNAME);
							gifBg.setImageBitmap(Util.background);
						}
						return;
					}
					cancelLayer2ChsState();
					picLayout.setBackgroundResource(R.drawable.picedit_selected);
				
				}
			});
			//4、加入触摸事件，当手指按下１级菜单时，动画停止播放，以防滑动１级菜单的时候卡
			siv.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN) {
						gifBody.pauseGifAnimation();
					}
					return false;
				}
			});
			
			//5、判断是头部菜单和是身子菜单，设置自身对应编号的图片，将自身ADDVIEW进1级菜单的相应位置
			if(s.equals(AppConstantS.HEADNAME)) {
				System.out.println("width"+Util.getImageFromAssetFile(context,
						AppConstantS.HEADCHSBTN_FILENAME,AppConstantS.HEADNAME + num + AppConstantS.PNG_ENDNAME).getWidth());
				siv.setImageBitmap(Util.getImageFromAssetFile(context,
					AppConstantS.HEADCHSBTN_FILENAME,AppConstantS.HEADNAME + num + AppConstantS.PNG_ENDNAME));
				chsHeadLay.addView(picLayout,i);
			} else if(s.equals(AppConstantS.BODYNAME)) {
				siv.setImageBitmap(Util.getImageFromAssetFile(context,
							AppConstantS.BODYNAME+num,"class" + AppConstantS.PNG_ENDNAME));
				chsBodyLay.addView(picLayout,i);
			} else if(s.equals(AppConstantS.FRAMENAME)) {
				siv.setImageBitmap(Util.getImageFromAssetFile(context,
						AppConstantS.FRAMENAME,AppConstantS.FRAMENAME+(i+1) + AppConstantS.PNG_ENDNAME));
				chsFrameLay.addView(picLayout,i);
			}
		}
	}

	//预先计算好头身的二级菜单的图片，存放在链表中
	private void setHeadAndBodyLayer2(final int num) {
		for (int i = 0; i < 4; i++) {
			lay2Ivs.get(i).setTag(num+i);
			if((Integer)changehead.getTag()==BUTTON_OPEN) {
				lay2Ivs.get(i).setImageBitmap(Util.getImageFromAssetFile
					(PicEditActivity.this, "headchsbtn", "head" + ((Integer)lay2Ivs.get(i).getTag())
						+ AppConstantS.PNG_ENDNAME));
			} else {
				lay2Ivs.get(i).setImageBitmap(Util.getImageFromAssetFile(PicEditActivity.this, AppConstantS.BODYNAME + ((Integer)lay2Ivs.get(i).getTag()), "color1" + ".png"));
			}
		}
	}
	
	//初始化头身的二级菜单和对应点击事件
	private void initHeadBodyLayer2Btn() {
		//遍历每一个二级菜单的对应按钮，分别设置点击相应事件
		for(int i=0;i<layer2num;i++) {
			lay2Ivs.get(i).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//头部二级菜单
					if((Integer)changehead.getTag()==BUTTON_OPEN) {
						//1、显示对应的头
						//2、记录头的编号
						//3、记录头的每一帧，其实头只有一帧（此处后期进行结构修改）
						//4、恢复身子的动态显示
						gifHead.setImageBitmap(Util.getImageFromAssetFile(context, "head", "head" + ((Integer)v.getTag()) + ".png"));
						Util.curShowingHead = ((Integer)v.getTag());
					
						for (int i = 0;i < AppConstantS.GIF_FRAMECOUNT;i++) {
							Util.head[i] = Util.getImageFromAssetFile(PicEditActivity.this,
									"head", "head" + ((Integer)v.getTag()) + ".png");
						}
						gifBody.restartGifAnimation();
					}
					//身子二级菜单
					else if((Integer)changebody.getTag()==BUTTON_OPEN) {
						//1、显示对应的身子动图
						//2、记录身子编号
						gifBody.setGifImage(Util.getInputStreamFromAsset(context, "body"+((Integer)v.getTag()), "gif.gif"));
						Util.curShowingBody = ((Integer)v.getTag());
					}
					//最后，之前的选中状态清空，本次选中按钮变色
					cancelLayer2ChsState();
					v.setBackgroundResource(R.drawable.picedit_selected);
				}
			});
		}	
	}
	
	//取消头部和身子的第二层菜单选中状态
	private void cancelLayer2ChsState() {
		for(int i=0;i<lay2Ivs.size();i++) {
			lay2Ivs.get(i).setBackgroundResource(0);
		}
	}
	//取消头部第一层菜单的所有选中状态
	private void cancelLayer1HeadChs() {
		for(int i=0;i<chsHeadLay.getChildCount();i++) {
			chsHeadLay.getChildAt(i).setBackgroundResource(R.drawable.picedit_class_select);
		}
	}
	//取消头部第一层菜单的所有选中状态
	private void cancelLayer1BodyChs() {
		for(int i=0;i<chsBodyLay.getChildCount();i++) {
			chsBodyLay.getChildAt(i).setBackgroundResource(R.drawable.picedit_class_select);
		}
	}
	//随机选图
	private void ranChsPic() {
		// 改变当前头和身子的图编号
		Util.curShowingHead = (int) (Math.random() * AppConstantS.headNumber + 1);
		Util.curShowingBody = (int) (Math.random() * AppConstantS.bodyNumber + 1);
		gifHead.setImageBitmap(Util.getImageFromAssetFile(context,
				"head", "head" + Util.curShowingHead + ".png"));
		gifBody.setGifImage(Util.getInputStreamFromAsset(context,
				AppConstantS.BODYNAME + Util.curShowingBody,  "gif"+ AppConstantS.GIF_ENDNAME));
		for (int i = 0;i < AppConstantS.GIF_FRAMECOUNT;i++)	{
			Util.head[i] = Util.getImageFromAssetFile(context,AppConstantS.HEADNAME, 
				AppConstantS.HEADNAME+Util.curShowingHead + AppConstantS.PNG_ENDNAME);
		}
	}
	
	// 后台线程处理点击事件和摇动操作
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handerler线程收到消息" + msg);
			super.handleMessage(msg);
			switch (msg.what) {
				case SENSOR_SHAKE:
					Log.v(TAG, "编辑页面中，用户在摇动手机，正在执行随机为用户选图");
					ranChsPic();
					//摇一摇的声音
					MediaPlayer mp = MediaPlayer.create(PicEditActivity.this, R.raw.shake);
					mp.start();
					break;
					
				case MATCH_A_FRAME:
					makeGifTimeDialog.setProgress(j.getCurFrameNum()*11);
					break; 
					
				case OUTPUT_KEYBOARD:
					InputMethodManager imm = (InputMethodManager) PicEditActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		    }  
			super.handleMessage(msg);      
		}
	};

	private  Bitmap matchPic(Bitmap head, Bitmap body) {
		Bitmap drawBit = Bitmap.createBitmap(320, AppConstantS.FINAL_GIF_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();
		// 用来对位图进行滤波处理
		paint.setFilterBitmap(true);
		// 画上背景
		if (Util.background == null) {
			backgroundbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME);
		} else {
			backgroundbitm = Util.background;
		}
		
		Rect dsRect = new Rect(0, 0, backgroundbitm.getWidth(),
				backgroundbitm.getHeight());
		
		canvas.drawBitmap(backgroundbitm, null, dsRect, paint);
		
		// 画上头部，设定顶部起始位0
		headBitm = head;
		int top = 0;
		int left = 0;
		int right = headBitm.getWidth();
		int bottom = headBitm.getHeight();
		//获取缩放与位移变量
		float finalLeftbitm = MyGestureListener.finalLeft;
		float finalTopbitm = MyGestureListener.finalTop;
		float finalRightbitm = MyGestureListener.finalRight;
		float finalBottombitm = MyGestureListener.finalBottom;		
		if (isEdit) {			
			Util.gmList.get(curGifNum).setLeft(finalLeftbitm);
			Util.gmList.get(curGifNum).setRight(finalRightbitm);
			Util.gmList.get(curGifNum).setTop(finalTopbitm);
			Util.gmList.get(curGifNum).setBottom(finalBottombitm);
		}else{
			gm.setLeft(finalLeftbitm);
			gm.setRight(finalRightbitm);
			gm.setTop(finalTopbitm);
			gm.setBottom(finalBottombitm);
		}
		
		if (finalLeftbitm != finalRightbitm){
			left = (int)(finalLeftbitm*right);
			top = (int)(finalTopbitm*bottom);
			right = (int)(finalRightbitm*right);
			bottom = (int)(finalBottombitm*bottom);
		}
		//Log.v(TAG, "位移及缩放参数"+finalLeftbitm+" "+finalTopbitm+" "+right+" "+bottom+" "+scalebitm);
		
		Rect dstRect = new Rect(left, top, right, bottom);
		Rect srcRect = new Rect(0, 0, headBitm.getWidth(), headBitm.getHeight());
		canvas.drawBitmap(headBitm, srcRect, dstRect, paint);

		
		// 画上身子,设定顶部起始位
		//top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		bodyBitm = body;
		dstRect = new Rect(left, top, right, bottom);
		srcRect = new Rect(0, 0, bodyBitm.getWidth(), bodyBitm.getHeight());
		canvas.drawBitmap(bodyBitm, srcRect, dstRect, paint);

		//文字部分
		if (gifShowWord.getText().toString() != null) {
			Typeface mFace = Typeface.createFromAsset(this.getAssets(), "fonts/fangzhengjianzhi.ttf");
			TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			tp.setFilterBitmap(true);
			//tp.setColor(Color.rgb(8, 98, 104));
			tp.setColor(Color.WHITE);
			tp.setTextSize(30);
			tp.setTypeface(mFace);
			//tp.setFakeBoldText(true);
			tp.setShadowLayer(5, 0, 0, Color.BLACK);
		
			StaticLayout layout = new StaticLayout(gifShowWord.getText().toString(), tp, AppConstantS.FINAL_GIF_WIDTH, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
			canvas.translate(0, AppConstantS.FINAL_GIF_HEIGHT-layout.getHeight()-20);
			
	
			layout.draw(canvas);
			Log.v(TAG, "有文字拼接部分完成");
		}
		return drawBit;
	}

	private void matchPicsFromCurStaticPicStory() {
		Log.v(TAG, "执行makeImages,获取当前展示的头和身子,获取他们的多张静态图片,进行多次的拼接(makeimage)");
		Bitmap image =Bitmap.createBitmap(320, 350, Bitmap.Config.ARGB_8888);
		System.out.println("makestory");
		try {
			
			image = matchPic(Util.head[0], Util.body[0]);
			if (isEdit) {
				Util.gmList.get(curGifNum).setBitmap(image);
			}else{
				gm.setBitmap(image);
			}			
		} catch (Exception e) {
			Log.v(TAG, "执行makeImages失败");
			e.printStackTrace();
		}				
	}

	private Bitmap[] matchPicsFromCurStaticPic() {
		
		Log.v(TAG, "执行makeImages,获取当前展示的头和身子,获取他们的多张静态图片,进行多次的拼接(makeimage)");
		Bitmap[] images = new Bitmap[AppConstantS.GIF_FRAMECOUNT];

		try {
			for (int i = 0; i < images.length; i++) {
				images[i] = matchPic(Util.head[i], Util.body[i]);;
			}
		} catch (Exception e) {
			Log.v(TAG, "执行makeImages失败");
			e.printStackTrace();
		}		
		return images;		
	}
	JpgToGif j;
	public void picsToGif() {
		j = new JpgToGif(this.handler);
	
		String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
		try {
			//参数为,images和存储的目标路径
			j.jpgToGif(matchPicsFromCurStaticPic(), path);
			Log.v(TAG, "制作Gif完毕，图片被保存入：" + path);
		} catch (Exception e) {
			Log.v(TAG, "制作Gif失败了,请检查jpgToGif");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_btn_back:
			if (isFromStoryMode){
				// 返回故事编辑页面
				Intent in = new Intent(PicEditActivity.this,StoryEditActivity.class);
				//in.putExtra(GifModel.NAME, gm);
				setResult(0, in);
				PicEditActivity.this.finish();
			}else{
				//退出后清空记录的图片背景
				if(Util.background!=null) {
					Util.background = null;
				}
				//跳转回首页
				Intent in = new Intent(PicEditActivity.this,MainActivity.class);
				startActivity(in);
				PicEditActivity.this.finish();
			}
			break;
			
		case R.id.topbar_btn_right:
			Log.v(TAG,"用户点击分享，开始制作动图中。。。");
			Log.v(TAG,"判断程序的启动是否来自微信的回调" + PicEditActivity.isFromWX);
			//关闭有该按钮点击功能直到线程结束，防止连续点击
			topbar_btnRight.setClickable(false);
			
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("headNum",Util.curShowingHead+"");
			map.put("bodyNum",Util.curShowingBody+""); 
			MobclickAgent.onEvent(PicEditActivity.this, "sharenum", map);  
			
			if (isFromWX) {
				MobclickAgent.onEvent(PicEditActivity.this, AppConstantS.UMENG_SHARE_FROM_WEIXIN);
			}
			if(!topbar_btnRight.isClickable()) {
				mgTask = new MakeGifTask();
				mgTask.execute();
			}
			break;
		default:
			break;
		}
	}

	
	
	protected void onPause() {
		super.onPause();
		Log.v(TAG , "OnPause...");
		MobclickAgent.onPause(this);
		System.out.println(TAG + "OnPause");
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG , "OnResume...");
		MobclickAgent.onResume(this);
		initShakeLisener();
		if(Util.background != null) {
			ibDelCam.setVisibility(View.VISIBLE);
		} else{
			ibDelCam.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {  
        // TODO Auto-generated method stub  
        Log.i("UserInfoActivity", "onConfigurationChanged");  
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {  
            Log.i("UserInfoActivity", "横屏");  
            Configuration o = newConfig;  
            o.orientation = Configuration.ORIENTATION_PORTRAIT;  
            newConfig.setTo(o);  
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {  
            Log.i("UserInfoActivity", "竖屏");  
        }  
        super.onConfigurationChanged(newConfig);  
    }
	
	class MakeGifTask extends AsyncTask<Void, Void, Boolean> {
		private boolean flag = true;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//还原进度条坐标
		
			//故事模式不在这里生成图片，所以跳过此步
			if(!isFromStoryMode) {
				makeGifTimeDialog.setTitle("正在合成动画中");
				makeGifTimeDialog.setMessage("请稍候...");
				makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				makeGifTimeDialog.show();
			}else{
//				makeGifTimeDialog.setTitle("正在合成动画中");
//				makeGifTimeDialog.setMessage("请稍候...");
//				makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				makeGifTimeDialog.show();
			}
		
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = true;
			gifBody.pauseGifAnimation();
			Log.v(TAG, "开始执行异步任务jpg2gif，gif生成中");
			
			try {
				if (isFromStoryMode){
					matchPicsFromCurStaticPicStory();
				}else{
					picsToGif();
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "jpg2gif失败");
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			super.onCancelled();
			j.stopJpgToGif();
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.dismiss();
				//恢复右键功能
				if(!topbar_btnRight.isClickable()) {
					topbar_btnRight.setClickable(true);
				}
				System.out.println("cancelcount"+count);
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			count = 0;
			//生成完毕后关闭进度条
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.dismiss();
				//恢复右键功能
				if(!topbar_btnRight.isClickable()) {
					topbar_btnRight.setClickable(true);
				}
			}
			if (result) {
				String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
				if (isFromWX) {
					//退出后清空记录的图片背景
					if(Util.background!=null) {
						Util.background = null;
					}
					WXEmojiObject emoji = new WXEmojiObject();
					emoji.emojiPath = path;

					WXMediaMessage msg = new WXMediaMessage(emoji);
					msg.title = "Emoji Title";
					msg.description = "Emoji Description";

					Bitmap bmp = BitmapFactory.decodeFile(path);
					//注意高度宽度数值不要调过大
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
					bmp.recycle();
					msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

					GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
					resp.transaction = getTransaction();
					resp.message = msg;
					mmAPI.sendResp(resp);
					finish();
					System.exit(0);
				} else {
					//应当传入图新生成或编辑图片的编号
					if (isFromStoryMode){						
						//销毁gifhead和gifbody						
						//判断是否故事模式，是的话,把每一个生成的图片的编号记录存放进去，以备以后集体生成
						//存储背景
						//存储底部文字
						//存储头部（静态）
						//存储身子（动态）
						//if (Util.background == null) backgroundbitm = null;
						if (isEdit){
							Util.gmList.get(curGifNum).setBgBitmap(Util.background);
							Util.gmList.get(curGifNum).setBotWord(gifShowWord.getText().toString());
							Util.gmList.get(curGifNum).setHead(Util.curShowingHead);
							//Bitmap[] bodytemp = Util.body;
							Util.gmList.get(curGifNum).setBody(Util.curShowingBody);
						}else{
							gm.setBgBitmap(Util.background);
							gm.setBotWord(gifShowWord.getText().toString());
							gm.setHead(Util.curShowingHead);
							//Bitmap[] bodytemp = Util.body;
							gm.setBody(Util.curShowingBody);
							System.out.println("body+已经存储啦");
							//Util.gmList.add(gm);
							Util.gmList.add(curGifNum,gm);
							System.out.println("size"+Util.gmList.size());
						}
						//退出后清空记录的图片背景
						if(Util.background!=null) {
							Util.background = null;
						}						
						// 初始化topbar右边按钮
						Intent in = new Intent(PicEditActivity.this,StoryEditActivity.class);
						//in.putExtra(GifModel.NAME, gm);
						setResult(StoryEditActivity.ResNumFromPicEdit, in);
						PicEditActivity.this.finish();
					}else {
						Intent in = new Intent(PicEditActivity.this,PicShareActivity.class);
						in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, PicEditActivity.this.getClass().getName());
						PicEditActivity.this.startActivity(in);
						//PicEditActivity.this.finish();
					}
				}
			} 
		}
	}

	//初始化摇一摇的监听
	private void initShakeLisener() {
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				mShakeListener.stop();
				handler.postDelayed(new Runnable() {
					public void run() {
						//发送摇一摇线程消息
						Message msg = new Message();
						msg.what = SENSOR_SHAKE;
						handler.sendMessage(msg);
						mShakeListener.start();
					}
				}, 20);
			}
		});
	}
	
	//微信回调时获得bundle
	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (isFromStoryMode){
					// 返回故事编辑页面
					Intent in = new Intent(PicEditActivity.this,StoryEditActivity.class);
					//in.putExtra(GifModel.NAME, gm);
					setResult(0, in);
					PicEditActivity.this.finish();
				}else{
					//退出后清空记录的图片背景
					if(Util.background!=null) {
						Util.background = null;
					}
					//跳转回首页
					Intent in = new Intent(PicEditActivity.this,MainActivity.class);
					startActivity(in);
					PicEditActivity.this.finish();
				}
			}
		return super.onKeyDown(keyCode, event);
	}
}