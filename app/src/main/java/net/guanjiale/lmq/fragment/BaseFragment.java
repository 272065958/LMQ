package net.guanjiale.lmq.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.model.cjx.MyApplication;

import net.guanjiale.lmq.R;

/**
 * Created by cjx on 2016/9/6.
 */
public class BaseFragment extends Fragment {

    public View view;

    TextView toolbarTitle;
    Toolbar toolbar;

    /**
     * 设置toolbar
     *
     * @param hasBack      是否包含返回按钮
     * @param backListener 返回按钮监听
     * @param titleRes     标题的资源id
     */
    public void setToolBar(View view, boolean hasBack, View.OnClickListener backListener, int titleRes) {
        initToolbar(view, hasBack, backListener);
        if (toolbarTitle != null) {
            if (titleRes > 0){
                toolbarTitle.setText(titleRes);
            }
        }
    }

    public void setToolBar(View view, boolean hasBack, View.OnClickListener backListener, String titleRes) {
        initToolbar(view, hasBack, backListener);
        if (toolbarTitle != null) {
            toolbarTitle.setText(titleRes);
        }
    }

    private void initToolbar(View view, boolean hasBack, View.OnClickListener backListener){
        Activity a = getActivity();
        if(a == null){
            return ;
        }
        AppCompatActivity activity = null;
        if(a instanceof AppCompatActivity){
            activity = (AppCompatActivity)a;
        }
        if(activity == null){
            return ;
        }
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(MyApplication.getInstance().getToolbarBg());
        if (toolbar != null) {
            if (hasBack) {
                toolbar.setNavigationIcon(MyApplication.getInstance().getBackRes());
                activity.setSupportActionBar(toolbar);
                if (backListener == null) {
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                } else {
                    toolbar.setNavigationOnClickListener(backListener);
                }

            } else {
                activity.setSupportActionBar(toolbar);
            }
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
    }
}
