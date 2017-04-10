package net.guanjiale.lmq.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.model.cjx.component.TouchImageView;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.bean.ServerBean;

import java.util.ArrayList;


public class ImageRepeatPagerAdapter extends PagerAdapter {
    private View[] views;
    private int photoCount = 0;
    private ArrayList<ServerBean> photos;
    private Activity context;
    private View first, last;

    public ImageRepeatPagerAdapter(Activity context, View first, View last, View[] l, ArrayList<ServerBean> photos) {
        this.context = context;
        this.photoCount = photos == null ? 0 : photos.size();
        this.photos = photos;
        views = l;
        this.first = first;
        this.last = last;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v;
        if (photoCount == 4) {
            if (position == 2) {
                v = last;
            } else if (position == 3) {
                v = first;
            } else {
                v = views[position];
            }
        } else {
            if (position == 0 || position == photoCount - 2) {
                v = last;
            } else if (position == 1 || position == photoCount - 1) {
                v = first;
            } else {
                v = views[(position - 2) % 3];
            }
        }

        container.removeView(v);
        TouchImageView image = (TouchImageView) v;
        container.addView(v);
        ServerBean ib = photos.get(position);
        Tools.setImage(context, image, ib.image);
        return v;
    }

    @Override
    public int getCount() {
        return photoCount;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public void notifyDataSetChanged(View firstView, View lastView, View[] views, ArrayList<ServerBean> photos) {
        this.first = firstView;
        this.last = lastView;
        this.views = views;
        this.photoCount = photos == null ? 0 : photos.size();
        this.photos = photos;
        notifyDataSetChanged();
    }
}
