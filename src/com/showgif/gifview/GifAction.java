package com.showgif.gifview;

public interface GifAction {
	
	/**绗竴璐磋В鐮佹垚鍔�/
	 * 
	 */
	
	public static final int RETURN_FIRST = 1;
	/**鎵�湁瑙ｇ爜鎴愬姛*/
	public static final int RETURN_FINISH = 2;
	/**缂村瓨瑙ｇ爜鎴愬姛*/
	public static final int RETURN_CACHE_FINISH = 3;
	/**瑙ｇ爜澶辫触*/
	public static final int RETURN_ERROR = 4;
	
	/**
	 * 鍔ㄧ敾瑙ｇ爜缁撴灉	
	 * @param iResult 缁撴灉
	 */
	public void parseReturn(int iResult);
	
	/**
	 * @hide
	 * gif鍔ㄧ敾鏄惁缁撴潫浜嗕竴杞殑鏄剧ず锛屾瘡涓�疆缁撴潫锛岄兘浼氭湁鏈簨浠惰Е鍙�
	 */
	public void loopEnd();
}
