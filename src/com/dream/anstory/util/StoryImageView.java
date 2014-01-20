package com.dream.anstory.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class StoryImageView extends ImageView  {
	Context context; //������
	int row;  //��ǰ����
	int col; //����
	Handler viewHandler;//��Ϣ���ݶ���
	Bitmap bitmap; //ͼƬ��Դ
	String shareUrl;//ԭͼ��ַ
	String shareInfo;
	int imgHeight;
	int imgWidth;
//	private List<LeafletInfo> CurrentHotList;
	private int count;
	boolean isChoosed=false;
	//������ͼƬ�����ҵ�ͼƬ
	boolean isHot;
	RelativeLayout myParent;
	

	public boolean isHot() {
		return isHot;
	}


	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}

	public StoryImageView getView() {
		return this;
	}


	public StoryImageView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		init();
	}

	public StoryImageView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		init();
	}

	public StoryImageView(Context c) {
		super(c);
		this.context = c;
		init();
	}

	private void init() {
//		setOnClickListener(this);
//		setOnLongClickListener(this);
	}

	
//	@Override
//	public void onClick(View v) {
		//Toast.makeText(context, CurrentHotList.get(0).title+CurrentHotList.get(1).title+CurrentHotList.get(2).title+CurrentHotList.get(3).title+count, Toast.LENGTH_LONG).show();
//	    if(LeafletHotActivity.isSendToHangupSms) {
//	    	 Intent data=new Intent();  
//	         data.putExtra("flyerID", CurrentHotList.get(count).flyerId);  
//	         data.putExtra("flyerImageUrl", CurrentHotList.get(count).imgUrl);  
//	         data.putExtra("flyerThumbnailUrl", CurrentHotList.get(count).thumbnailUrl);  
//	         data.putExtra("flyerShareUrl", CurrentHotList.get(count).shareUrl); 
//	         
//	    	LeafletHotActivity ac = (LeafletHotActivity)context;
//	    	ac.setResult(0, data);
//	    	
//	    	ac.finish();
//	    } else if(LeafletHotActivity.isSendLeafToCus) {
			//��������ͼƬ���ǻƿ�򽫵����ͼƬ���ϻƿ���ı����ԣ����뵽choosedImg��
//			if(isChoosed==false) {
//				//������ѡ��ͼƬ������ա��Ѿ�ѡ�е�ͼƬ�����ı���״̬�ͱ�������Ϊֻ��ѡһ���� 
//				if(LeafletHotActivity.choosedImg !=null) {
//					LeafletHotActivity.choosedImg.isChoosed =false;
//					LeafletHotActivity.choosedImg.myParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.leaflet_gridview_item_bg));
//					LeafletHotActivity.choosedImg = null;
//				}
//				//����ǰ�����ͼƬ�һƿ�ı�������
//				isChoosed=true;
//				myParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.leaflet_choose_bg));	
//				LeafletHotActivity.choosedImg = this;
//			} else {
//				//��������ͼƬ���ǻƿȡ���ƿ�ı����ԣ����choosedImg.�ָ���û�лƿ�
//				isChoosed=false;
//				LeafletHotActivity.choosedImg.isChoosed =false;
//				LeafletHotActivity.choosedImg.myParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.leaflet_gridview_item_bg));
//				LeafletHotActivity.choosedImg = null;
//			}
//		} else {
			//�Ӻ��
//			myParent.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.leaflet_gridview_item_bg_press));
//			LeafletHotActivity.choosedRedImg = this;
//			if(isHot) {
//				LeafletFragment.isFromHot = true;
//				LeafletFragment.isFromMine =false;
//			} else {
//				LeafletFragment.isFromHot = false;
//				LeafletFragment.isFromMine = true;
//			}
//			Intent intent = new Intent(context,LeafletActivity.class);
//			intent.putExtra("item", count);
//			Bundle bundle = new Bundle();	
//			bundle.putInt("position",count);
//			bundle.putSerializable("info_list", (Serializable)CurrentHotList);
//			intent.putExtras(bundle);
//			context.startActivity(intent);
//		}
//	}
	

	public RelativeLayout getMyParent() {
		return myParent;
	}


	public void setMyParent(RelativeLayout myParent) {
		this.myParent = myParent;
	}


//	public List<LeafletInfo> getCurrentHotList() {
//		return CurrentHotList;
//	}

	
	public boolean isChoosed() {
		return isChoosed;
	}


	public void setChoosed(boolean isChoosed) {
		this.isChoosed = isChoosed;
	}

	
	public int getRow(){
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col)	{
		this.col = col;
	}
	
	

	public String getShareUrl() {
		return shareUrl;
	}


	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}


	public String getShareInfo() {
		return shareInfo;
	}


	public void setShareInfo(String shareInfo) {
		this.shareInfo = shareInfo;
	}


	public int getImgHeight() {
		return imgHeight;
	}


	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}


	public int getImgWidth() {
		return imgWidth;
	}


	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}



//	@Override
//	public boolean onLongClick(View v) {
//		// TODO Auto-generated method stub
//		return false;
//	}	
	
}
