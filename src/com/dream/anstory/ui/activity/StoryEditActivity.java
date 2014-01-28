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
 * 逻辑需要修改
 * 故事的再退出时需要保存
 * 12/24 wwy
 */
public class StoryEditActivity extends Activity{
	public static String TAG = "com.dream.anstory.ui.activity.StoryEditActivity";
	public static final int STORY_EDIT = 1;
	public static final int STORY_BOT_ADD = 2;
	public static final int STORY_INSERT = 3;
	// 用于线程控制
	public static final int MATCH_A_FRAME = 6; //拼接完一帧图片，HandlerMsg

	public static final int ResNumFromPicAdd = 100;	//GIF添加
	public static final int ResNumFromPicEdit = 200;//GIF编辑
	public static final int ResNumFromCancel = 900; //GIF取消
	//初始的图片数 （只有标题）
	private int curGifNum = 0;
	//放所有小容器的主容器
	LinearLayout mainLayout;
	//存放子容器的对列
	private ArrayList<RelativeLayout> picList;
	//添加新图
	ImageView addNewGif;
	//生成新事
	Button makeNewStory;
	//动图生成时的读取进度
	ProgressDialog makeGifTimeDialog;
	//窗口管理器
	public WindowManager wm;
	//屏幕宽度高度
	public static int devWid;
	public static int devHei;
	//公用topbar的左按钮
	private Button topbar_btnLeft;
	//公用topbar的右按钮
	private Button topbar_btnRight;
	//标题t
	private TextView titleView;
	//作者t
	private TextView authorView;
	//作者文本
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
		//生成图片时要用到的进度条
		
		if(makeGifTimeDialog==null) {
			makeGifTimeDialog = (ProgressDialog)new ProgressDialog(StoryEditActivity.this);
			makeGifTimeDialog.setTitle("正在合成动画中");
			makeGifTimeDialog.setMessage("请稍候...");
			makeGifTimeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			makeGifTimeDialog.setCancelable(false);
			makeGifTimeDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					//此写法是为控制在取消之前,设置makegiftimedialog一些属性,如果直接后退取消不捕捉事件则无法修改.
					//停止拼接任务,
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
						//终止异步任务，终止jpg里的循环
						j2g.stopJpgToGif();
						mgTask.cancel(true);
						if(!topbar_btnRight.isClickable()) {
							topbar_btnRight.setClickable(true);
						}
						//回到第一帧计数，进度条归0，dialog关闭 
						count = 0;
						makeGifTimeDialog.setProgress(0);
						makeGifTimeDialog.dismiss();
					}
					return false;
				}
			});
		} 
		//获得主容器,用于放每个子容器（子容器装着每一张故事的预览图片）
		mainLayout = (LinearLayout)findViewById(R.id.storylayout);		
		//计算屏幕宽度
		wm =  (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		devWid = wm.getDefaultDisplay().getWidth();
	}
	
	private void initBtn() {
		// 初始化topbar左边按钮
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearData();
			}
		});
	
		//初始化topbar右边按钮
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!picList.isEmpty()) {
					topbar_btnRight.setClickable(false);
					mgTask = new MakeGifTask();
					mgTask.execute();
				} else {
					Toast.makeText(StoryEditActivity.this, "您还没有添加动画哦...", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		//添加标题事件
		titleView = (TextView) this.findViewById(R.id.titleview);
		android.widget.LinearLayout.LayoutParams paramsTitleView = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, LayoutParams.WRAP_CONTENT);
		paramsTitleView.setMargins(0, devWid/20, 0, 0);
		//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
		titleView.setLayoutParams(paramsTitleView);
		titleView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("给你的故事一个标题");
				//自动使用上次输入的内容
				if(titleView!=null) {
					gifEditWord.setText(titleView.getText().toString());
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("添加故事标题(不超过九个字符)")
				.setView(addWordDig)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//刷新显示gif_showword的ui
						if (gifEditWord.getText().toString()!=null&&gifEditWord.getText().toString().length()!=0){
							titleView.setText(gifEditWord.getText().toString());
						}else{
							titleView.setText("我的故事");
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
				aDlg.show();
			}
		});
		//添加作者事件
		authorView = (TextView) this.findViewById(R.id.authorview);
		android.widget.LinearLayout.LayoutParams paramsAuthorView = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH/4);
		//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
		authorView.setLayoutParams(paramsAuthorView);
		authorView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("输入你的大名");
				//自动使用上次输入的内容
				if(tempText!=null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this)
				.setTitle("添加创作者(不超过九个字符)")
				.setView(addWordDig)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//刷新显示gif_showword的ui						
						tempText = gifEditWord.getText().toString();
						if (tempText!=null&&tempText.length()!=0){
							authorView.setText("创作者："+tempText);
						}else{
							authorView.setText("创作者：匿名");
						}					
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
				//是从哪个模块传入GIF制作的
				in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
				in.putExtra(AppConstantS.STORY_MODE, STORY_BOT_ADD);
				// util.gmlist.size永远比队列最后一位坐标大1，所以curgifnum为对尾之后的那个坐标
				curGifNum = Util.gmList.size();
				System.out.println("curgifL:"+curGifNum);
				in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
				//是添加还是编辑模式
				startActivityForResult(in, 100);
			}
		});
	}
	
	//初始化故事对话框
	private void initDialog() {
		// 一上来添加标题
		final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
		final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
		gifEditWord.setHint("给你的故事一个标题");
		// 自动使用上次输入的内容
		if (titleView != null) {
			gifEditWord.setText(titleView.getText().toString());
		}
		AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("添加故事标题(不超过九个字符)").setView(addWordDig).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 刷新显示gif_showword的ui
				if (gifEditWord.getText().toString() != null && gifEditWord.getText().toString().length() != 0) {
					titleView.setText(gifEditWord.getText().toString());
				} else {
					titleView.setText("我的故事");
				}
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("输入你的大名");
				// gifEditWord.requestFocus();
				// 自动使用上次输入的内容
				if (tempText != null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("添加创作者(不超过九个字符)").setView(addWordDig).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 刷新显示gif_showword的ui
						tempText = gifEditWord.getText().toString();
						if (tempText != null && tempText.length() != 0) {
							authorView.setText("创作者：" + tempText);
						} else {
							authorView.setText("创作者：匿名");
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("创作者：匿名");
					}
				}).create();
				aDlg.show();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				titleView.setText("我的故事");
				// Toast.makeText(PicEditActivity.this, "dialogcancel",
				// Toast.LENGTH_LONG).show();
				final View addWordDig = getLayoutInflater().inflate(R.layout.addword_dialog, null);
				final EditText gifEditWord = (EditText) addWordDig.findViewById(R.id.gif_editword);
				gifEditWord.setHint("输入你的大名");
				gifEditWord.requestFocus();
				// 自动使用上次输入的内容
				if (tempText != null) {
					gifEditWord.setText(tempText);
				}
				AlertDialog aDlg = new AlertDialog.Builder(StoryEditActivity.this).setTitle("添加创作者(不超过九个字符)").setView(addWordDig).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 刷新显示gif_showword的ui
						tempText = gifEditWord.getText().toString();
						if (tempText != null && tempText.length() != 0) {
							authorView.setText("创作者：" + tempText);
						} else {
							authorView.setText("创作者：匿名");
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						authorView.setText("创作者：匿名");
					}
				}).create();
				aDlg.show();
			}
		}).create();
		aDlg.show();
	}

	//制作单张GIF完毕返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 可以根据多个请求代码来作相应的操作
		if(requestCode==AppConstantS.STORY_TO_GIFSHARE&&resultCode==AppConstantS.GIFSHARE_B_STORY) {
			System.out.println("GifShare B PicStory");
			//为了使显示完美，用此方法，当从PICEDIT-->SHARE时，保留100%进度，回来时先设再消。
			makeGifTimeDialog.setProgress(0);
			makeGifTimeDialog.dismiss();
		}
		
		
		if (requestCode == ResNumFromPicAdd && resultCode != 0) {
			//改变添加按钮大小
			android.widget.LinearLayout.LayoutParams paramsAddNewGif = new android.widget.LinearLayout.LayoutParams(devWid-devWid/10, (devWid-devWid/10) * AppConstantS.FINAL_GIF_HEIGHT / AppConstantS.FINAL_GIF_WIDTH/4);
			//params.addRule(LinearLayout.CENTER_IN_PARENT, LineareLayout.TRUE);
			addNewGif.setBackgroundResource(R.drawable.storyedit_btn_addgif_sel);
			addNewGif.setLayoutParams(paramsAddNewGif);
			//
			Log.v(TAG, "添加" + requestCode + resultCode + curGifNum);
			Bitmap pic = Util.gmList.get(curGifNum).getBitmap();
			//放每一张图片的子容器
			final RelativeLayout picLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.hot_item, null);
			ImageView siv = (ImageView) picLayout.findViewById(R.id.hotImage);
			siv.setImageBitmap(pic);
			picList.add(curGifNum, picLayout);
			
			// 灵活计算中间gif图片的放置位置
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
					// 是从哪个模块传入GIF制作的
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					// 本图上插入模式
					in.putExtra(AppConstantS.STORY_MODE, STORY_INSERT);
					// curgifnum为队列中的位置
					// (0,1,2)假设点击第2个(数字1),curgifnum为1,插入位置为1.正好变为(0,插入数,1,2)
					curGifNum = picList.indexOf(picLayout);
					in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
					startActivityForResult(in, 100);
				}
			});

			picLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG, "点击的位置" + picList.indexOf(picLayout));
					Intent in = new Intent(StoryEditActivity.this, PicEditActivity.class);
					// 是从哪个模块传入GIF制作的
					in.putExtra(AppConstantS.FROM_ACTIVITY_NAME, StoryEditActivity.this.getClass().getName());
					// 编辑单张图片的方式
					in.putExtra(AppConstantS.STORY_MODE, STORY_EDIT);
					// 编辑当前坐标的图片
					curGifNum = picList.indexOf(picLayout);
					in.putExtra(AppConstantS.STORY_GIF_NUM, curGifNum);
					// 是添加还是编辑模式
					// in.putExtra(AppConstantS.STORYMODE_PIC_NUM, value);
					startActivityForResult(in, 200);
				}
			});
		}
		if (requestCode == ResNumFromPicEdit && resultCode != 0) {
			Log.v(TAG, "编辑" + requestCode + resultCode + curGifNum);
			Bitmap pic = Util.gmList.get(curGifNum).getBitmap();
			// picLayout = (RelativeLayout)
			// getLayoutInflater().inflate(R.layout.hot_item, null);
			final RelativeLayout picLayout = (RelativeLayout) mainLayout.getChildAt(curGifNum+1);
			ImageView iv = (ImageView) picLayout.findViewById(R.id.hotImage);
			iv.setImageBitmap(pic);
			Log.v(TAG, "编辑" + requestCode + resultCode + curGifNum);
		}
		
		scrollView = (ScrollView)findViewById(R.id.scrollview);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(scrollView.FOCUS_DOWN);
			}
		});
	}
	
    //生成最终故事GIF图片
	Bitmap matchPic(Bitmap head, Bitmap body, int frame) {
		Bitmap drawBit = Bitmap.createBitmap(AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+176, Bitmap.Config.RGB_565);		
		Log.v(TAG, "开始合成第n张1：" + frame + drawBit.getRowBytes() * drawBit.getHeight());
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();		
		// 用来对位图进行滤波处理
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
			//控制出边界
			Bitmap maskBit = Bitmap.createBitmap(AppConstantS.FINAL_GIF_WIDTH, AppConstantS.FINAL_GIF_HEIGHT, Bitmap.Config.ARGB_8888);
			Canvas canvasmask = new Canvas(maskBit);
			Paint paintmask = new Paint();
			
			// 画上背景
			Bitmap backgroundbitm;
			if (Util.gmList.get(i).getBgBitmap() == null) {
				backgroundbitm = Util.getImageFromAssetFile(this, AppConstantS.GIFBG_FOLDERNAME, AppConstantS.GIFBG_FILENAME);
			} else {
				backgroundbitm = Util.gmList.get(i).getBgBitmap();
			}
			Rect dsRect = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i,
					backgroundbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i + backgroundbitm.getHeight());
			canvas.drawBitmap(backgroundbitm, null, dsRect, paint);		
		
			// 获取缩放与位移变量
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
			
			//从文件夹中找出指定的头部
			Bitmap headbitm = Util.getImageFromAssetFile(this, AppConstantS.HEADNAME
					, AppConstantS.HEADNAME + Util.gmList.get(i).getHead() + AppConstantS.PNG_ENDNAME);
			//根据偏移缩放量画上头部			
			canvasmask.drawBitmap(headbitm, srcRectmask, dstRectmask, paintmask);
			//从文件夹中找出指定的身子（当前帧数+1)
			Bitmap bodybitm = Util.getImageFromAssetFile(StoryEditActivity.this, 
					AppConstantS.BODYNAME + Util.gmList.get(i).getBody(), "color" + (frame + 1) + ".png");
			//根据偏移缩放量画上身子
			canvasmask.drawBitmap(bodybitm, srcRectmask, dstRectmask, paintmask);
			Rect dstRect = new Rect(0, titleHeight+AppConstantS.FINAL_GIF_HEIGHT * i, headbitm.getWidth(), titleHeight+AppConstantS.FINAL_GIF_HEIGHT * (i+1));
			Rect srcRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
			canvas.drawBitmap(maskBit, srcRect, dstRect, paint);
			//释放内存
			maskBit.recycle();
			maskBit = null;
			// 文字部分
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
			
			//画上边框
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
		Log.v(TAG, "结束合成第n张：" + frame);		
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
		//存放路径
		String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
		try {
			j2g.jpgToGif(bits, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//销毁
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
			Log.v(TAG, "开始执行异步任务jpg2gif，gif生成中");
			try {
				picsToGif();
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "jpg2gif失败");
			}
			return result;
		}
		
	
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//恢复右键功能
			if(!topbar_btnRight.isClickable()) {
				topbar_btnRight.setClickable(true);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			//停止异步线程
			j2g.stopJpgToGif();
			if(makeGifTimeDialog!=null) {			
				makeGifTimeDialog.setProgress(0);
				makeGifTimeDialog.dismiss();
				//恢复右键功能
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
    	//暂时定为：退出时清空所保存数据
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
		//跳转回首页
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
					//（正常进度1,2,3,4,5,6,7,8，9)
					//（当中途中段时，1,2,3,4,5. 先count=0,然后进入到这里,setprogress为0，然后count+1,回到初始状态
					makeGifTimeDialog.setProgress(count*11);
					count++;
					//把跳转放在这里以便进度条显示更准确
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
