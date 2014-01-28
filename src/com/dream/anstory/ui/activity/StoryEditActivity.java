package com.dream.anstory.ui.activity;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
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
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
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
import com.dream.anstory.util.Util;
import com.showgif.jpg2gif.JpgToGif;

/*
 * �߼���Ҫ�޸�
 * ���µ����˳�ʱ��Ҫ����
 * 12/24 wwy
 */
public class StoryEditActivity extends Activity{
	public static String TAG = "com.dream.anstory.ui.activity.StoryEditActivity";
	public static final int STORY_EDIT = 1;
	public static final int STORY_BOT_ADD = 2;
	public static final int STORY_INSERT = 3;
	// �����߳̿���
	public static final int MATCH_A_FRAME = 6; //ƴ����һ֡ͼƬ��HandlerMsg

	public static final int ResNumFromPicAdd = 100;	//GIF���
	public static final int ResNumFromPicEdit = 200;//GIF�༭
	public static final int ResNumFromCancel = 900; //GIFȡ��
	//��ʼ��ͼƬ�� ��ֻ�б��⣩
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
	//����t
	private TextView titleView;
	//����t
	private TextView authorView;
	//�����ı�
	private String tempText;
    int count = 1;
	
    ScrollView scrollView;
    private MakeGifTask mgTask ;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.storyedit);
		initData();
		initBtn();
		initDialog();
	}

	private void initData() {
		picList = new ArrayList<RelativeLayout>();	
		//����ͼƬʱҪ�õ��Ľ�����
		
		if(makeGifTimeDialog==null) {
			makeGifTimeDialog = (ProgressDialog)new ProgressDialog(StoryEditActivity.this);
			makeGifTimeDialog.setTitle("���ںϳɶ�����");
			makeGifTimeDialog.setMessage("���Ժ�...");
			makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			makeGifTimeDialog.setCancelable(false);
			makeGifTimeDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					//��д����Ϊ������ȡ��֮ǰ,����makegiftimedialogһЩ����,���ֱ�Ӻ���ȡ������׽�¼����޷��޸�.
					//ֹͣƴ������,
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
						//��ֹ�첽������ֹjpg���ѭ��
						j2g.stopJpgToGif();
						mgTask.cancel(true);
						if(!topbar_btnRight.isClickable()) {
							topbar_btnRight.setClickable(true);
						}
						//�ص���һ֡��������������0��dialog�ر� 
						count = 0;
						makeGifTimeDialog.setProgress(0);
						makeGifTimeDialog.dismiss();
					}
					return false;
				}
			});
		} 
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
	
		//��ʼ��topbar�ұ߰�ť
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!picList.isEmpty()) {
					topbar_btnRight.setClickable(false);
					mgTask = new MakeGifTask();
					mgTask.execute();
				} else {
					Toast.makeText(StoryEditActivity.this, "����û����Ӷ���Ŷ...", Toast.LENGTH_LONG).show();
				}
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
					}
				}).create();
				aDlg.show();
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
				in.putExtra(AppConstantS.STORY_MODE, STORY_BOT_ADD);
				// util.gmlist.size��Զ�ȶ������һλ�����1������curgifnumΪ��β֮����Ǹ�����
				curGifNum = Util.gmList.size();
				System.out.println("curgifL:"+curGifNum);
				in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
				//����ӻ��Ǳ༭ģʽ
				startActivityForResult(in, 100);
			}
		});
	}
	
	//��ʼ�����¶Ի���
	private void initDialog() {
		// һ������ӱ���
		final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
		final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
		gifEditWord.setHint("����Ĺ���һ������");
		// �Զ�ʹ���ϴ����������
		if (titleView != null) {
			gifEditWord.setText(titleView.getText().toString());
		}
		AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("��ӹ��±���(�������Ÿ��ַ�)").setView(addWordDig).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// ˢ����ʾgif_showword��ui
				if (gifEditWord.getText().toString() != null && gifEditWord.getText().toString().length() != 0) {
					titleView.setText(gifEditWord.getText().toString());
				} else {
					titleView.setText("�ҵĹ���");
				}
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("������Ĵ���");
				// gifEditWord.requestFocus();
				// �Զ�ʹ���ϴ����������
				if (tempText != null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("��Ӵ�����(�������Ÿ��ַ�)").setView(addWordDig).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// ˢ����ʾgif_showword��ui
						tempText = gifEditWord.getText().toString();
						if (tempText != null && tempText.length() != 0) {
							authorView.setText("�����ߣ�" + tempText);
						} else {
							authorView.setText("�����ߣ�����");
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("�����ߣ�����");
					}
				}).create();
				aDlg.show();
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				titleView.setText("�ҵĹ���");
				// Toast.makeText(PicEditActivity.this, "dialogcancel",
				// Toast.LENGTH_LONG).show();
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("������Ĵ���");
				gifEditWord.requestFocus();
				// �Զ�ʹ���ϴ����������
				if (tempText != null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("��Ӵ�����(�������Ÿ��ַ�)").setView(addWordDig).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// ˢ����ʾgif_showword��ui
						tempText = gifEditWord.getText().toString();
						if (tempText != null && tempText.length() != 0) {
							authorView.setText("�����ߣ�" + tempText);
						} else {
							authorView.setText("�����ߣ�����");
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("�����ߣ�����");
					}
				}).create();
				aDlg.show();
			}
		}).create();
		aDlg.show();
	}

	//��������GIF��Ϸ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// ���Ը��ݶ���������������Ӧ�Ĳ���
		if(requestCode==AppConstantS.STORY_TO_GIFSHARE&&resultCode==AppConstantS.GIFSHARE_B_STORY) {
			System.out.println("GifShare B PicStory");
			//Ϊ��ʹ��ʾ�������ô˷���������PICEDIT-->SHAREʱ������100%���ȣ�����ʱ����������
			makeGifTimeDialog.setProgress(0);
			makeGifTimeDialog.dismiss();
		}
		
		
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
			siv.setImageBitmap(pic);
			picList.add(curGifNum, picLayout);
			
			// �������м�gifͼƬ�ķ���λ��
			LayoutParams params = new LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			picLayout.setLayoutParams(params);
			
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

			Button insertBtn = (Button) picLayout.findViewById(R.id.insertGif);
			insertBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent in = new Intent(StoryEditActivity.this, PicEditActivity.class);
					// �Ǵ��ĸ�ģ�鴫��GIF������
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					// ��ͼ�ϲ���ģʽ
					in.putExtra(AppConstantS.STORY_MODE, STORY_INSERT);
					// curgifnumΪ�����е�λ��
					// (0,1,2)��������2��(����1),curgifnumΪ1,����λ��Ϊ1.���ñ�Ϊ(0,������,1,2)
					curGifNum = picList.indexOf(picLayout);
					in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
					startActivityForResult(in, 100);
				}
			});

			picLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG, "�����λ��" + picList.indexOf(picLayout));
					Intent in = new Intent(StoryEditActivity.this, PicEditActivity.class);
					// �Ǵ��ĸ�ģ�鴫��GIF������
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					// �༭����ͼƬ�ķ�ʽ
					in.putExtra(AppConstantS.STORY_MODE, STORY_EDIT);
					// �༭��ǰ�����ͼƬ
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
				
				StaticLayout layout = new StaticLayout(Util.gmList.get(i).getBotWord(), tp, AppConstantS.FINAL_GIF_WIDTH, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
				canvas.translate(0, titleHeight + AppConstantS.FINAL_GIF_HEIGHT * (i+1)-layout.getHeight()-20);
				layout.draw(canvas);
				canvas.translate(0, -(titleHeight + AppConstantS.FINAL_GIF_HEIGHT * (i+1)-layout.getHeight()-20));
			}
			
			//���ϱ߿�
			Bitmap borderbitm = Util.getImageFromAssetFile(StoryEditActivity.this, "background", "bg_border.png");
			Rect dsRectborder = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i,
					backgroundbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i + backgroundbitm.getHeight());
			canvas.drawBitmap(borderbitm, null, dsRectborder, paint); 
		}
		Bitmap authorbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, "bg_author.png");
		Rect dstReckauthor = new Rect(0, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+88, AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+176);
		canvas.drawBitmap(authorbitm, null, dstReckauthor, paint);
		if (authorView.getText().toString() != null) {
			TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			tp.setFilterBitmap(true);
			tp.setColor(getResources().getColor(color.author_grey));
			tp.setTextSize(20);			
			tp.setTextAlign(Align.LEFT);
			canvas.drawText(authorView.getText().toString(), 10, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+88+30, tp);
		}
		Log.v(TAG, "�����ϳɵ�n�ţ�" + frame);		
		return drawBit;
	}
	
	JpgToGif j2g;
    public void picsToGif() {
    	Bitmap[] bits = new Bitmap[AppConstantS.GIF_FRAMECOUNT];
		for (int i = 0; i < bits.length; i++) {
			bits[i] = matchPic(Util.head, Util.body[i],i);;
		}	
		if(j2g==null) {
			j2g = new JpgToGif(this.handler);
		}
		j2g.startJpgToGif();
		//���·��
		String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
		try {
			j2g.jpgToGif(bits, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//����
		for (int i = 0; i < bits.length; i++) {
			bits[i].recycle();
		}
	}
   
    class MakeGifTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			//ֹͣ�첽�߳�
			j2g.stopJpgToGif();
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.dismiss();
				//�ָ��Ҽ�����
				if(!topbar_btnRight.isClickable()) {
					topbar_btnRight.setClickable(true);
				}
			}
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
		curGifNum = 1;
		if(picList!=null) {
			picList.clear();
		}
		if(mainLayout!=null) {
			mainLayout.removeAllViews();
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
			super.handleMessage(msg);
			switch (msg.what) {
				case MATCH_A_FRAME:
					//����������1,2,3,4,5,6,7,8��9)
					//������;�ж�ʱ��1,2,3,4,5. ��count=0,Ȼ����뵽����,setprogressΪ0��Ȼ��count+1,�ص���ʼ״̬
					makeGifTimeDialog.setProgress(count*11);
					count++;
					//����ת���������Ա��������ʾ��׼ȷ
					if(count==(AppConstantS.GIF_FRAMECOUNT+1)) {
						count=1;
						makeGifTimeDialog.setProgress(100);
						Intent in = new Intent(StoryEditActivity.this,PicShareActivity.class);
						in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
						startActivityForResult(in, AppConstantS.STORY_TO_GIFSHARE);
					}
				break; 
		    }  
			super.handleMessage(msg);      
		}
	};
}
