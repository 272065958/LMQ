package net.guanjiale.lmq.my;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.activity.BaseListActivity;
import com.model.cjx.adapter.BaseClassAdapter;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.OrderDetailBean;
import net.guanjiale.lmq.bean.OrderItemBean;
import net.guanjiale.lmq.util.OrderOperateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/14.
 */
public class OrderDetailActivity extends BaseListActivity implements View.OnClickListener {
    String orderId, orderAmount;
    OrderOperateUtil util;
    OrderOperateUtil.CancelInterface cancelInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolBar(true, null, R.string.shop_pay_detail);
        loadData();
    }

    // 初始化界面
    @Override
    protected void onCreateView(){
        setContentView(R.layout.activity_order_detail);
        initListView();
        loadData();
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList list) {
        return new OrderDetailAdapter(list, this);
    }

    @Override
    public void onClick(View v) {
        if (util == null) {
            util = new OrderOperateUtil();
        }
        switch ((int) v.getTag()) {
            case R.string.button_order_comfirm: // 确认收货
                if(cancelInterface == null){
                    cancelInterface = new OrderOperateUtil.CancelInterface() {
                        @Override
                        public void beforeCancel() {
                            showLoadDislog();
                        }

                        @Override
                        public void cancelSuccess(String message) {
                            showToast(message);
                            dismissLoadDialog();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void cancelFail() {
                            dismissLoadDialog();
                        }
                    };
                }
                util.showComfirmDialog(cancelInterface, OrderDetailActivity.this, getIntent().getAction());
                break;
            case R.string.button_order_cancel: // 取消订单
                if(cancelInterface == null){
                    cancelInterface = new OrderOperateUtil.CancelInterface() {
                        @Override
                        public void beforeCancel() {
                            showLoadDislog();
                        }

                        @Override
                        public void cancelSuccess(String message) {
                            dismissLoadDialog();
                            showToast(message);
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void cancelFail() {
                            dismissLoadDialog();
                        }
                    };
                }
                util.showCancelTipDialog(cancelInterface, OrderDetailActivity.this, getIntent().getAction());
                break;
            case R.string.button_order_pay: // 订单支付
                if (orderAmount != null && orderId != null) {
                    util.goToPay(this, orderId, orderAmount);
                } else {
                    showToast("订单信息不完全");
                }
                break;
        }
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
                dismissLoadDialog();
                try {
                    JSONArray array = new JSONArray(response.datum);
                    JSONObject object = array.getJSONObject(0);
                    ArrayList<OrderDetailBean> list = JsonParser.getInstance().fromJson(array.getString(1),
                            new TypeToken<ArrayList<OrderDetailBean>>() {
                            }.getType());
                    listView.addHeaderView(getHeaderView(object));
                    listView.addFooterView(getFooterView(object));
                    onLoadResult(list);

                    // 显示底部按钮
                    int status = getIntent().getIntExtra("status", -1);
                    if (status > 0) {
                        findViewById(R.id.bottom_button_content).setVisibility(View.VISIBLE);
                        TextView button1 = (TextView) findViewById(R.id.order_button_1);
                        button1.setOnClickListener(OrderDetailActivity.this);
                        switch (status) {
                            case 1:// 待付款
                                button1.setText(R.string.button_order_pay);
                                button1.setTag(R.string.button_order_pay);
                                TextView button2 = (TextView) findViewById(R.id.order_button_2);
                                button2.setVisibility(View.VISIBLE);
                                button2.setOnClickListener(OrderDetailActivity.this);
                                button2.setText(R.string.button_order_cancel);
                                button2.setTag(R.string.button_order_cancel);
                                break;
                            case 2:// 待服务
                                button1.setText(R.string.button_order_comfirm);
                                button1.setTag(R.string.button_order_comfirm);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "order/order_detail_user", "sn", getIntent().getAction());
    }

    // 获取headerview
    private View getHeaderView(JSONObject json) throws JSONException {
        View view = View.inflate(this, R.layout.header_order_detail, null);
        TextView numView = (TextView) view.findViewById(R.id.order_detail_num);
        TextView addressView = (TextView) view.findViewById(R.id.order_detail_address);
        TextView typeView = (TextView) view.findViewById(R.id.order_detail_type);
        TextView timeView = (TextView) view.findViewById(R.id.order_detail_time);
        TextView payView = (TextView) view.findViewById(R.id.order_detail_pay);
        TextView phoneView = (TextView) view.findViewById(R.id.order_detail_phone);
        if (json.has("address")) {
            addressView.setText(json.getString("address"));
        }
        if (json.has("sn")) {
            numView.setText(json.getString("sn"));
        }
        if (json.has("shipping_method_name")) {
            typeView.setText(json.getString("shipping_method_name"));
        }
        if (json.has("payment_method_name")) {
            payView.setText(json.getString("payment_method_name"));
        }
        if (json.has("service_date")) {
            String time = json.getString("service_date");
            if(TextUtils.isEmpty(time) && !time.equals("null")){
                timeView.setText(time);
            }else{
                timeView.setText("尽快安排");
            }

        }
        if (json.has("phone")) {
            phoneView.setText(json.getString("phone"));
        }
        if (json.has("id")) {
            orderId = json.getString("id");
        }
        if (json.has("amount")) {
            orderAmount = json.getString("amount");
        }
        return view;
    }

    // 获取headerview
    private View getFooterView(JSONObject json) throws JSONException {
        View view = View.inflate(this, R.layout.footer_order_detail, null);
        TextView priceView = (TextView) view.findViewById(R.id.order_price);
        TextView timeView = (TextView) view.findViewById(R.id.order_time);
        if (json.has("amount")) {
            priceView.setText(String.format(getString(R.string.price_format), json.getString("amount")));
        }
        if (json.has("creation_date")) {
            timeView.setText(json.getString("creation_date"));
        }
        return view;
    }

    class OrderDetailAdapter extends BaseClassAdapter {
        OrderDetailAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context, 1, MyApplication.getInstance().getScreen_width(), true);
        }

        @Override
        protected ParentViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, ParentViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            OrderDetailBean odb = (OrderDetailBean) getItem(position);
            ho.titleView.setText(odb.name);
        }

        @Override
        protected ArrayList<?> getItemList(int position) {
            return ((OrderDetailBean) list.get(position)).list;
        }

        @Override
        protected View createItemView(Context context) {
            return View.inflate(context, R.layout.item_order_detail_item, null);
        }

        @Override
        protected void bindItemData(int position, Object obj, ItemViewHolder holder) {
            OrderItemBean oib = (OrderItemBean) obj;
            OrderViewHolder ho = (OrderViewHolder) holder;
            Tools.setImage(context, ho.imageView, oib.image);
            ho.countView.setText(String.format(getString(R.string.count_format_string), oib.quantity));
            ho.nameView.setText(oib.full_name);
            ho.priceView.setText(String.format(getString(R.string.price_format), oib.pricestr));
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        protected ItemViewHolder bindItemViewHolder(View v) {
            return new OrderViewHolder(v);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_order_detail, null);
        }

        class ViewHolder extends ParentViewHolder {
            TextView titleView;

            public ViewHolder(View view) {
                super(view);
                titleView = (TextView) view.findViewById(R.id.order_class_title);
            }

            @Override
            protected LinearLayout getContentView(View view) {
                return (LinearLayout) view.findViewById(R.id.order_item_content);
            }
        }

        class OrderViewHolder extends ItemViewHolder {
            ImageView imageView;
            TextView nameView, priceView, countView;

            OrderViewHolder(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.shop_detail_photo);
                nameView = (TextView) v.findViewById(R.id.shop_detail_title);
                priceView = (TextView) v.findViewById(R.id.shop_detail_price);
                countView = (TextView) v.findViewById(R.id.shop_detail_count);
            }
        }
    }
}
