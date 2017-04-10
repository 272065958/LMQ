package net.guanjiale.lmq.server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseListActivity;
import com.model.cjx.adapter.BaseClassAdapter;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.ImageBean;
import net.guanjiale.lmq.bean.ServerChildBean;
import net.guanjiale.lmq.bean.ServerTypeBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/21.
 * 服务的二级页面
 */
public class ServerSelectActivity extends BaseListActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolBar(R.drawable.back_icon, null, getIntent().getAction());
        setListViweDivider(ContextCompat.getDrawable(this, R.color.cjx_background_color),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        loadData();
        registerReceiver(new IntentFilter(CustomApplication.ACTION_ORDER_CREATE));
    }

    // 初始化界面
    @Override
    protected void onCreateView() {
        setContentView(R.layout.activity_server_select);
        initListView();
        loadData();
    }

    // 收到广播回调
    @Override
    protected void onBroadcastReceive(Intent intent) {
        finish();
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList list) {
        return new MyServerAdapter(list, this);
    }

    @Override
    protected void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response;
            }

            @Override
            public void success(Object result) {
                ResponseBean response = (ResponseBean) result;
                hideLoadView();
                try {
                    JSONArray array = new JSONArray(response.datum);
                    JsonParser parser = JsonParser.getInstance();
                    ImageBean ib = parser.fromJson(array.getString(0), ImageBean.class);
                    showImage(ib);
                    Type type = new TypeToken<ArrayList<ServerTypeBean>>() {
                    }.getType();
                    ArrayList<ServerTypeBean> list = parser.fromJson(array.getString(1), type);
                    onLoadResult(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                hideLoadView();
            }
        };
        Intent intent = getIntent();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/thirdIcon", "key", intent.getStringExtra("key"),
                "type", intent.getStringExtra("type"));
    }

    private void showImage(ImageBean ib) {
        ImageView adverImage = (ImageView) findViewById(R.id.image_view);
        int screenWidth = MyApplication.getInstance().getScreen_width();
        int height = (int) (screenWidth * 22 / 72f);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) adverImage.getLayoutParams();
        lp.height = height;
        Tools.setImage(this, adverImage, ib.image);
    }

    class MyServerAdapter extends BaseClassAdapter {
        MyServerAdapter(ArrayList list, Activity context) {
            super(list, context, 2, CustomApplication.getInstance().getScreen_width(), true);
        }

        @Override
        protected ParentViewHolder bindViewHolder(View view) {
            return new MyParentViewHolder(view);
        }

        @Override
        protected void bindData(int position, ParentViewHolder holder) {
            ServerTypeBean si = (ServerTypeBean) getItem(position);
            MyParentViewHolder ho = (MyParentViewHolder) holder;
            ho.titleView.setText(si.name);
        }

        @Override
        protected ArrayList getItemList(int position) {
            return ((ServerTypeBean) getItem(position)).list;
        }

        @Override
        protected View createItemView(Context context) {
            return View.inflate(context, R.layout.item_server_select, null);
        }

        @Override
        protected void bindItemData(int position, Object obj, ItemViewHolder holder) {
            ServerChildBean scb = (ServerChildBean) obj;
            MyItemViewHolder ho = (MyItemViewHolder) holder;
            Tools.setImage(context, ho.imageView, scb.image);
            ho.titleView.setText(scb.name);
            ho.descView.setText(scb.desc);
            ho.getItemView().setTag(scb);
        }

        @Override
        protected ItemViewHolder bindItemViewHolder(View v) {
            return new MyItemViewHolder(v);
        }

        @Override
        public void onClick(View v) {
            ServerChildBean scb = (ServerChildBean) v.getTag();
            Intent intent = new Intent(ServerSelectActivity.this, ServerDetailActivity.class);
            intent.setAction(scb.name);
            intent.putExtra("key", scb.key);
            intent.putExtra("type", scb.type);
            startActivity(intent);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_server_select_content, null);
        }

        class MyParentViewHolder extends ParentViewHolder{
            TextView titleView;
            MyParentViewHolder(View v){
                super(v);
                titleView = (TextView) v.findViewById(R.id.title_view);
            }

            @Override
            protected LinearLayout getContentView(View view) {
                return (LinearLayout)view.findViewById(R.id.linear_layout);
            }
        }

        class MyItemViewHolder extends ItemViewHolder {
            ImageView imageView;
            TextView titleView, descView;

            MyItemViewHolder(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.image_view);
                titleView = (TextView) v.findViewById(R.id.title_view);
                descView = (TextView) v.findViewById(R.id.desc_view);
            }
        }
    }

//    class ServerAdapter extends MyBaseAdapter implements View.OnClickListener {
//        LinearLayout.LayoutParams leftParams, rightParams;
//        LinearLayout.LayoutParams lineParams;
//        LinearLayout.LayoutParams verLineParams;
//
//        public ServerAdapter(ArrayList<?> list, BaseActivity context) {
//            super(list, context);
//            OmengoApplication app = (OmengoApplication) context.getApplication();
//            int viewSpace = getResources().getDimensionPixelOffset(R.dimen.view_space);
//            int width = (app.getScreen_width() - viewSpace) / 2;
//            leftParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            rightParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewSpace);
//            verLineParams = new LinearLayout.LayoutParams(viewSpace, ViewGroup.LayoutParams.MATCH_PARENT);
//        }
//
//        @Override
//        protected View createView(Context context) {
//            return View.inflate(context, R.layout.item_server_select_content, null);
//        }
//
//        @Override
//        protected MyViewHolder bindViewHolder(View view) {
//            return new ViewHolder(view);
//        }
//
//        @Override
//        protected void bindData(int position, MyViewHolder holder) {
//            ServerTypeBean si = (ServerTypeBean) getItem(position);
//            ViewHolder ho = (ViewHolder) holder;
//            initContentView(ho.itemContentView, si.list);
//            ho.titleView.setText(si.name);
//        }
//
//        class ViewHolder extends MyViewHolder {
//            TextView titleView;
//            LinearLayout itemContentView;
//
//            public ViewHolder(View v) {
//                super(v);
//                titleView = (TextView) v.findViewById(R.id.title_view);
//                itemContentView = (LinearLayout) v.findViewById(R.id.linear_layout);
//            }
//        }
//
//        @Override
//        public void onClick(View v) {
//            ServerChildBean scb = (ServerChildBean) v.getTag();
//            Intent intent = new Intent(ServerSelectActivity.this, ServerDetailActivity.class);
//            intent.setAction(scb.name);
//            intent.putExtra("key", scb.key);
//            intent.putExtra("type", scb.type);
//            startActivity(intent);
//        }
//
//        Stack<View> itemStack = new Stack<>();
//        Stack<View> lineStack = new Stack<>();
//        Stack<View> verLineStack = new Stack<>();
//        Stack<LinearLayout> contentStack = new Stack<>();
//
//        private void initContentView(LinearLayout contentView, ArrayList<ServerChildBean> list) {
//            int childCount = contentView.getChildCount();
//            for (int i = childCount - 1; i > -1; i--) {
//                View view = contentView.getChildAt(i);
//                contentView.removeView(view);
//                if (view instanceof LinearLayout) {
//                    if (view.isSelected()) {
//                        LinearLayout linearLayout = (LinearLayout) view;
//                        View itemView = linearLayout.getChildAt(2);
//                        itemStack.add(itemView);
//                        linearLayout.removeView(itemView);
//
//                        View verLineView = linearLayout.getChildAt(1);
//                        verLineStack.add(verLineView);
//                        linearLayout.removeView(verLineView);
//
//                        itemView = linearLayout.getChildAt(0);
//                        itemStack.add(itemView);
//                        linearLayout.removeView(itemView);
//
//                        contentStack.add(linearLayout);
//                    } else {
//                        itemStack.add(view);
//                    }
//                } else {
//                    lineStack.add(view);
//                }
//            }
//            if (list == null || list.isEmpty()) {
//                return;
//            }
//            int size = list.size();
//            int line = (int) Math.ceil(size / 2.0f);
//            for (int i = 0; i < line; i++) {
//                if (i > 0) {
//                    contentView.addView(getLineView(), lineParams);
//                }
//                int p = i * 2;
//                LinearLayout linearLayout = getContentView();
//                View v1 = getItemView(list.get(p));
//                View verLineView = getVerLineView();
//                linearLayout.addView(v1, leftParams);
//                linearLayout.addView(verLineView, verLineParams);
//                if (p < size - 1) {
//                    View v2 = getItemView(list.get(p + 1));
//                    linearLayout.addView(v2, rightParams);
//                }
//                contentView.addView(linearLayout);
//            }
//        }
//
//        private LinearLayout getContentView() {
//            if (contentStack.isEmpty()) {
//                LinearLayout linearLayout = new LinearLayout(context);
//                linearLayout.setBackgroundColor(Color.WHITE);
//                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                linearLayout.setSelected(true);
//                return linearLayout;
//            } else {
//                return contentStack.pop();
//            }
//        }
//
//        private View getItemView(ServerChildBean scb) {
//            View v;
//            ItemViewHolder holder;
//            if (itemStack.isEmpty()) {
//                v = View.inflate(context, R.layout.item_server_select, null);
//                holder = new ItemViewHolder();
//                holder.imageView = (ImageView) v.findViewById(R.id.image_view);
//                holder.titleView = (TextView) v.findViewById(R.id.title_view);
//                holder.descView = (TextView) v.findViewById(R.id.desc_view);
//                v.setTag(R.id.tag_viewholder, holder);
//                v.setOnClickListener(this);
//            } else {
//                v = itemStack.pop();
//                holder = (ItemViewHolder) v.getTag(R.id.tag_viewholder);
//            }
//            Tools.setImage(context, holder.imageView, scb.image);
//            holder.titleView.setText(scb.name);
//            holder.descView.setText(scb.desc);
//            v.setTag(scb);
//            return v;
//        }
//
//        private View getLineView() {
//            if (lineStack.isEmpty()) {
//                return View.inflate(context, R.layout.item_divider, null);
//            } else {
//                return lineStack.pop();
//            }
//        }
//
//        private View getVerLineView() {
//            if (verLineStack.isEmpty()) {
//                return View.inflate(context, R.layout.item_divider, null);
//            } else {
//                return verLineStack.pop();
//            }
//        }
//
//        class ItemViewHolder {
//            ImageView imageView;
//            TextView titleView, descView;
//        }
//    }

}
