package com.dream.anstory.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import android.R.integer;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class Util {
	public static int curShowingHead;
	public static int curShowingBody;
	public static Bitmap[] head = new Bitmap[AppConstantS.GIF_FRAMECOUNT];
	public static Bitmap[] body = new Bitmap[AppConstantS.GIF_FRAMECOUNT];
	public static Bitmap background;
	public static Bitmap defaultbg;

	//���ͼͼƬ���ݵ��б�
	public static ArrayList<GifModel> gmList =new ArrayList<GifModel>();
	//�Ƿ��һ������GIF�����ڸ��������Ϣ
	public static boolean isFirstEdit = true;
	//��¼gifͼ�����
	public static String gifEditWordStr = null;
	
	private static String mPhotoPath;//���պ���Ƭ�ļ����·��
	WindowManager wm;
	public static int bodyNumber= 0;
	public static final String TAG = "ShowGif.Util";
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static byte[] getHtmlByteArray(final String url) {
		 URL htmlUrl = null;     
		 InputStream inStream = null;     
		 try {         
			 htmlUrl = new URL(url);         
			 URLConnection connection = htmlUrl.openConnection();         
			 HttpURLConnection httpConnection = (HttpURLConnection)connection;         
			 int responseCode = httpConnection.getResponseCode();         
			 if(responseCode == HttpURLConnection.HTTP_OK){             
				 inStream = httpConnection.getInputStream();         
			  }     
			 } catch (MalformedURLException e) {               
				 e.printStackTrace();     
			 } catch (IOException e) {              
				e.printStackTrace();    
		  } 
		byte[] data = inputStreamToByte(inStream);

		return data;
	}
	
	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.v(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if(offset <0){
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if(len <=0 ){
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if(offset + len > (int) file.length()){
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // ���������ļ���С������
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}
	
	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	/**
	 *  ��ȡӦ�ô����·��
	 */
	public static String getAppStorePath() {
		return Environment.getExternalStorageDirectory().toString() 
				+ File.separatorChar + AppConstantS.APP_STORE_FOLDERNAME;
	}
	
	 /**
     * ��Сbitmap��С
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
    //�ȼ����߱ȣ���w��h
    //�������320:350  ���֣���ȡ��h����Ϊh*320/350�� ��ȡ�������Ϊ(w-h*320/350)/2, �յ�Ϊ(w-h*320/350)/2 + h*320/350  ��
	//���С��320:350  ���ݣ�   ȡ��w ����Ϊw*350/320�� ��ȡ�������Ϊ(h-w*350/320)/2, �յ�Ϊ(h-w*350/320)/2 + w*350/320
    public static Bitmap cutOut320350(Bitmap bitmap, float w, float h) {
    	float scale = w/h;
    	Log.v(TAG,"ͼƬ�Զ����У�scale"+scale+"w:"+w+"h:"+h+"scaleV:"+320/350);
    	if(scale>(0.915)) {		
    		Log.v(TAG,"ͼƬ�Զ������֣�w"+bitmap.getWidth()+"h"+bitmap.getHeight()+"w"+w+"h"+h);
    		bitmap = Bitmap.createBitmap(bitmap, (int)((w-h*320/350)/2), 0, (int) (h*320/350), (int)h);
    		Log.v(TAG,"ͼƬ�Զ������֣�w"+bitmap.getWidth()+"h"+bitmap.getHeight());
    	} else if(scale<(0.913)) {
    		bitmap = Bitmap.createBitmap(bitmap, 0, (int)((h-w*350/320)/2), (int)w, (int)(w*350/320));
    		Log.v(TAG,"ͼƬ�Զ������ݣ�w"+bitmap.getWidth()+"h"+bitmap.getHeight());
    	} else {
    		Log.v(TAG,"ͼƬ�Զ����в��ֲ��ݣ�w"+bitmap.getWidth()+"h"+bitmap.getHeight());
    		return bitmap;
    	}
    	return bitmap;
    }
    /**
     * ��ȡͷ�������ص���ı���
     */
    public static float getHeadAndBodyScaleAfterOverLap() {
    	float f = ((float)(AppConstantS.headOriHeightPx-AppConstantS.headAndBodyOverLap))/
    					((float)(AppConstantS.bodyOriHeightPx-AppConstantS.headAndBodyOverLap));
    	return f;
    }
    
    public static InputStream getInputStreamFromAsset(Context ctx,String foldName,String fileName) {
		AssetManager am = ctx.getAssets();
		InputStream is = null;
		try {
			Log.v("123",foldName + File.separator + fileName);
			 is = am.open(foldName + File.separator + fileName);
		} catch(Exception e){
			e.printStackTrace();
		}
		return is;
    }
    
    /**
     * ��assets�ļ��л���ļ�
     */
	public static Bitmap getImageFromAssetFile(Context ctx,String foldName,String fileName) {
		Bitmap image = null;
		AssetManager am = ctx.getAssets();
		InputStream is = null;
		try {
			Log.v("123",foldName + File.separator + fileName);
			 is = am.open(foldName + File.separator + fileName);
			 image = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			System.out.println("����");
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				System.out.println("����");
				e.printStackTrace();
			}
		}
		return image;
	}
	
	public static Bitmap convertViewToBitmap(View view){
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
	}

	
	
	 /**
     * ��ʱ���������Ƭ����
     * @return
     */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		Log.v(TAG, "����ʱ�����Ƭ��"+ dateFormat.format(date) + ".jpg");
		return dateFormat.format(date) + ".jpg";
	}
	
	
	//�������ͼƬĬ�ϽǶ�
	public static int calPicAngle(String mPhotoPath) {
		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(mPhotoPath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			// ��ȡͼƬ�����������Ϣ
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			// ������ת�Ƕ�
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break; 
			default:
				digree = 0;
				break;
			}
		}
		
		return digree;
	}
	
	public static Bitmap zoomAndResetPic() {
		return background;
		 //���ֻ��Դ�������ķֱ��ʽ���ѹ��
       
	}
	
}
