package net.guanjiale.lmq.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.ServerBean;
import net.guanjiale.lmq.component.HomeHeaderView;
import net.guanjiale.lmq.component.ScrollAdverView;
import net.guanjiale.lmq.server.ServerListActivity;
import net.guanjiale.lmq.server.ServerSelectActivity;
import net.guanjiale.lmq.server.ShopListActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/20.
 * 首页碎片
 */
public class ServerFragment extends BaseFragment implements View.OnClickListener, ScrollAdverView.OnSingleTouchListener {

    ListView listView;
    View loadView, emptyView;
    HomeAdapter adapter;
    HomeHeaderView headerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_view, null);
            view.setBackgroundResource(R.color.cjx_divider_color);
            listView = (ListView) view.findViewById(R.id.list_view);
            headerView = new HomeHeaderView(getContext());
            headerView.bind(this, this);
            headerView.initViewSize(MyApplication.getInstance().getScreen_width());
            listView.addHeaderView(headerView);
            loadView = view.findViewById(R.id.loading_view);
            emptyView = view.findViewById(R.id.empty_view);
            loadCache();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        selectListener((ServerBean) v.getTag(R.string.main_server));
    }

    @Override
    public void onSingleTouch(ServerBean m) {
        selectListener(m);
    }

    private void selectListener(ServerBean sb) {
        if (sb == null) {
            return;
        }
        Intent intent = null;
        switch (sb.type) {
            case "1": // 进入二级菜单页面
            case "3": // 桶装水
                intent = new Intent(getContext(), ServerSelectActivity.class);
                break;
            case "2": // 日用商品
                intent = new Intent(getContext(), ShopListActivity.class);
                break;
            case "4": // 全部分类
                intent = new Intent(getContext(), ServerListActivity.class);
                break;
            case "13":
                intent = new Intent(getContext(), ServerSelectActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("key", sb.key);
            intent.putExtra("type", sb.type);
            intent.setAction(sb.name);
            getContext().startActivity(intent);
        }
    }

    private void loadCache() {
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.app_name),
                Activity.MODE_PRIVATE);
        if (sp.contains("server_data")) {
            String data = sp.getString("server_data", null);
            Type type = new TypeToken<ArrayList<ArrayList<ServerBean>>>() {
            }.getType();
            ArrayList<ArrayList<ServerBean>> mainList = JsonParser.getInstance().fromJson(data, type);
            displayData(mainList);
            loadData();
        } else {
            loadData();
        }
    }

    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {

            @Override
            public Object parser(ResponseBean response) {
                Type type = new TypeToken<ArrayList<ArrayList<ServerBean>>>() {
                }.getType();
                ArrayList<ArrayList<ServerBean>> mainList = JsonParser.getInstance().fromJson(response.datum, type);
                if(mainList != null && !mainList.isEmpty()){
                    SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.app_name),
                            Activity.MODE_PRIVATE);
                    sp.edit().putString("server_data", response.datum).apply();
                }
                return mainList;
            }

            @Override
            public void success(Object result) {
                hideLoadView();
                displayData((ArrayList)result);
            }

            @Override
            public void error() {
                hideLoadView();
            }
        };
        HttpUtils.getInstance().postEnqueue((BaseActivity) getActivity(), callbackInterface, "app/newIndex");
    }

    // 隐藏加载的view
    private void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
    }

    // 显示数据
    private void displayData(ArrayList<ArrayList<ServerBean>> mainList) {
        ArrayList<ServerBean> adverList;
        ArrayList<ServerBean> serverList;
        ArrayList<ServerBean> dataList;
        if (mainList != null) {
            adverList = mainList.get(0);
            serverList = mainList.get(1);
            dataList = mainList.get(2);
        } else {
            adverList = null;
            serverList = null;
            dataList = null;
        }
        BaseActivity activity = (BaseActivity) getActivity();
        headerView.setData(activity, adverList, serverList);
        if (adapter == null) {
            listView.requestFocus();
            listView.setDivider(null);
            listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.auto_margin));
            listView.setHeaderDividersEnabled(true);
            adapter = new HomeAdapter(dataList, activity, MyApplication.getInstance().getScreen_width());
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(dataList);
        }
    }

    class HomeAdapter extends MyBaseAdapter {
        int imageHeight; // 定义每个item的高度

        public HomeAdapter(ArrayList<ServerBean> list, BaseActivity context, int width) {
            super(list, context);
            imageHeight = (int) (23 * width / 75f);
        }

        @Override
        public View createView(Context context) {
            View view = View.inflate(context, R.layout.item_home, null);
            AbsListView.LayoutParams alp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight);
            view.setLayoutParams(alp);
            view.setOnClickListener(ServerFragment.this);
            return view;
        }

        @Override
        public void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            ServerBean ib = (ServerBean) getItem(position);
            Glide.with(context).load(ib.image).into(ho.imageView);
            ho.getView().setTag(R.string.main_server, ib);
        }

        @Override
        public MyViewHolder bindViewHolder(View v) {
            return new ViewHolder(v);
        }

        class ViewHolder extends MyViewHolder {
            ImageView imageView;

            public ViewHolder(View view) {
                super(view);
                imageView = (ImageView) view;
            }
        }
    }
}
