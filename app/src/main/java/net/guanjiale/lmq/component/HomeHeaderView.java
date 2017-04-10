package net.guanjiale.lmq.component;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.ServerBean;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Administrator on 2016/8/20.
 */
public class HomeHeaderView extends LinearLayout {

    ScrollAdverView adverView;
    ViewPager pagerView;
    ScrollAdverView.OnSingleTouchListener singleTouchListener;
    int itemWidth;

    public HomeHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public HomeHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        View.inflate(context, R.layout.main_header, this);

        adverView = (ScrollAdverView) findViewById(R.id.adver_scroll_view);
        pagerView = (ViewPager) findViewById(R.id.adver_view_pager);
        itemWidth = (int) (MyApplication.getInstance().getScreen_width() / 5f);
    }

    OnClickListener listener;
    public void bind(OnClickListener clickListener, ScrollAdverView.OnSingleTouchListener singleTouchListener){
        this.listener = clickListener;
        this.singleTouchListener = singleTouchListener;
    }

    public void initViewSize(int width) {
        int adverViewHeight = (int) (2 * width / 5f);
        LayoutParams lp = (LayoutParams) adverView.getLayoutParams();
        lp.height = adverViewHeight;
    }

    MyPagerAdapter pagerAdapter;
    private void initViewPager(View[] views) {
        final View view = views[0];
        int width = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        LayoutParams lp = (LayoutParams) pagerView.getLayoutParams();
        lp.height = view.getMeasuredHeight(); // 获取高度
        if(pagerAdapter == null){
            pagerAdapter = new MyPagerAdapter(views);
            pagerView.setAdapter(pagerAdapter);
        }else{
            pagerAdapter.notifyDataSetChanged(views);
        }
    }

    public void setData(BaseActivity activity, ArrayList<ServerBean> adverList,
                        ArrayList<ServerBean> serverList) {
        // 设置广告
        adverView.setData(activity, adverList);
        adverView.setOnSingleTouchListener(singleTouchListener);
        // 设置服务
        if (serverList != null) {
            // 清空现在的view
            if(pagerAdapter != null){
                View[] views = pagerAdapter.getViews();
                if(views != null){
                    for(View v : views){
                        LinearLayout viewGroup = (LinearLayout) v;
                        if(viewGroup.getOrientation() == LinearLayout.VERTICAL){
                            for(int i=1; i>-1; i--){
                                LinearLayout child = (LinearLayout) viewGroup.getChildAt(i);
                                clearLineView(child);
                                contentStack.add(child);
                                viewGroup.removeView(child);
                            }
                        }else{
                            clearLineView(viewGroup);
                        }
                    }
                }
            }

            int size = serverList.size();
            int page = (int) Math.ceil(size / 10.0f);
            View[] views = new View[page];
            for (int i = 0; i < page; i++) {
                ArrayList<ServerBean> list = new ArrayList<>();
                for (int j = 0; j < 10; j++) { // 每页显示10个icon
                    int p = i * 10 + j;
                    if (p < size) {
                        list.add(serverList.get(p));
                    } else {
                        break;
                    }
                }
                views[i] = initPageView(list);
            }
            initViewPager(views);
        }
    }

    LayoutParams itemLayoutParams;
    Stack<LinearLayout> contentStack = new Stack<>();
    Stack<View> itemStack = new Stack<>();

    private View initPageView(ArrayList<ServerBean> list) {
        LinearLayout linearLayout;
        if (itemLayoutParams == null) {
            itemLayoutParams = new LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int size = list.size();
        if (size > 5) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout child1 = getLineView();
            for (int i = 0; i < 5; i++) {
                View itemView = getItemView(list.get(i));
                itemView.setLayoutParams(itemLayoutParams);
                child1.addView(itemView);
            }
            linearLayout.addView(child1);

            LinearLayout child2 = getLineView();
            LayoutParams llp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            child2.setLayoutParams(llp);
            for (int i = 5; i < size; i++) {
                View itemView = getItemView(list.get(i));
                itemView.setLayoutParams(itemLayoutParams);
                child2.addView(itemView);
            }
            linearLayout.addView(child2);
        } else {
            linearLayout = getLineView();
            for (ServerBean g : list) {
                View itemView = getItemView(g);
                itemView.setLayoutParams(itemLayoutParams);
                linearLayout.addView(itemView);
            }
        }
        return linearLayout;
    }

    // 清除一个item行的容器
    private void clearLineView(LinearLayout linearLayout){
        int childCount = linearLayout.getChildCount();
        for(int i=childCount-1; i>-1; i--){
            View itemView = linearLayout.getChildAt(i);
            itemStack.add(itemView);
            linearLayout.removeView(itemView);
        }
    }

    // 添加一个item行的容器
    private LinearLayout getLineView() {
        if(contentStack.isEmpty()){
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            return linearLayout;
        }else{
            return contentStack.pop();
        }
    }

    // 添加一个item view
    private View getItemView(ServerBean gb) {
        View v;
        if(itemStack.isEmpty()){
            v = View.inflate(getContext(), R.layout.item_server, null);
            v.setOnClickListener(listener);
        }else{
            v = itemStack.pop();
        }
        ImageView iconView = (ImageView) v.findViewById(R.id.server_icon);
        TextView nameView = (TextView) v.findViewById(R.id.server_name);
        Tools.setImage((Activity) getContext(), iconView, gb.image);
        nameView.setText(gb.name);
        v.setTag(R.string.main_server, gb);
        return v;

    }

    class MyPagerAdapter extends PagerAdapter {
        View[] views;

        MyPagerAdapter(View[] views) {
            this.views = views;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views[position];
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        View[] getViews(){
            return views;
        }

        void notifyDataSetChanged(View[] views) {
            this.views = views;
            notifyDataSetChanged();
        }
    }

}
