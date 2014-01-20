package com.dream.anstory.ui.activity;

import java.util.ArrayList;

import com.dream.anstory.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
 
public class GuideActivity extends Activity {
    public static final String SHAREDPREFERENCES_NAME = "my_pref";
    public static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    
	
    private ViewPager viewPager;
     
    /**装分页显示的view的数组*/
    private ArrayList<View> pageViews;    
    private TextView textView;
     
    /**将小圆点的图片用数组表示*/
    private TextView[] textViews;
     
    //包裹滑动图片的LinearLayout
    private ViewGroup viewPics;
     
    //包裹小圆点的LinearLayout
    private ViewGroup viewPoints;
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        //将要分页显示的View装入数组中
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.guidepage1, null));
        pageViews.add(inflater.inflate(R.layout.guidepage2, null));
        pageViews.add(inflater.inflate(R.layout.guidepage3, null));
//        pageViews.add(inflater.inflate(R.layout.guidepage3, null));
        pageViews.add(inflater.inflate(R.layout.guidepage4, null));
        pageViews.add(inflater.inflate(R.layout.guidepage5, null));
         
        //创建imageviews数组，大小是要显示的图片的数量
        textViews = new TextView[pageViews.size()];
        //从指定的XML文件加载视图
        viewPics = (ViewGroup) inflater.inflate(R.layout.guidegroup, null);
         
        //实例化小圆点的linearLayout和viewpager
        viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);
      
        //添加小圆点的图片
        for(int i=0;i<pageViews.size();i++){
            textView = new TextView(GuideActivity.this);
            //设置小圆点imageview的参数
            textView.setLayoutParams(new LayoutParams(50,75));//创建一个宽高均为20 的布局
            
            //imageView.setPadding(0, 0, 0, 0);
            //将小圆点layout添加到数组中
            textViews[i] = textView;
            //默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
            if(i==0){
                textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_pressed);
            }else{
            	textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_nor);
            }
             
            //将imageviews添加到小圆点视图组
            viewPoints.addView(textViews[i]);
            final int temp = i;
            viewPoints.getChildAt(i).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					viewPager.setCurrentItem(temp, true);
				}			
					 
			});
        }
         
        //显示滑动图片的视图
        setContentView(viewPics);
        //设置viewpager的适配器和监听事件

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());        
       
    }
     
    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //设置已经引导
            setGuided();
            //跳转
            Intent mIntent = new Intent();
//            mIntent.setClass(GuideActivity.this, PicEditActivity.class);
//            GuideActivity.this.startActivity(mIntent);
//            GuideActivity.this.finish();
            mIntent.setClass(GuideActivity.this,  MainActivity.class);
            GuideActivity.this.startActivity(mIntent);
            GuideActivity.this.finish();
            
        }
    }; 
     

    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }
     
     
    class GuidePageAdapter extends PagerAdapter{
    	
        //销毁position位置的界面
        @Override
        public void destroyItem(View v, int position, Object arg2) {

        	((ViewPager)v).removeView(pageViews.get(position));
        }
 
        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
        }
         
        //获取当前窗体界面数
        @Override
        public int getCount() {
            return pageViews.size();
        }
 
        //初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
        	System.out.println("p"+position);
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));  
             // 测试最后一页的按钮事件  
            if (position == textViews.length-1) {  
                Button btn = (Button) v.findViewById(R.id.btn_close_guide);  
                btn.setOnClickListener(Button_OnClickListener);  
            }  
            return pageViews.get(position);  
        }
 
        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }
        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }
 
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }
     
     
    class GuidePageChangeListener implements OnPageChangeListener{
 
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
 
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }
 
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<textViews.length;i++){
                textViews[position].setBackgroundResource(R.drawable.guideslip_tabbar_btn_pressed);
                //不是当前选中的page，其小圆点设置为未选中的状态
                if(position !=i){
                	textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_nor);
                }
            }
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
 } 

