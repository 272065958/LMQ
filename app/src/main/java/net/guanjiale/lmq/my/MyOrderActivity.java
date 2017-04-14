package net.guanjiale.lmq.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.activity.BaseTabRefreshActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.OrderBean;
import net.guanjiale.lmq.bean.OrderItemBean;
import net.guanjiale.lmq.util.OrderOperateUtil;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/27.
 * 我的订单
 */
public class MyOrderActivity extends BaseTabRefreshActivity {
    String[] value;
    String[] status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBar(true, null, R.string.myself_order);
    }

    @Override
    protected String[] initTitle() {
        value = new String[]{null, "0", "0", "2"};
        status = new String[]{null, "payment_status", "shipping_status", "order_status"};
        return new String[]{
                getString(R.string.order_all), getString(R.string.order_pay),
                getString(R.string.order_server), getString(R.string.order_finish)};
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    // 加载数据
    @Override
    protected void loadData(int position) {
        HttpUtils.getInstance().postEnqueue(this, getMyCallbackInterface(position, new TypeToken<ArrayList<OrderBean>>() {
                }.getType()), "order/index",
                "searchProperty", status[position], "searchValue", value[position]);
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(int position, ArrayList list) {
        return new OrderAdapter(list, this);
    }

    class OrderAdapter extends MyBaseAdapter {

        OrderAdapter(ArrayList list, BaseActivity context) {
            super(list, context);
        }

        @Override
        public View createView(Context context) {
            return View.inflate(context, R.layout.item_order_pat, null);
        }

        @Override
        public void bindData(int position, MyViewHolder holder) {
            OrderBean ob = (OrderBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            holder.getView().setTag(ob);
            ho.timeView.setText(ob.creation_date);
            int size = ob.itemList.size();
            if (size == 1) {
                ho.recyclerView.setVisibility(View.GONE);
                ho.detailView.setVisibility(View.VISIBLE);
                OrderItemBean oib = ob.itemList.get(0);
                ho.countView.setText(String.format(getString(R.string.count_format_string), oib.quantity));
                Tools.setImage(context, ho.iconView, oib.image);
                ho.titleView.setText(oib.full_name);
                ho.priceView.setText(String.format(getString(R.string.price_format), oib.pricestr));
            } else {
                ho.recyclerView.setVisibility(View.VISIBLE);
                ho.detailView.setVisibility(View.GONE);
                ProductAdapter pAdapter;
                if (ho.recyclerView.getAdapter() != null) {
                    pAdapter = (ProductAdapter) ho.recyclerView.getAdapter();
                    pAdapter.notifyDataSetChanged(ob.itemList);
                } else {
                    pAdapter = new ProductAdapter(ob.itemList, context);
                    ho.recyclerView.setAdapter(pAdapter);
                }
            }
            ho.payView.setText(String.format(getString(R.string.price_format), ob.amount)); // 总价
            int status = getOrderStatus(ob);
            switch (status) {
                case 0:// 已完成
                    ho.statusView.setText(R.string.order_finish);
                    ho.button2View.setVisibility(View.GONE);
                    ho.button1View.setVisibility(View.GONE);
                    break;
                case 1:// 待付款
                    ho.statusView.setText(R.string.order_pay);
                    ho.button2View.setVisibility(View.VISIBLE);
                    ho.button1View.setVisibility(View.VISIBLE);
                    ho.button2View.setText(R.string.button_order_cancel);
                    ho.button1View.setText(R.string.button_order_pay);

                    ho.button1View.setTag(R.id.tag_type, ob);
                    ho.button1View.setTag(R.string.button_order_pay);
                    ho.button2View.setTag(R.id.tag_type, ob);
                    ho.button2View.setTag(R.string.button_order_cancel);
                    break;
                case 2:// 确认收货
                    ho.statusView.setText(R.string.order_server);
                    ho.button2View.setVisibility(View.GONE);
                    ho.button1View.setVisibility(View.VISIBLE);
                    ho.button1View.setText(R.string.button_order_comfirm);
                    ho.button1View.setTag(R.string.button_order_comfirm);
                    ho.button1View.setTag(R.id.tag_type, ob);
                    break;
            }
        }

        @Override
        public MyViewHolder bindViewHolder(View v) {
            return new ViewHolder(v);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            TextView timeView, titleView, countView, priceView, payView, button1View, button2View, statusView;
            ImageView iconView;
            View detailView;
            RecyclerView recyclerView;

            OrderOperateUtil.CancelInterface cancelInterface;
            OrderOperateUtil util;
            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                timeView = (TextView) v.findViewById(R.id.order_time);
                titleView = (TextView) v.findViewById(R.id.shop_detail_title);
                countView = (TextView) v.findViewById(R.id.shop_detail_count);
                priceView = (TextView) v.findViewById(R.id.shop_detail_price);
                payView = (TextView) v.findViewById(R.id.order_pay);
                statusView = (TextView) v.findViewById(R.id.order_status);
                button1View = (TextView) v.findViewById(R.id.order_button_1);
                button1View.setOnClickListener(this);
                button2View = (TextView) v.findViewById(R.id.order_button_2);
                button2View.setOnClickListener(this);
                iconView = (ImageView) v.findViewById(R.id.shop_detail_photo);

                detailView = v.findViewById(R.id.order_detail_content);
                recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);

            }

            @Override
            public void onClick(View v) {
                Object obj = v.getTag();
                if (obj instanceof OrderBean) {
                    OrderBean ob = (OrderBean) v.getTag();
                    int status = getOrderStatus(ob);
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.setAction(ob.sn);
                    intent.putExtra("status", status);
                    startActivity(intent);
                } else {
                    if(util == null){
                        util = new OrderOperateUtil();
                    }
                    OrderBean ob = (OrderBean) v.getTag(R.id.tag_type);
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
                                        refresh();
                                    }

                                    @Override
                                    public void cancelFail() {
                                        dismissLoadDialog();
                                    }
                                };
                            }
                            util.showComfirmDialog(cancelInterface, MyOrderActivity.this, ob.sn);
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
                                        showToast(message);
                                        dismissLoadDialog();
                                        refresh();
                                    }

                                    @Override
                                    public void cancelFail() {
                                        dismissLoadDialog();
                                    }
                                };
                            }
                            util.showCancelTipDialog(cancelInterface, MyOrderActivity.this, ob.sn);
                            break;
                        case R.string.button_order_pay: // 订单支付
                            util.goToPay(MyOrderActivity.this, ob.id, ob.amount);
                            break;
                    }
                }
            }
        }
    }


    class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Activity context;
        ArrayList<OrderItemBean> list;
        int size;
        int margin;

        ProductAdapter(ArrayList<OrderItemBean> list, Activity context) {
            this.list = list;
            this.context = context;
            size = getResources().getDimensionPixelSize(R.dimen.image_size);
            margin = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(context);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
            if (viewType == 0) {
                lp.leftMargin = margin;
            }
            lp.rightMargin = margin;
            lp.topMargin = margin;
            lp.bottomMargin = margin;
            imageView.setLayoutParams(lp);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OrderItemBean oib = list.get(position);
            Tools.setImage(context, (ImageView) holder.itemView, oib.image);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public void notifyDataSetChanged(ArrayList<OrderItemBean> itemList) {
            this.list = itemList;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private int getOrderStatus(OrderBean ob) {
        int state = -1; // 0 = 已完成, 1 = 待付款, 2 = 待服务, 3 = 退款审核中, 4 = 已退款
        if (ob.order_status == 2) {
            state = 0; // 已完成
        } else {
            if (ob.payment_status == 0) {
                state = 1; //待付款
            } else {
                if (ob.payment_status == 2 && ob.order_status == 1 &&
                        (ob.shipping_status == 0 || ob.shipping_status == 2)) {
                    state = 2; //待服务
                }
            }
        }
        return state;
    }
}
