package com.dream.anstory.wxapi;


import com.dream.anstory.ui.activity.PicEditActivity;
import com.dream.anstory.ui.activity.PicShareActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static final String TAG = "com.showgif.wxapi";
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private Button gotoBtn, regBtn, launchBtn, checkBtn;
	
	// IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        Log.v(TAG,"jinlaile");
    	api = WXAPIFactory.createWXAPI(this, "", false);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	//微信发送请求到第三方应用时，会回调到该方法
	public void onReq(BaseReq req) {
		// 微信分享
		// 测试id
		Log.v(TAG,"onReq");
		//注册微信ID
		System.out.println("isfromwx:" + PicEditActivity.isFromWX);
		PicEditActivity.isFromWX = true;
		
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				Intent in = new Intent();
			    in.setClass(this, PicEditActivity.class);
				in.putExtras(getIntent());
			    startActivity(in);
			    finish();
			    Log.v(TAG,"getmsg from wx");
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				System.out.println("COMMAND_SHOWMESSAGE_FROM_WX");	
				break;
			default:
				break;
		}
	}

	public void onResp(BaseResp arg0) {
	    this.finish();
		System.out.println("Onresp:" + PicEditActivity.isFromWX);
	    Log.v("com.showgif.WXEntryActivity","onResp");
	}
}