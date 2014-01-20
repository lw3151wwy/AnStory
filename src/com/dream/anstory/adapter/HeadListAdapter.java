package com.dream.anstory.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dream.anstory.R;
import com.dream.anstory.ui.activity.PicEditActivity;
import com.dream.anstory.util.Util;
import com.showgif.gifview.GifView;

public class HeadListAdapter extends BaseAdapter{
	private Context context; // 运行上下文
	private List<HashMap<String, Object>> listItems; // 商品信息集合
	private LayoutInflater listContainer; // 视图容器
	private boolean[] hasChecked; // 记录商品选中状态
	private GifView gifBody;
	private ImageView gifHead;
	
	public HeadListAdapter(Context context, List<HashMap<String, Object>> listItems,
			ImageView gifHead,GifView gifBody) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		this.gifHead = gifHead;
		this.gifBody = gifBody;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return 16;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder; 
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.simpleitem, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) view.findViewById(R.id.img);
			holder.iv.setOnClickListener(new OnClickListener() {	
				public void onClick(View v) {
					gifHead.setImageBitmap(Util.getImageFromAssetFile(context,
							"gifbutton", "head"+ (position+1) +".png"));
				}
			});
			view.setTag(holder);
		} else {
			holder = (ViewHolder)view.getTag();
		}
		System.out.println("myposition"+getItemId(position));
		holder.iv.setImageBitmap((Bitmap)listItems.get(position).get("image"));
	    return view;
	}
	
	class ViewHolder {
    	ImageView iv;
    }
}
