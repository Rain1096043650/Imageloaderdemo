package com.rain.imageloaderdemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created  on 2017/5/10.
 *
 * @author Rain
 */
public class PhotoAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private Context mContext;
    public static String[] imageUrls;
    private ImageLoader mImageLoader;
    private int mStart;
    private int mEnd;
    private boolean isFirstEnter;

    public PhotoAdapter(Context context, String[] imageThumbUrls,ListView listView) {
        this.mContext = context;
        this.imageUrls = imageThumbUrls;
        mImageLoader = new ImageLoader(listView);
        isFirstEnter = true;
        //一定要注册该事件
        listView.setOnScrollListener(this);

    }


    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String imageUrl = imageUrls[position];
        View view;
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.photo_layout, null);
        } else {
            view = convertView;
        }
        ImageView photo = (ImageView) view.findViewById(R.id.photo);
        // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
        photo.setTag(imageUrl);
//        new ImageLoader().showImageFromUrl(photo,imageUrl);
        mImageLoader.showImageFromAsync(photo, imageUrl);
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //停止状态 加载可见项
            mImageLoader.loadBitmaps(mStart,mEnd);
        } else {
            //取消加载
            mImageLoader.cancleAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //第一次显示的时候调用
        if(isFirstEnter && visibleItemCount >0){
            mImageLoader.loadBitmaps(mStart,mEnd);
            isFirstEnter = false;
        }
    }
}
