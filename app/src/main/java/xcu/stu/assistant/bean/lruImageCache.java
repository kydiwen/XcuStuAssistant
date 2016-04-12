package xcu.stu.assistant.bean;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by 孙文权 on 2016/4/12.
 */
public class lruImageCache implements ImageLoader.ImageCache {
    private static LruCache<String, Bitmap> mMemoryCache;
    private static lruImageCache lruImageCache;

    //私有化构造方法
    private lruImageCache() {
        // Get the Max available memory
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    //单例模式获取
    public static lruImageCache instance() {
        if (lruImageCache == null) {
            lruImageCache = new lruImageCache();
        }
        return lruImageCache;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mMemoryCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (getBitmap(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
    }
}
