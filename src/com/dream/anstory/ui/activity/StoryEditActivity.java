package com.dream.anstory.ui.activity;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dream.anstory.R;
import com.dream.anstory.R.color;
import com.dream.anstory.util.AppConstantS;
import com.dream.anstory.util.GifModel;
import com.dream.anstory.util.Util;
import com.showgif.jpg2gif.AnimatedGifEncoder1;

/*
 * �߼���Ҫ�޸�
 * ���µ����˳�ʱ��Ҫ����
 * 12/24 wwy
 */
public class StoryEditActivity extends Activity{
	public static String TAG = "com.dream.anstory.ui.activity.StoryEditActivity";
	public static final int STORY_EDIT = 1;
	public static final int STORY_ADD = 2;
	public static final int ResNumFromPicAdd = 100;
	public static final int ResNumFromPicEdit = 200;
	private int curGifNum = 0;
	
	//������С������������
	LinearLayout mainLayout;
	//����������Ķ���
	private ArrayList<RelativeLayout> picList;
	//�����ͼ
	ImageView addNewGif;
	//��������
	Button makeNewStory;
	//��ͼ����ʱ�Ķ�ȡ����
	ProgressDialog makeGifTimeDialog;
	//���ڹ�����
	public WindowManager wm;
	//��Ļ��ȸ߶�
	public static int devWid;
	public static int devHei;
	//����topbar����ť
	private Button topbar_btnLeft;
	//����topbar���Ұ�ť
	private Button topbar_btnRight;
	//����textview
	private TextView titleView;
	//����textview
	private TextView authorView;
	//�����ı�
	private String tempText;
    int count = 0;
	
    ScrollView scrollView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.storyedit);
		initData();
		initBtn();
		
		//һ������ӱ���
		final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
		final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
		gifEditWord.setHint("����Ĺ���һ������");
		//�Զ�ʹ���ϴ����������
		if(titleView!=null) {
			gifEditWord.setText(titleView.getText().toString());
		}
		AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
		.setTitle("��ӹ��±���(�������Ÿ��ַ�)")
		.setView(addWordDig)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//ˢ����ʾgif_showword��ui
				if (gifEditWord.getText().toString()!=null&&gifEditWord.getText().toString().length()!=0){
					titleView.setText(gifEditWord.getText().toString());
				}else{
					titleView.setText("�ҵĹ���");
				}
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("������Ĵ���");
				//gifEditWord.requestFocus();
				//�Զ�ʹ���ϴ����������
				if(tempText!=null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("��Ӵ�����(�������Ÿ��ַ�)")
				.setView(addWordDig)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//ˢ����ʾgif_showword��ui						
						tempText = gifEditWord.getText().toString();
						if (tempText!=null&&tempText.length()!=0){
							authorView.setText("�����ߣ�"+tempText);
						}else{
							authorView.setText("�����ߣ�����");
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("�����ߣ�����");
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				titleView.setText("�ҵĹ���");
				//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("������Ĵ���");
				gifEditWord.requestFocus();
				//�Զ�ʹ���ϴ����������
				if(tempText!=null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("��Ӵ�����(�������Ÿ��ַ�)")
				.setView(addWordDig)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//ˢ����ʾgif_showword��ui						
						tempText = gifEditWord.getText().toString();
						if (tempText!=null&&tempText.length()!=0){
							authorView.setText("�����ߣ�"+tempText);
						}else{
							authorView.setText("�����ߣ�����");
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("�����ߣ�����");
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
			}
		}).create();
		aDlg.show();
	}
	
	private void initData() {
		picList = new ArrayList<RelativeLayout>();	
		//����ͼƬʱҪ�õ��Ľ�����
		makeGifTimeDialog = (ProgressDialog)new ProgressDialog(StoryEditActivity.this);
		//���������,���ڷ�ÿ����������������װ��ÿһ�Ź��µ�Ԥ��ͼƬ��
		mainLayout = (LinearLayout)findViewById(R.id.storylayout);		
		//������Ļ���
		wm =  (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		devWid = wm.getDefaultDisplay().getWidth();
	}
	
	private void initBtn() {
		// ��ʼ��topbar��߰�ť
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearData();
			}
		});
		// ��ʼ��topbar�ұ߰�ť
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				
				if(!picList.isEmpty()) {
					topbar_btnRight.setClickable(false);
					new MakeGifTask().execute();
				} else {
					Toast.makeText(StoryEditActivity.this, "����û����Ӷ���Ŷ...", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		addNewGif=(ImageView) this.findViewById(R.id.addnewgif);
		android.widget.LinearLayout.LayoutParams paramsAddNewGif = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH);
		//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
		addNewGif.setBackgroundResource(R.drawable.storyedit_btn_addgif_big_sel);
		addNewGif.setLayoutParams(paramsAddNewGif);
		addNewGif.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(StoryEditActivity.this,PicEditActivity.class);
				//�Ǵ��ĸ�ģ�鴫��GIF������
				in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
				in.putExtra(AppConstantS.STORY_MODE, STORY_ADD);
				//����ӵڼ��Ż�༭�ڼ���
				curGifNum = Util.gmList.size();
				System.out.println("curgifL:"+curGifNum);
				in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
				//����ӻ��Ǳ༭ģʽ
				//in.putExtra(AppConstantS.STORYMODE_PIC_NUM, value);
				startActivityForResult(in, 100);
			}
		});
		//��ӱ����¼�
		titleView = (TextView) this.findViewById(R.id.titleview);
		android.widget.LinearLayout.LayoutParams paramsTitleView = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, LayoutParams.WRAP_CONTENT);
		paramsTitleView.setMargins(0, devWid/20, 0, 0);
		//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
		titleView.setLayoutParams(paramsTitleView);
		titleView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("����Ĺ���һ������");
				//�Զ�ʹ���ϴ����������
				if(titleView!=null) {
					gifEditWord.setText(titleView.getText().toString());
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("��ӹ��±���(�������Ÿ��ַ�)")
				.setView(addWordDig)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//ˢ����ʾgif_showword��ui
						if (gifEditWord.getText().toString()!=null&&gifEditWord.getText().toString().length()!=0){
							titleView.setText(gifEditWord.getText().toString());
						}else{
							titleView.setText("�ҵĹ���");
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
			}
		});
		//��������¼�
		authorView = (TextView) this.findViewById(R.id.authorview);
		android.widget.LinearLayout.LayoutParams paramsAuthorView = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH/4);
		//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
		authorView.setLayoutParams(paramsAuthorView);
		authorView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("������Ĵ���");
				//�Զ�ʹ���ϴ����������
				if(tempText!=null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("��Ӵ�����(�������Ÿ��ַ�)")
				.setView(addWordDig)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//ˢ����ʾgif_showword��ui						
						tempText = gifEditWord.getText().toString();
						if (tempText!=null&&tempText.length()!=0){
							authorView.setText("�����ߣ�"+tempText);
						}else{
							authorView.setText("�����ߣ�����");
						}					
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Toast.makeText(PicEditActivity.this, "dialogcancel", Toast.LENGTH_LONG).show();
					}
				}).create();
				aDlg.show();
			}
		});
	}
	
	//��������GIF��Ϸ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// ���Ը��ݶ���������������Ӧ�Ĳ���
		if (requestCode == ResNumFromPicAdd && resultCode != 0) {
			//�ı���Ӱ�ť��С
			android.widget.LinearLayout.LayoutParams paramsAddNewGif = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH/4);
			//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
			addNewGif.setBackgroundResource(R.drawable.storyedit_btn_addgif_sel);
			addNewGif.setLayoutParams(paramsAddNewGif);
			//
			Log.v(TAG, "���" + requestCode + resultCode + curGifNum);
			Bitmap pic = Util.gmList.get(curGifNum).getBitmap();
			//��ÿһ��ͼƬ��������
			final RelativeLayout picLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.hot_item, null);

			ImageView siv = (ImageView) picLayout.findViewById(R.id.hotImage);

			// picLayout.setTag(Util.gmList.get(Util.gmList.size()-1);
			// siv.setCount(mainLayout.getChildCount());
			siv.setImageBitmap(pic);
			picList.add(curGifNum, picLayout);
			
			// �������м�gifͼƬ�ķ���λ��
			// RelativeLayout tLayout = (RelativeLayout)
			// this.findViewById(R.id.picedit_rellayout);
			LayoutParams params = new LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			picLayout.setLayoutParams(params);
			//
			mainLayout.addView(picLayout, curGifNum+1);
	
			Button deleteBtn = (Button) picLayout.findViewById(R.id.delPic);
			deleteBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					mainLayout.removeView(picLayout);
					curGifNum = picList.indexOf(picLayout);
					picList.remove(curGifNum);					
					Util.gmList.remove(curGifNum);
				}
			});

			Button addBtn = (Button) picLayout.findViewById(R.id.insertGif);
			addBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent in = new Intent(StoryEditActivity.this, PicEditActivity.class);
					// �Ǵ��ĸ�ģ�鴫��GIF������
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					// ����ӵڼ��Ż�༭�ڼ���
					curGifNum = picList.indexOf(picLayout);
					in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
					//
					startActivityForResult(in, 100);
				}
			});

			picLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG, "�����λ��" + picList.indexOf(picLayout));
					Intent in = new Intent(StoryEditActivity.this, PicEditActivity.class);
					// �Ǵ��ĸ�ģ�鴫��GIF������
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					in.putExtra(AppConstantS.STORY_MODE, STORY_EDIT);
					// ����ӵڼ��Ż�༭�ڼ���
					curGifNum = picList.indexOf(picLayout);
					in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
					// ����ӻ��Ǳ༭ģʽ
					// in.putExtra(AppConstantS.STORYMODE_PIC_NUM, value);
					startActivityForResult(in, 200);
				}
			});
		}
		if (requestCode == ResNumFromPicEdit && resultCode != 0) {
			Log.v(TAG, "�༭" + requestCode + resultCode + curGifNum);
			Bitmap pic = Util.gmList.get(curGifNum).getBitmap();
			// picLayout = (RelativeLayout)
			// getLayoutInflater().inflate(R.layout.hot_item, null);
			final RelativeLayout picLayout = (RelativeLayout) mainLayout.getChildAt(curGifNum+1);
			ImageView iv = (ImageView) picLayout.findViewById(R.id.hotImage);
			iv.setImageBitmap(pic);
			Log.v(TAG, "�༭" + requestCode + resultCode + curGifNum);
		}
		
		scrollView = (ScrollView)findViewById(R.id.scrollview);
		scrollView.post(new Runnable() {
			
			public void run() {
				scrollView.fullScroll(scrollView.FOCUS_DOWN);
			}
		});
	}
	
    //�������չ���GIFͼƬ
	Bitmap matchPic(Bitmap head, Bitmap body, int frame) {
		Bitmap drawBit = Bitmap.createBitmap(AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+176, Bitmap.Config.RGB_565);		
		Log.v(TAG, "��ʼ�ϳɵ�n��1��" + frame + drawBit.getRowBytes() * drawBit.getHeight());
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();		
		// ������λͼ�����˲�����
		paint.setFilterBitmap(true);
		Bitmap titlebitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, "bg_title.png");
		Rect dstReckTitle = new Rect(0, 0, AppConstantS.FINAL_GIF_WIDTH, 88);
		canvas.drawBitmap(titlebitm, null, dstReckTitle, paint);
		int titleHeight = titlebitm.getHeight();
		if (titleView.getText().toString() != null) {
			//Typeface mFace = Typeface.createFromAsset(this.getAssets(), "fonts/fangzhengjianzhi.ttf");
			TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			tp.setFilterBitmap(true);			
			tp.setColor(getResources().getColor(color.title_blue));			
			tp.setTextSize(30);
			//tp.setTypeface(mFace);
			//tp.setShadowLayer(5, 0, 0, Color.BLACK);
			tp.setTextAlign(Align.CENTER);
			
			canvas.drawText(titleView.getText().toString(), AppConstantS.FINAL_GIF_WIDTH / 2, 44 + tp.getTextSize()/2, tp);
		}
		for (int i = 0; i < Util.gmList.size(); i++) {
			//���Ƴ��߽�
			Bitmap maskBit = Bitmap.createBitmap(AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT, Bitmap.Config.ARGB_8888);
			Canvas canvasmask = new Canvas(maskBit);
			Paint paintmask = new Paint();
			
			// ���ϱ���
			Bitmap backgroundbitm;
			if (Util.gmList.get(i).getBgBitmap() == null) {
				backgroundbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME);
			} else {
				backgroundbitm = Util.gmList.get(i).getBgBitmap();
			}
			Rect dsRect = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i,
					backgroundbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i + backgroundbitm.getHeight());
			canvas.drawBitmap(backgroundbitm, null, dsRect, paint);		
		
			// ��ȡ������λ�Ʊ���
			int top = 0;
			int left = 0;
			int right = AppConstantS.FINAL_GIF_WIDTH;
			int bottom = AppConstantS.FINAL_GIF_HEIGHT;
			float finalLeftbitm = Util.gmList.get(i).getLeft();
			float finalTopbitm = Util.gmList.get(i).getTop();
			float finalRightbitm = Util.gmList.get(i).getRight();
			float finalBottombitm = Util.gmList.get(i).getBottom();
			if (finalLeftbitm != finalRightbitm) {
				left = (int) (finalLeftbitm * right);
				top = (int) (finalTopbitm * bottom);
				right = (int) (finalRightbitm * right);
				bottom = (int) (finalBottombitm * bottom);
			}
			Rect dstRectmask = new Rect(left, top, right, bottom);
			Rect srcRectmask = new Rect(0, 0, backgroundbitm.getWidth(), AppConstantS.FINAL_GIF_HEIGHT);
			
			//���ļ������ҳ�ָ����ͷ��
			Bitmap headbitm = Util.getImageFromAssetFile(this, AppConstantS.HEADNAME
					, AppConstantS.HEADNAME + Util.gmList.get(i).getHead() + AppConstantS.PNG_ENDNAME);
			//����ƫ������������ͷ��			
			canvasmask.drawBitmap(headbitm, srcRectmask, dstRectmask, paintmask);
			//���ļ������ҳ�ָ�������ӣ���ǰ֡��+1)
			Bitmap bodybitm = Util.getImageFromAssetFile(StoryEditActivity.this, 
					AppConstantS.BODYNAME + Util.gmList.get(i).getBody(), "color" + (frame + 1) + ".png");
			//����ƫ����������������
			canvasmask.drawBitmap(bodybitm, srcRectmask, dstRectmask, paintmask);
			Rect dstRect = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i, headbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * (i+1));
			Rect srcRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
			canvas.drawBitmap(maskBit, srcRect, dstRect, paint);
			//�ͷ��ڴ�
			maskBit.recycle();
			maskBit = null;
			// ���ֲ���
			if (Util.gmList.get(i).getBotWord() != null) {
				Typeface mFace = Typeface.createFromAsset(this.getAssets(), "fonts/fangzhengjianzhi.ttf");
				TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
				tp.setFilterBitmap(true);
				tp.setColor(Color.WHITE);
				tp.setTextSize(30);
				tp.setTypeface(mFace);
				tp.setShadowLayer(5, 0, 0, Color.BLACK);
				tp.setTextAlign(Align.CENTER);
				canvas.drawText(Util.gmList.get(i).getBotWord(), AppConstantS.FINAL_GIF_WIDTH / 2, titleHeight+320 + AppConstantS.FINAL_GIF_HEIGHT * i, tp);
			}
			
			//���ϱ߿�
			Bitmap borderbitm = Util.getImageFromAssetFile(StoryEditActivity.this, "background", "bg_border.png");
			Rect dsRectborder = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i,
					backgroundbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i + backgroundbitm.getHeight());
			canvas.drawBitmap(borderbitm, null, dsRectborder, paint); 
			//drawable.draw(canvas); 
			//canvas.drawBitmap(backgroundbitm, null, dsRect, paint);
		}
		Bitmap authorbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, "bg_author.png");
		Rect dstReckauthor = new Rect(0, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+88, AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+176);
		canvas.drawBitmap(authorbitm, null, dstReckauthor, paint);
	//	int authorHeight = authorbitm.getHeight();
		if (authorView.getText().toString() != null) {
			//Typeface mFace = Typeface.createFromAsset(this.getAssets(), "fonts/fangzhengjianzhi.ttf");
			TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			tp.setFilterBitmap(true);
			tp.setColor(getResources().getColor(color.author_grey));
			tp.setTextSize(20);			
			//tp.setTypeface(mFace);
			//tp.setShadowLayer(5, 0, 0, Color.BLACK);
			tp.setTextAlign(Align.LEFT);
			canvas.drawText(authorView.getText().toString(), 10, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+88+30, tp);
		}
		Log.v(TAG, "�����ϳɵ�n�ţ�" + frame);		
		return drawBit;
	}
   
    public void picsToGif() {
		String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
		try {
			AnimatedGifEncoder1 e = new AnimatedGifEncoder1();
			e.setRepeat(0);
			e.start(path);
			
			for (int i = 0; i < AppConstantS.GIF_FRAMECOUNT; i++) {
		        System.out.println("������Ϣ��StoryEdtit");
				// ���ò��ŵ��ӳ�ʱ��
				e.setDelay(100);
				
				e.addFrame(matchPic(Util.head[i], Util.body[i],i)); // ��ӵ�֡��
				// pic[i].recycle();
				Message msg = new Message();  
		        msg.what = 10;  
		        handler.sendMessage(msg); 
			}
			e.finish();
			// ����Ϊ,images�ʹ洢��Ŀ��·��
			Log.v(TAG, "����Gif��ϣ�ͼƬ�������룺" + path);
		} catch (Exception e) {
			Log.v(TAG, "����Gifʧ����,����StoryEditActivity");
			e.printStackTrace();
		}
	}
   
    class MakeGifTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			makeGifTimeDialog.setTitle("���ںϳɶ�����");
			makeGifTimeDialog.setMessage("���Ժ�...");
			makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			makeGifTimeDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = true;
			Log.v(TAG, "��ʼִ���첽����jpg2gif��gif������");
			try {
				picsToGif();
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "jpg2gifʧ��");
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//�ָ��Ҽ�����
			if(!topbar_btnRight.isClickable()) {
				topbar_btnRight.setClickable(true);
			}
			//������Ϻ�رս�����
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.cancel();
			}
			Intent in = new Intent(StoryEditActivity.this,PicShareActivity.class);
			in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
			StoryEditActivity.this.startActivity(in);
		}
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:	
				clearData();
		}
		return super.onKeyDown(keyCode, event);
	}
    
    private void clearData() {
    	//��ʱ��Ϊ���˳�ʱ�������������
		curGifNum = 0;
		if(picList!=null) {
			picList.clear();
			System.out.println("�Ѿ����");
			//picList=null;
		}
		if(mainLayout!=null) {
			mainLayout.removeAllViews();
			//mainLayout=null;
		}
		if(Util.gmList!=null) {
			Util.gmList.clear();
		}
		//��ת����ҳ
		Intent in = new Intent(StoryEditActivity.this,MainActivity.class);
		startActivity(in);
		StoryEditActivity.this.finish();
    }

    Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handerler�߳��յ���Ϣ" + msg);
			super.handleMessage(msg);
			switch (msg.what) {
				case 10:
					if(count==100) {
						count = 0;
					}
					count = count + 11; 
					if(count==99) {
						count = count + 1;
					}
					makeGifTimeDialog.setProgress(count);
	            	break; 
		    }  
			super.handleMessage(msg);      
		}
	};
}
