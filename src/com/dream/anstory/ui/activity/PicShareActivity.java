package com.dream.anstory.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.dream.anstory.util.AccessTokenKeeper;
import com.dream.anstory.util.AppConstantS;
import com.dream.anstory.util.Util;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.UploadPhotoParam;
import com.showgif.gifview.GifImageType;
import com.showgif.gifview.GifView;
import com.dream.anstory.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

/**
 * ����ҳ
 */
public class PicShareActivity extends Activity {
	public static String TAG = "com.showgif.PicShareAcitivity";
	private static final String APP_ID = "242579";
	private static final String API_KEY = "a76374f3272d4379a09f17d4ad493caa";
	private static final String SECRET_KEY = "482d77e9f54049e597b06def584fc1e1";
	
	//���ݴ�������
	private Bundle bundle;
	//���Թ���ģʽ�����Ա༭ģʽ
	private boolean isFromStory; 
	private boolean isFromEdit;
	// ΢������ͼ
	private static final int THUMB_SIZE = 120;
	private IWXAPI mmAPI;
	private RennClient rennClient;
	public static Oauth2AccessToken weiboAccessToken;
	SsoHandler mSsoHandler;
	private Weibo mWeibo;
	//�����ϴ�ʱ�õ�
	ProgressDialog mProgressDialog;
	//public Bundle bundle;
	//Ԥ��gif
	GifView showGif;
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundle = intent.getExtras();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picshare);
		bundle = getIntent().getExtras();
		if (bundle.getString(AppConstantS.FROM_ACTIVITY_NAME).equals(StoryEditActivity.class.getName())) {
			isFromStory = true;
		} else if(bundle.getString(AppConstantS.FROM_ACTIVITY_NAME).equals(StoryEditActivity.class.getName())) {
			isFromEdit = true;
		}
		// ����MAIN�Ĵ����
		TextView tv = (TextView) findViewById(R.id.topbar_titletxt);
		tv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.topbar_txt_share));
		// ��߰�ť
		Button btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PicShareActivity.this.finish();
			}
		});
		//����Ԥ��		
		WindowManager wm =  (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int devWid = wm.getDefaultDisplay().getWidth();//��Ļ���
		final String resultPath = Util.getAppStorePath() + File.separator + AppConstantS.GIF_STORENAME;
		showGif = (GifView) findViewById(R.id.picshare_showgif);
		showGif.setGifImage(resultPath);
		showGif.setGifImageType(GifImageType.COVER);
		showGif.setLoopAnimation();
		showGif.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				showGif.restartGifAnimation();
				return false;
			}
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(devWid-devWid/5,(devWid-devWid/5)*AppConstantS.FINAL_GIF_HEIGHT
				/AppConstantS.FINAL_GIF_WIDTH);
		//params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);		
		params.addRule(RelativeLayout.BELOW,R.id.picshare_topbar);
		params.setMargins(devWid/10, devWid/10, 0, 0);
		LinearLayout.LayoutParams paramsShowgif;
		if (Util.gmList.size()!=0){
			paramsShowgif = new LinearLayout.LayoutParams(devWid-devWid/5,(devWid-devWid/5)*(AppConstantS.FINAL_GIF_HEIGHT * Util.gmList.size()+176)
					/AppConstantS.FINAL_GIF_WIDTH);
		}else{
			paramsShowgif = new LinearLayout.LayoutParams(devWid-devWid/5,(devWid-devWid/5)*AppConstantS.FINAL_GIF_HEIGHT
					/AppConstantS.FINAL_GIF_WIDTH);
		}
		ScrollView scrollView = (ScrollView) this.findViewById(R.id.scrollview);
		showGif.setLayoutParams(paramsShowgif);
		scrollView.setLayoutParams(params);	
		
		weiboAccessToken = AccessTokenKeeper.readAccessToken(this);
		// ΢�ŷ���
		// ����id
		mmAPI = WXAPIFactory.createWXAPI(PicShareActivity.this,
				"wxf563365665427632", false);
		mmAPI.registerApp("wxf563365665427632");

		// ��ʽע��id
		// mmAPI = WXAPIFactory.createWXAPI(PicShareActivity.this,
		// "wxc905f64571ad9581 ", false);h
		// mmAPI.registerApp("wxc905f64571ad9581 ");
		Button btnShare2Weixin = (Button) findViewById(R.id.share2weixin);
		btnShare2Weixin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//�ȼ������״̬
				if(getNetWorkStatus()) { 
					MobclickAgent.onEvent(PicShareActivity.this, AppConstantS.UMENG_SHARE_TO_WEIXIN);
					if (showGif.animationRun){
						showGif.pauseGifAnimation();
						showGif.destroy();
					}
					final String path = Util.getAppStorePath() + File.separator + AppConstantS.GIF_STORENAME;
					WXEmojiObject emoji = new WXEmojiObject();
					emoji.emojiPath = path;
	
					WXMediaMessage msg = new WXMediaMessage(emoji);
					msg.title = "Emoji Title";
					msg.description = "Emoji Description";
	
					Bitmap bmp = BitmapFactory.decodeFile(path);
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
					bmp.recycle();
					msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("emoji");
					req.message = msg;
					req.scene = SendMessageToWX.Req.WXSceneSession;
					mmAPI.sendReq(req);
				}
			}
		});

		//����΢������
		Button btnShare2SinaWeibo = (Button) findViewById(R.id.share2sinaweibo);
		btnShare2SinaWeibo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//�ȼ������״̬
				if(getNetWorkStatus()) { 
					MobclickAgent.onEvent(PicShareActivity.this, AppConstantS.UMENG_SHARE_TO_WEIBO);
					if (showGif.animationRun){
						showGif.pauseGifAnimation();
						showGif.destroy();
					}
					mWeibo = Weibo.getInstance(AppConstantS.APP_KEY,
							AppConstantS.REDIRECT_URL);
					mSsoHandler = new SsoHandler(PicShareActivity.this, mWeibo);
					mSsoHandler.authorize(new AuthDialogListener());
					try {
						Class sso = Class
								.forName("com.weibo.sdk.android.sso.SsoHandler");
					} catch (ClassNotFoundException e) {
						// e.printStackTrace();
						Log.i(TAG,"com.weibo.sdk.android.sso.SsoHandler not found");
					}
				}
			}
		});
		
		Button btnShare2Renren = (Button) findViewById(R.id.share2renren);
		btnShare2Renren.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				//�ȼ������״̬
				if(getNetWorkStatus()) {
					MobclickAgent.onEvent(PicShareActivity.this, AppConstantS.UMENG_SHARE_TO_RENREN);
					if (showGif.animationRun){
						showGif.pauseGifAnimation();
						showGif.destroy();
					}
					
					if(rennClient==null) {
						rennClient = RennClient.getInstance(PicShareActivity.this);
					}
			
					if (!rennClient.isLogin()) {
						rennClient.init(APP_ID, API_KEY, SECRET_KEY);
						rennClient.setScope("read_user_blog read_user_photo read_user_status read_user_album "
								+ "read_user_comment read_user_share publish_blog publish_share "
								+ "send_notification photo_upload status_update create_album "
								+ "publish_comment publish_feed");
						rennClient.setTokenType("bearer");
					
						rennClient.setLoginListener(new LoginListener() {
							public void onLoginSuccess() {
								System.out.println("��֤�ɹ�");
								Toast.makeText(PicShareActivity.this, "��֤�ɹ�",
										Toast.LENGTH_SHORT).show();
								uploadPic2Renren();
							}
							public void onLoginCanceled() {
								Log.v(TAG,"�û���ȡ����֤");
							}
						});
						rennClient.login(PicShareActivity.this);
					} else {
						//ֱ�ӷ���ͼƬ
						uploadPic2Renren();
						Toast.makeText(PicShareActivity.this, "�Ѿ���֤",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	
	public void uploadPic2Renren() {
		UploadPhotoParam param = new UploadPhotoParam();
		
		try {
			String path = Util.getAppStorePath() +  File.separator+AppConstantS.GIF_STORENAME;
			File picFile = new File(path);
			param.setFile(picFile);
			param.setDescription("#��ͼ# http://115.28.4.190/");
		} catch (Exception e) {
			Log.v(TAG,"�����ϴ���Ƭʱ�������ô���"+e.getMessage());
		}
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(PicShareActivity.this);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setTitle("��ȴ�");
			mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
			mProgressDialog.setMessage("�����ϴ�gifͼ��������...");
			mProgressDialog.show();
		}
		try {
			rennClient.getRennService().sendAsynRequest(param, new CallBack() {
				public void onSuccess(RennResponse response) {
					//textView.setText(response.toString());
					Toast.makeText(PicShareActivity.this, "�ϴ���Ƭ�����˳ɹ�", Toast.LENGTH_SHORT).show();
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
				}

				public void onFailed(String errorCode, String errorMessage) {
					Toast.makeText(PicShareActivity.this, "�ϴ���Ƭ������ʧ��", Toast.LENGTH_SHORT).show();
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
				}
			});
		} catch (RennException e1) {
			e1.printStackTrace();
		}
	}
	
	
	// ����΢����¼����ʱ��
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	// ����΢����sso�ص�
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//sso��Ȩ�ص�
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	//����΢���ļ�����
	class AuthDialogListener implements WeiboAuthListener {
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			System.out.println("token" + token);
			String expires_in = values.getString("expires_in");
			System.out.println("EI" + expires_in);
			for (String key : values.keySet()) {
				System.out.println("values:key = " + key + " value = "
						+ values.getString(key));
			}
			weiboAccessToken = new Oauth2AccessToken(token, expires_in);
			if (weiboAccessToken.isSessionValid()) {
				System.out.println("��֤�ɹ�");
				String path =  Util.getAppStorePath() +  File.separator
						+ AppConstantS.GIF_STORENAME;
				Intent it = new Intent(PicShareActivity.this,
						SinaWeiboShareActivity.class);
				it.putExtra(SinaWeiboShareActivity.EXTRA_ACCESS_TOKEN,
						weiboAccessToken.getToken());
				it.putExtra(SinaWeiboShareActivity.EXTRA_EXPIRES_IN,
						weiboAccessToken.getExpiresTime());
				it.putExtra(SinaWeiboShareActivity.EXTRA_PIC_PATH_, path);
				startActivity(it);

				AccessTokenKeeper.keepAccessToken(PicShareActivity.this,
						weiboAccessToken);
			} else {
				System.out.println("��֤ʧ��");
			}
		}

		public void onError(WeiboDialogError e) {
			System.out.println("��֤����");
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		public void onCancel() {
			System.out.println("��֤ȡ��");
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		public void onWeiboException(WeiboException e) {
			System.out.println("��֤����");
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	private boolean getNetWorkStatus() {        
		boolean netSataus = false;       
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);    
		cwjManager.getActiveNetworkInfo();      
		if (cwjManager.getActiveNetworkInfo() != null) {         
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();      
		} 
		if (!netSataus) {
			Toast.makeText(PicShareActivity.this, "������������״̬", Toast.LENGTH_LONG).show();
		}
		return netSataus;   
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent in = new Intent(PicShareActivity.this,PicEditActivity.class);
			setResult(AppConstantS.GIFSHARE_B_EMOJ,in);
			PicShareActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
