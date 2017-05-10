package com.rain.imageloaderdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created  on 2017/5/10.
 *
 * @author Rain
 */

public class ImageLoader {

    private ImageView mImageView;
    private String mString;
    private LruCache<String, Bitmap> mCache;
    private ImageLoader mImageLoader;

    private ListView mListView;
    private Set<MyAsyncTask> taskCollection;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mString)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

//    public ImageLoader getInstance() {
//        if (mImageLoader == null) {
//            synchronized (ImageLoader.class) {
//                if (mImageLoader == null) {
//                    mImageLoader = new ImageLoader(mListView);
//                }
//            }
//        }
//        return mImageLoader;
//    }

    public ImageLoader() {
    }

    public ImageLoader(ListView listview) {
        mListView = listview;
        taskCollection = new HashSet<>();
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 4);
        Log.d("MYTAG", cacheSize + "================");
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes();
            }
        };
    }

    public void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (mCache.get(url) == null) {
            mCache.put(url, bitmap);
        }

    }

    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    public void showImageFromUrl(ImageView imageView, String str) {
        mImageView = imageView;
        mString = str;
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmapFromUrl(mString);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private Bitmap getBitmapFromUrl(String str) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(4000);
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            conn.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageFromAsync(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }

    }

    /**
     * 加载从start 到end的图片
     *
     * @param start
     * @param end
     */
    public void loadBitmaps(int start, int end) {

        for (int i = start; i < end; i++) {
            String url = PhotoAdapter.imageUrls[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                MyAsyncTask task = new MyAsyncTask(url);
                task.execute(url);
                taskCollection.add(task);
            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 取消所有等待下载或者正在下载的任务
     */
    public void cancleAllTask() {
        if (taskCollection != null) {
            for (MyAsyncTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {

        //        private  ImageView mImageviewas;
        private String murl;

        public MyAsyncTask(String url) {
//            mImageviewas = imageView;
            this.murl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("MYTAG", params[0]);
            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            addBitmapToMemoryCache(url, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d("MYTAG", "onPostExecute");
//            if(mImageviewas.getTag().equals(murl)){
            ImageView imageView = (ImageView) mListView.findViewWithTag(murl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
//            }

        }
    }
}
