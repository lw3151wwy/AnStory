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

	//ͷ����һ���˵�������
	private final int headLayer1num = 5;
	//���ӵ�һ���˵�������
	private final int bodyLayer1num = 9;
	//������һ���˵�������
	private final int frameLayer1num = 5;
	//ͷ�������Ӷ����˵�������Ŀǰ����4
	private final int layer2num = 4;
	
	// ҡһҡ�ļ�����
	private ShakeListener mShakeListener;
	public static boolean isFromWX = false;
	// gif����ͼ
	private ImageView gifBg;
	// ͷ�����ӣ����������ڻ�ͼ
	private Bitmap backgroundbitm;
	// ������
	GestureDetector gestureMyIvDetector;
	// �����߳̿���
	public static final int MATCH_A_FRAME = 6; //ƴ����һ֡ͼƬ��HandlerMsg
	//handler��Ļ�ζ�MSG
	public static final int SENSOR_SHAKE = 10;
	private final static int TAKE_PHOTO = 11;//���෵����
	private final static int CHS_PHOTO = 12;//��᷵����
	//handler̽�������MSG 
	final static public int OUTPUT_KEYBOARD = 20;
	//��ť״̬��100Ϊ�򿪣�200Ϊ�ر�
	public static final int BUTTON_OPEN = 100;
	public static final int BUTTON_CLOSE = 200;
	
	public WindowManager wm;
	public static int devWid;
	public static int devHei;
	private Bundle bundle;

	private Button topbar_btnLeft;//����topbar����ť
	private Button topbar_btnRight;//����topbar���Ұ�ť

	private Button ibCam;
	private Button ibDelCam;
	private Button addWord;
	
	private Bitmap headBitm;
	private Bitmap bodyBitm;
	private Button changehead;	//ѡ��ͷ����ť
	private Button changebody;	//ѡ�����Ӱ�ť
	private Button changeframe;	//ѡ��߿�ť
	
	HorizontalScrollView chsHeadHsv;//ͷ����1���˵�������
	LinearLayout chsHeadLay;	//ͷ��1���˵�������
	
	HorizontalScrollView chsBodyHsv;//���ӵ�1���˵�������
	LinearLayout chsBodyLay;	//����1���˵�������
	
	HorizontalScrollView chsFrameHsv;//�߿��1���˵�������
	LinearLayout chsFrameLay;	//�߿�1���˵�������
	
	LinearLayout chsHeadBodyLay2;	//ѡ��ͷ��������2���˵�������
	ArrayList<ImageView> lay2Ivs;	//��ŵ�ǰ��ʾ��ͷ�������ӵ�2���˵�ͼƬ
	
	private TextView gifShowWord;
	private File mPhotoFile;//���պ󱣴����Ƭ�ļ�
	private String mPhotoPath;//���պ󱣴���ļ�·��
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
	ProgressDialog makeGifTimeDialog;//��ͼ����ʱ�Ľ�����
	int count = 0; //��ͼ����ʱ�Ľ�����
	
	boolean isFromPicShareActivity = false;	
	private boolean isFromStoryMode = false;//�Ƿ����Թ���ģʽ 
	private boolean isEdit = false;	//�Ƿ����Ա༭ģʽ 
	private int curGifNum; 	//��ǰ�༭��ͼƬ���
	MakeGifTask mgTask;//�ϳ�ͼƬ���첽����
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
		//�ж������ĸ�ACTIVITY
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
		devWid = wm.getDefaultDisplay().getWidth();//��Ļ���

		initData();
		initButton();
		
		//��ʼ���ƶ������Ų���
		MyGestureListener.finalLeft = 0;
		MyGestureListener.finalTop = 0;
		MyGestureListener.finalRight = 0;
		MyGestureListener.finalBottom = 0;
		//��ʼ״̬�趨
		int inihead;
		int inibody;
		int left;
		int right;
		int top;
		int bottom;
		
		if (isFromStoryMode){			
			//����ģʽ
			if(isEdit){
				//�༭ģʽ
				curGifNumini=curGifNum;
			}else{
				//����ģʽ
				if (curGifNum != 0){
					//������һ�ŵ�
					curGifNumini=curGifNum-1;
				}
			}
		}
		if (curGifNumini == -1){
			//����Ĭ������
			ranChsPic();
		}else{
			System.out.println("�������");
			//���ü�������
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
				//�Լ��������ʼ���ƶ�������
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
					//��ʼ���ƶ������Ų���
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
		

		
		//��������
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
		//ֱ�ӵ㷵�ؼ�
		if(resultCode==0) {
			return;
		}
		//����
		if (requestCode == TAKE_PHOTO) {
			Bitmap bitmap = Util.calPicFromPath(mPhotoPath);
			if (bitmap != null) {
				gifBg.setImageBitmap(bitmap);
				Util.background = bitmap;
			}
		}   
		// ����Gallery���ص�  
		else if(requestCode ==  CHS_PHOTO) {
			//���ĳ������ContentProvider���ṩ���� ����ͨ��ContentResolver�ӿ�
            ContentResolver resolver = getContentResolver();
        	//���ͼƬ��URI
            Uri originalUri = data.getData();       
            String[] proj = {MediaStore.Images.Media.DATA};
            //������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�
            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
            //���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //�����������ͷ ���������Ҫ����С�ĺ���������Խ��
            cursor.moveToFirst();
            //����������ֵ��ȡͼƬ·��
            String path = cursor.getString(column_index);
        
	        Bitmap bitmap = Util.calPicFromPath(path);
			if (bitmap!= null) {
				gifBg.setImageBitmap(bitmap);
				Util.background = bitmap;
			}  
        }  
	}
	
	//��ʼ������
	private void initData() {
		Log.v(TAG, "��ʼ������");
		//ͨ��΢��ֱ�Ӵ򿪳���ʱ���������ֱ�ӹرճ��򲢲��������ҳ�棬������Ҫ����ֱ��ע��΢�š�
		mmAPI = WXAPIFactory.createWXAPI(PicEditActivity.this,
				"wxf563365665427632", false);
		mmAPI.registerApp("wxf563365665427632");
		// �����洢Ŀ¼
		File file = new File(Util.getAppStorePath());
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void initButton() {
		gifHead = (ImageView)findViewById(R.id.edit_head);
		gifBody = (GifView) findViewById(R.id.edit_body);
		
		// ��ʼ��topbar�ұ߰�ť
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		if (isFromStoryMode){
			topbar_btnRight.setBackgroundResource(R.drawable.picedit_topbar_btn_right1_sel);
		}else{
			topbar_btnRight.setBackgroundResource(R.drawable.picedit_topbar_btn_right_sel);
		}
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(this);
		// ��ʼ��topbar��ť
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(this);
		//�������м�gifͼƬ�ķ���λ��
		RelativeLayout tLayout = (RelativeLayout) this.findViewById(R.id.picedit_rellayout);
		LayoutParams params = new LayoutParams(devWid-devWid/10,(devWid-devWid/10)*AppConstantS.FINAL_GIF_HEIGHT
															/AppConstantS.FINAL_GIF_WIDTH);
		params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		tLayout.setLayoutParams(params);
		
		//�ձ����İ�ť
		ibCam = (Button)this.findViewById(R.id.camera);
		ibCam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog a = new AlertDialog.Builder(PicEditActivity.this)
				.setTitle("ѡ��")
				.setCancelable(true)
				.setPositiveButton("���",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
				        intent.setType("image/*");  
				        startActivityForResult(intent, CHS_PHOTO);  
					}
				}).setNegativeButton("����", new DialogInterface.OnClickListener() {
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
							Log.v(TAG,"ϵͳ������̳���,ԭ��" + e.getMessage());
						}
					}
				}).create();
				a.show();
			}
		});

		//ɾ�����ձ����İ�ť
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
		
		//������ְ�ť�¼�
		addWord = (Button) this.findViewById(R.id.addword);		
		addWord.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
				//�Զ�ʹ���ϴ����������
				if(gifShowWord!=null) {
					gifEditWord.setText(gifShowWord.getText().toString());
				}
				gifShowWord.setMaxWidth(devWid-devWid/10);
				AlertDialog aDlg = new AlertDialog.Builder(PicEditActivity.this)
				.setTitle("�������(������30�ַ�)")
				.setView(addWordDig)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//ˢ����ʾgif_showword��ui
						gifShowWord.setText(gifEditWord.getText().toString());
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
				handler.sendEmptyMessage(OUTPUT_KEYBOARD);
			}
		});
		//���ֿ���ʾ
		Typeface mFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/fangzhengjianzhi.ttf");
		gifShowWord = (TextView) this.findViewById(R.id.gif_showword);
		gifShowWord.setTypeface(mFace);
		if (curGifNumini!=-1&&isEdit){
			//�Լ��������ʼ������
			//Log.v(TAG,"�������֣�"+Util.gmList.get(curGifNumini).getBotWord());				
			gifShowWord.setText(Util.gmList.get(curGifNumini).getBotWord());
		}
		//��������ʼ��
		makeGifTimeDialog = (ProgressDialog)new ProgressDialog(PicEditActivity.this);
		makeGifTimeDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {		
				//ȡ��������ʱ��ȡ��JPG2GIF���첽����
				mgTask.cancel(true);
			}
		});
		// ѡ��ͷ�������ӵİ�ť
		changehead = (Button) this.findViewById(R.id.changehead);
		changebody = (Button) this.findViewById(R.id.changebody);
		changeframe = (Button) this.findViewById(R.id.changeframe);
		//ͷ������1���˵� ����ΪSCROLLVIEW���޷��Ŷ�����࣬����ȷ�һ��LINEARLAYOUT�ٷ�����
		chsHeadHsv= (HorizontalScrollView)this.findViewById(R.id.head_chs_list);
		chsBodyHsv= (HorizontalScrollView)this.findViewById(R.id.body_chs_list);
		chsFrameHsv = (HorizontalScrollView)this.findViewById(R.id.frame_chs_list);
		chsHeadLay = (LinearLayout)findViewById(R.id.headchslayout);
		chsBodyLay = (LinearLayout)this.findViewById(R.id.bodychslayout);
		chsFrameLay = (LinearLayout)this.findViewById(R.id.framechslayout);
		//ͷ�������ӵ�2���˵�
		chsHeadBodyLay2 = (LinearLayout) this.findViewById(R.id.headbody_chs_lay2);
		chsHeadBodyLay2.setVisibility(View.INVISIBLE);
		// ARRLIST�����������������������ʾ��ͬ��ͷ����ͼѡ��
		lay2Ivs = new ArrayList<ImageView>();
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img1));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img2));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img3));
		lay2Ivs.add((ImageView) this.findViewById(R.id.headbody_chs_lay2_img4));
		//����2���˵����������Ĵ�С�뱳��
		lay2Ivs.get(0).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(0).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(1).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(1).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(2).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));
		//lay2Ivs.get(2).setBackgroundResource(R.drawable.picedit_select);
		lay2Ivs.get(3).setLayoutParams(new LinearLayout.LayoutParams(devWid/4, devWid/4));		
		//lay2Ivs.get(3).setBackgroundResource(R.drawable.picedit_select);
		// ��������ťID��¼������ť�Ŀ������ԣ���ʼĬ�Ϲرգ�����������ı䰴ťID
		changehead.setTag(BUTTON_CLOSE);
		changebody.setTag(BUTTON_CLOSE);
		changeframe.setTag(BUTTON_CLOSE);
		// ͷ��ť�����ʼ��
		initChangeHeadBtn();
		initChangeBodyBtn();
		initChangeFrameBtn();
		// 1��ͷ��˵���ʼ��
		initHeadBodyLayer1Btn(AppConstantS.HEADNAME,headLayer1num);
		initHeadBodyLayer1Btn(AppConstantS.BODYNAME,bodyLayer1num);
		initHeadBodyLayer1Btn(AppConstantS.FRAMENAME,frameLayer1num);
		// 2��ͷ��˵���ʼ��
		initHeadBodyLayer2Btn();
		
		//GIF����ͼ�������Ӻ�ͷ���ǣ��д���ʱ�Զ��������Ƽ��������listener		
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
		
		//�����Ĵ����¼�
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
	
	//ͷ���˵���ť�ĳ�ʼ���¼�
	private void initChangeHeadBtn() {
		changehead.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// �����ͷ����ť����Ϊ���ر�--����
					// 1���ı�ͷ��TAG����Ϊ��
					// 2�� ��ʾ��һ��ͷ���˵�
					changehead.setTag(BUTTON_OPEN);					
					changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_pressed);
					chsHeadHsv.setVisibility(View.VISIBLE);
					// 3���ص����Ӻͱ�����ѡ��˵�
					closeChangeBody();
					closeChangeFrame();
					// 4�����֮ǰͷ����ذ�ť��ѡ��״̬
					cancelLayer1HeadChs();
					cancelLayer2ChsState();
				} else {
					// �����ͷ����ť����Ϊ����--���ر�
					// ��������󣬹ر�����ͷ���˵�
					// �����������ӵĶ���
					changehead.setTag(BUTTON_CLOSE);
					chsHeadHsv.setVisibility(View.INVISIBLE);
					chsHeadBodyLay2.setVisibility(View.INVISIBLE);
					changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
					gifBody.restartGifAnimation();
				}
			}
		});
	}
	
	//���Ӱ�ť��ʼ��
	private void initChangeBodyBtn() {
		//���Ӳ˵���ť���¼�
		changebody.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// ��������Ӱ�ť����Ϊ���ر�--����
					// 1���ı����Ӱ�ťTAG����Ϊ��
					// 2�� ��ʾ��һ�����Ӳ˵�					
					changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_pressed);
					changebody.setTag(BUTTON_OPEN);
					chsBodyHsv.setVisibility(View.VISIBLE);
					
					// 3�� �����ʱͷ�����Ӳ˵�Ϊ��״̬
					closeChangeHead();
					closeChangeFrame();
					// 4�����֮ǰͷ��������ذ�ť��ѡ��״̬
					cancelLayer1BodyChs();
					cancelLayer2ChsState();
				} else {
					// ��������Ӱ�ť����Ϊ����--���ر�
					// ��������󣬹ر��������Ӳ˵�
					// �����������Ӱ�ť�ԣ�������Ϊ�ر�
					// �����������ӵĶ���
					changebody.setTag(BUTTON_CLOSE);
					chsBodyHsv.setVisibility(View.INVISIBLE);
					chsHeadBodyLay2.setVisibility(View.INVISIBLE);
					changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
					gifBody.restartGifAnimation();
				}
			}
		});
	}
	
	//�����˵���ʼ��
	private void initChangeFrameBtn() {
		// ���Ӳ˵���ť���¼�
		changeframe.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((Integer) v.getTag() == BUTTON_CLOSE) {
					// ��������Ӱ�ť����Ϊ���ر�--����
					// 1���ı����Ӱ�ťTAG����Ϊ��
					// 2�� ��ʾ��һ�����Ӳ˵�
					changeframe.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_pressed);
					changeframe.setTag(BUTTON_OPEN);
					chsFrameHsv.setVisibility(View.VISIBLE);
					
					// 3�� �����ʱͷ�����Ӳ��˵�Ϊ��״̬
					closeChangeBody();
					closeChangeHead();
					// 4�����֮ǰ������ذ�ť��ѡ��״̬
					cancelLayer1BodyChs();
					cancelLayer2ChsState();
				} else {
					// ��������Ӱ�ť����Ϊ����--���ر�
					// ��������󣬹ر��������Ӳ˵�
					// �����������Ӱ�ť�ԣ�������Ϊ�ر�
					// �����������ӵĶ���
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
		// 3�� �����ʱͷ���˵�Ϊ��״̬
		// ����������ͷ����ť�ԣ�������Ϊ�ر�
		// ����������ͷ���������˵�
		if ((Integer) changehead.getTag() == BUTTON_OPEN) {
			changehead.setTag(BUTTON_CLOSE);
			chsHeadHsv.setVisibility(View.INVISIBLE);
			chsHeadBodyLay2.setVisibility(View.INVISIBLE);
			changehead.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
		}
	}
	
	private void closeChangeBody() {
		// 3�� �����ʱ���Ӳ˵�Ϊ��״̬
		// ����������ͷ����ť�ԣ�������Ϊ�ر�
		// ����������ͷ���������˵�
		if ((Integer) changebody.getTag() == BUTTON_OPEN) {
			changebody.setTag(BUTTON_CLOSE);
			chsBodyHsv.setVisibility(View.INVISIBLE);
			chsHeadBodyLay2.setVisibility(View.INVISIBLE);
			changebody.setBackgroundResource(R.drawable.picedit_toolbar_btn_changebody_nor);
		}
	}
	
	private void closeChangeFrame() {
		// 3�� �����ʱ�����˵�Ϊ��״̬
		// ���������ñ�����ť�ԣ�������Ϊ�ر�
		// ����������ͷ���������˵�
		if ((Integer) changeframe.getTag() == BUTTON_OPEN) {
			changeframe.setTag(BUTTON_CLOSE);
			chsFrameHsv.setVisibility(View.INVISIBLE);
			changeframe.setBackgroundResource(R.drawable.picedit_toolbar_btn_changehead_nor);
		}
	}
	
	//��ʼ��ͷ�����ӵ�һ�㰴ť�Ͷ�Ӧ����¼�,countΪ�˵�������ѡ����
	private void initHeadBodyLayer1Btn(final String s,int layer1num) {
		System.out.println("string"+s);
		//������ʼ��1���˵���ÿһ����ť
		for(int i=0;i<layer1num;i++) {
			//1���������Ӧ��ͼƬ���
			final int  num = i*layer2num+1;
			//2����ʼ�������ͼƬ
			final RelativeLayout picLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.simpleitem, null);
			picLayout.setLayoutParams(new RelativeLayout.LayoutParams((int)(devWid/4.5), (int)(devWid/4.5)));
			picLayout.setBackgroundResource(R.drawable.picedit_class_select);
			final ImageView siv = (ImageView) picLayout.findViewById(R.id.img);
			//picLayout.setId(i);
			//3��Ϊ����������¼�
			//	��1�������� 2���Ӳ˵�����ʾͼƬ
			//��	��2������ʾ�����Ӳ˵���
			//	��3����������б�ɫ��ť
			//  ��4����ѡ�а�ť��ɫ
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
			//4�����봥���¼�������ָ���£����˵�ʱ������ֹͣ���ţ��Է����������˵���ʱ��
			siv.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN) {
						gifBody.pauseGifAnimation();
					}
					return false;
				}
			});
			
			//5���ж���ͷ���˵��������Ӳ˵������������Ӧ��ŵ�ͼƬ��������ADDVIEW��1���˵�����Ӧλ��
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

	//Ԥ�ȼ����ͷ��Ķ����˵���ͼƬ�������������
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
	
	//��ʼ��ͷ��Ķ����˵��Ͷ�Ӧ����¼�
	private void initHeadBodyLayer2Btn() {
		//����ÿһ�������˵��Ķ�Ӧ��ť���ֱ����õ����Ӧ�¼�
		for(int i=0;i<layer2num;i++) {
			lay2Ivs.get(i).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//ͷ�������˵�
					if((Integer)changehead.getTag()==BUTTON_OPEN) {
						//1����ʾ��Ӧ��ͷ
						//2����¼ͷ�ı��
						//3����¼ͷ��ÿһ֡����ʵͷֻ��һ֡���˴����ڽ��нṹ�޸ģ�
						//4���ָ����ӵĶ�̬��ʾ
						gifHead.setImageBitmap(Util.getImageFromAssetFile(context, "head", "head" + ((Integer)v.getTag()) + ".png"));
						Util.curShowingHead = ((Integer)v.getTag());
					
						for (int i = 0;i < AppConstantS.GIF_FRAMECOUNT;i++) {
							Util.head[i] = Util.getImageFromAssetFile(PicEditActivity.this,
									"head", "head" + ((Integer)v.getTag()) + ".png");
						}
						gifBody.restartGifAnimation();
					}
					//���Ӷ����˵�
					else if((Integer)changebody.getTag()==BUTTON_OPEN) {
						//1����ʾ��Ӧ�����Ӷ�ͼ
						//2����¼���ӱ��
						gifBody.setGifImage(Util.getInputStreamFromAsset(context, "body"+((Integer)v.getTag()), "gif.gif"));
						Util.curShowingBody = ((Integer)v.getTag());
					}
					//���֮ǰ��ѡ��״̬��գ�����ѡ�а�ť��ɫ
					cancelLayer2ChsState();
					v.setBackgroundResource(R.drawable.picedit_selected);
				}
			});
		}	
	}
	
	//ȡ��ͷ�������ӵĵڶ���˵�ѡ��״̬
	private void cancelLayer2ChsState() {
		for(int i=0;i<lay2Ivs.size();i++) {
			lay2Ivs.get(i).setBackgroundResource(0);
		}
	}
	//ȡ��ͷ����һ��˵�������ѡ��״̬
	private void cancelLayer1HeadChs() {
		for(int i=0;i<chsHeadLay.getChildCount();i++) {
			chsHeadLay.getChildAt(i).setBackgroundResource(R.drawable.picedit_class_select);
		}
	}
	//ȡ��ͷ����һ��˵�������ѡ��״̬
	private void cancelLayer1BodyChs() {
		for(int i=0;i<chsBodyLay.getChildCount();i++) {
			chsBodyLay.getChildAt(i).setBackgroundResource(R.drawable.picedit_class_select);
		}
	}
	//���ѡͼ
	private void ranChsPic() {
		// �ı䵱ǰͷ�����ӵ�ͼ���
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
	
	// ��̨�̴߳������¼���ҡ������
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handerler�߳��յ���Ϣ" + msg);
			super.handleMessage(msg);
			switch (msg.what) {
				case SENSOR_SHAKE:
					Log.v(TAG, "�༭ҳ���У��û���ҡ���ֻ�������ִ�����Ϊ�û�ѡͼ");
					ranChsPic();
					//ҡһҡ������
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
		// ������λͼ�����˲�����
		paint.setFilterBitmap(true);
		// ���ϱ���
		if (Util.background == null) {
			backgroundbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME);
		} else {
			backgroundbitm = Util.background;
		}
		
		Rect dsRect = new Rect(0, 0, backgroundbitm.getWidth(),
				backgroundbitm.getHeight());
		
		canvas.drawBitmap(backgroundbitm, null, dsRect, paint);
		
		// ����ͷ�����趨������ʼλ0
		headBitm = head;
		int top = 0;
		int left = 0;
		int right = headBitm.getWidth();
		int bottom = headBitm.getHeight();
		//��ȡ������λ�Ʊ���
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
		//Log.v(TAG, "λ�Ƽ����Ų���"+finalLeftbitm+" "+finalTopbitm+" "+right+" "+bottom+" "+scalebitm);
		
		Rect dstRect = new Rect(left, top, right, bottom);
		Rect srcRect = new Rect(0, 0, headBitm.getWidth(), headBitm.getHeight());
		canvas.drawBitmap(headBitm, srcRect, dstRect, paint);

		
		// ��������,�趨������ʼλ
		//top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		bodyBitm = body;
		dstRect = new Rect(left, top, right, bottom);
		srcRect = new Rect(0, 0, bodyBitm.getWidth(), bodyBitm.getHeight());
		canvas.drawBitmap(bodyBitm, srcRect, dstRect, paint);

		//���ֲ���
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
			Log.v(TAG, "������ƴ�Ӳ������");
		}
		return drawBit;
	}

	private void matchPicsFromCurStaticPicStory() {
		Log.v(TAG, "ִ��makeImages,��ȡ��ǰչʾ��ͷ������,��ȡ���ǵĶ��ž�̬ͼƬ,���ж�ε�ƴ��(makeimage)");
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
			Log.v(TAG, "ִ��makeImagesʧ��");
			e.printStackTrace();
		}				
	}

	private Bitmap[] matchPicsFromCurStaticPic() {
		
		Log.v(TAG, "ִ��makeImages,��ȡ��ǰչʾ��ͷ������,��ȡ���ǵĶ��ž�̬ͼƬ,���ж�ε�ƴ��(makeimage)");
		Bitmap[] images = new Bitmap[AppConstantS.GIF_FRAMECOUNT];

		try {
			for (int i = 0; i < images.length; i++) {
				images[i] = matchPic(Util.head[i], Util.body[i]);;
			}
		} catch (Exception e) {
			Log.v(TAG, "ִ��makeImagesʧ��");
			e.printStackTrace();
		}		
		return images;		
	}
	JpgToGif j;
	public void picsToGif() {
		j = new JpgToGif(this.handler);
	
		String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
		try {
			//����Ϊ,images�ʹ洢��Ŀ��·��
			j.jpgToGif(matchPicsFromCurStaticPic(), path);
			Log.v(TAG, "����Gif��ϣ�ͼƬ�������룺" + path);
		} catch (Exception e) {
			Log.v(TAG, "����Gifʧ����,����jpgToGif");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_btn_back:
			if (isFromStoryMode){
				// ���ع��±༭ҳ��
				Intent in = new Intent(PicEditActivity.this,StoryEditActivity.class);
				//in.putExtra(GifModel.NAME, gm);
				setResult(0, in);
				PicEditActivity.this.finish();
			}else{
				//�˳�����ռ�¼��ͼƬ����
				if(Util.background!=null) {
					Util.background = null;
				}
				//��ת����ҳ
				Intent in = new Intent(PicEditActivity.this,MainActivity.class);
				startActivity(in);
				PicEditActivity.this.finish();
			}
			break;
			
		case R.id.topbar_btn_right:
			Log.v(TAG,"�û����������ʼ������ͼ�С�����");
			Log.v(TAG,"�жϳ���������Ƿ�����΢�ŵĻص�" + PicEditActivity.isFromWX);
			//�ر��иð�ť�������ֱ���߳̽�������ֹ�������
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
            Log.i("UserInfoActivity", "����");  
            Configuration o = newConfig;  
            o.orientation = Configuration.ORIENTATION_PORTRAIT;  
            newConfig.setTo(o);  
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {  
            Log.i("UserInfoActivity", "����");  
        }  
        super.onConfigurationChanged(newConfig);  
    }
	
	class MakeGifTask extends AsyncTask<Void, Void, Boolean> {
		private boolean flag = true;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//��ԭ����������
		
			//����ģʽ������������ͼƬ�����������˲�
			if(!isFromStoryMode) {
				makeGifTimeDialog.setTitle("���ںϳɶ�����");
				makeGifTimeDialog.setMessage("���Ժ�...");
				makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				makeGifTimeDialog.show();
			}else{
//				makeGifTimeDialog.setTitle("���ںϳɶ�����");
//				makeGifTimeDialog.setMessage("���Ժ�...");
//				makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				makeGifTimeDialog.show();
			}
		
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = true;
			gifBody.pauseGifAnimation();
			Log.v(TAG, "��ʼִ���첽����jpg2gif��gif������");
			
			try {
				if (isFromStoryMode){
					matchPicsFromCurStaticPicStory();
				}else{
					picsToGif();
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "jpg2gifʧ��");
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
				//�ָ��Ҽ�����
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
			//������Ϻ�رս�����
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.dismiss();
				//�ָ��Ҽ�����
				if(!topbar_btnRight.isClickable()) {
					topbar_btnRight.setClickable(true);
				}
			}
			if (result) {
				String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
				if (isFromWX) {
					//�˳�����ռ�¼��ͼƬ����
					if(Util.background!=null) {
						Util.background = null;
					}
					WXEmojiObject emoji = new WXEmojiObject();
					emoji.emojiPath = path;

					WXMediaMessage msg = new WXMediaMessage(emoji);
					msg.title = "Emoji Title";
					msg.description = "Emoji Description";

					Bitmap bmp = BitmapFactory.decodeFile(path);
					//ע��߶ȿ����ֵ��Ҫ������
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
					//Ӧ������ͼ�����ɻ�༭ͼƬ�ı��
					if (isFromStoryMode){						
						//����gifhead��gifbody						
						//�ж��Ƿ����ģʽ���ǵĻ�,��ÿһ�����ɵ�ͼƬ�ı�ż�¼��Ž�ȥ���Ա��Ժ�������
						//�洢����
						//�洢�ײ�����
						//�洢ͷ������̬��
						//�洢���ӣ���̬��
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
							System.out.println("body+�Ѿ��洢��");
							//Util.gmList.add(gm);
							Util.gmList.add(curGifNum,gm);
							System.out.println("size"+Util.gmList.size());
						}
						//�˳�����ռ�¼��ͼƬ����
						if(Util.background!=null) {
							Util.background = null;
						}						
						// ��ʼ��topbar�ұ߰�ť
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

	//��ʼ��ҡһҡ�ļ���
	private void initShakeLisener() {
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				mShakeListener.stop();
				handler.postDelayed(new Runnable() {
					public void run() {
						//����ҡһҡ�߳���Ϣ
						Message msg = new Message();
						msg.what = SENSOR_SHAKE;
						handler.sendMessage(msg);
						mShakeListener.start();
					}
				}, 20);
			}
		});
	}
	
	//΢�Żص�ʱ���bundle
	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (isFromStoryMode){
					// ���ع��±༭ҳ��
					Intent in = new Intent(PicEditActivity.this,StoryEditActivity.class);
					//in.putExtra(GifModel.NAME, gm);
					setResult(0, in);
					PicEditActivity.this.finish();
				}else{
					//�˳�����ռ�¼��ͼƬ����
					if(Util.background!=null) {
						Util.background = null;
					}
					//��ת����ҳ
					Intent in = new Intent(PicEditActivity.this,MainActivity.class);
					startActivity(in);
					PicEditActivity.this.finish();
				}
			}
		return super.onKeyDown(keyCode, event);
	}
}