package net.guanjiale.lmq.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.dialog.DateSelectDialog;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.AddressBean;
import net.guanjiale.lmq.bean.BaseProductBean;
import net.guanjiale.lmq.bean.CouponBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cjx on 2016/12/23.
 * 商品提交订单页面
 */

public class OrderActivity extends BaseActivity {
    final int RESULT_ADDRESS = 1, RESULT_COUPON = 2;
    AddressBean address;
    ArrayList<CouponBean> couponList;
    ArrayList<BaseProductBean> products;
    CouponBean coupon;
    TextView couponView;
    TextView nameView, addressView, phoneView;
    TextView priceView, timeView;
    EditText remarkVew;

    boolean isFromCart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setToolBar(R.drawable.back_icon, null, R.string.shop_pay);

        Intent intent = getIntent();
        products = (ArrayList<BaseProductBean>) intent.getSerializableExtra("product");
        isFromCart = intent.getBooleanExtra("fromCart", false);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_ADDRESS:
                    address = (AddressBean) data.getSerializableExtra("address");
                    initAddressView();
                    break;
                case RESULT_COUPON:
                    CouponBean cb = (CouponBean) data.getSerializableExtra("coupon");
                    coupon = cb;
                    if (cb == null) {
                        couponView.setText(null);
                        couponView.setTag(null);
                    } else {
                        couponView.setText(cb.name);
                        couponView.setTag(cb.id);
                    }
                    initPrice();
                    break;
            }
        }
    }

    private void loadData(){
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                try {
                    JSONObject obj = new JSONObject(response.datum);
                    JsonParser parser = JsonParser.getInstance();
                    if (obj.has("receiver")) {
                        address = parser.fromJson(obj.getString("receiver"), AddressBean.class);
                    }
                    if (obj.has("coupon")) {
                        couponList = parser.fromJson(obj.getString("coupon"), new TypeToken<ArrayList<CouponBean>>() {
                        }.getType());
                        if (couponList != null) {
                            for (CouponBean cb : couponList) {
                                cb.initPrice();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void success(Object result) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                findViewById();
                initPrice();
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/memberResource");
    }

    private void findViewById() {
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_button).setVisibility(View.VISIBLE);
        listView.addHeaderView(getHeaderView());
        listView.setAdapter(new ShopAdapter(products, this));
        priceView = (TextView) findViewById(R.id.order_price);
    }

    // 显示收货地址
    private void initAddressView() {
        if (address != null) {
            if (phoneView.getVisibility() == View.GONE) {
                phoneView.setVisibility(View.VISIBLE);
                addressView.setVisibility(View.VISIBLE);
            }
            nameView.setText(address.consignee);
            addressView.setText(address.address);
            phoneView.setText(address.phone);
        }
    }

    private View getHeaderView() {
        View view = View.inflate(this, R.layout.order_header, null);
        nameView = (TextView) view.findViewById(R.id.property_name);
        phoneView = (TextView) view.findViewById(R.id.property_phone);
        addressView = (TextView) view.findViewById(R.id.property_location);
        couponView = (TextView) view.findViewById(R.id.coupon_view);
        timeView = (TextView) view.findViewById(R.id.order_time);
        timeView.setHint("不选择默认尽快配送");
        ((TextView) view.findViewById(R.id.order_time_tip)).setText("送货时间：");
        remarkVew = (EditText) view.findViewById(R.id.order_remark);
        if (couponList == null || couponList.isEmpty()) {
            couponView.setHint(R.string.order_no_coupon);
            view.findViewById(R.id.order_coupon_select).setClickable(false);
        }
        initAddressView();
        return view;
    }

    // 立即购买
    public void buyClick(View v) {
        if (address == null) {
            showToast(getString(R.string.receive_address_null_hint));
            return;
        }
        String time = timeView.getText().toString();
        String remark = remarkVew.getText().toString();
        String cid = (String) couponView.getTag();
        StringBuilder productIds = new StringBuilder();
        StringBuilder quantitys = new StringBuilder();
        for (BaseProductBean pb : products) {
            if (productIds.length() > 0) {
                productIds.append(",");
                quantitys.append(",");
            }
            productIds.append(pb.id);
            quantitys.append(pb.quantity);
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                ResponseBean response = (ResponseBean)result;
                showToast(response.message);
                try {
                    JSONObject jsonObject = new JSONObject(response.datum);
                    Intent payIntent = new Intent(OrderActivity.this, PayActivity.class);
                    payIntent.putExtra("id", jsonObject.getString("orderId"));
                    payIntent.putExtra("price", jsonObject.getString("amount"));
                    // String sn = jsonObject.getString("orderSn");
                    payIntent.putExtra("title", getIntent().getAction());
                    startActivity(payIntent);
                    if(isFromCart){
                        sendBroadcast(new Intent(CustomApplication.ACTION_CART_BUY));
                    }else{
                        sendBroadcast(new Intent(CustomApplication.ACTION_ORDER_CREATE));
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        if (isFromCart) {
            HttpUtils.getInstance().postEnqueue(this, callbackInterface, "order/createByNow", "cartItemIds", productIds.toString(),
                    "receiverId", address.id, "couponcodeId", cid, "dateTime", time, "remarks", remark);
        } else {
            HttpUtils.getInstance().postEnqueue(this, callbackInterface, "order/createByNow", "productIds", productIds.toString(),
                    "quantitys", quantitys.toString(), "receiverId", address.id, "couponcodeId", cid, "dateTime", time, "remarks", remark);
        }
    }

    // 选择收货地址
    public void propertySelect(View v) {
        Intent intent = new Intent(this, AddressSelectActivity.class);
        startActivityForResult(intent, RESULT_ADDRESS);
    }

    // 选择时间
    public void timeSelect(View v) {
        showDateDialog();
    }

    // 选择优惠券
    public void couponSelect(View v) {
        Intent intent = new Intent(this, CouponSelectActivity.class);
        intent.putExtra("coupon", couponList);
        startActivityForResult(intent, RESULT_COUPON);
    }

    DateSelectDialog dateDialog;

    private void showDateDialog() {
        if (dateDialog == null) {
            dateDialog = new DateSelectDialog(this);
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE)+1;
            if(hour == 24){ // +1天
                hour = 0;
                int dayCount;
                switch (month){
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        dayCount = 30;
                        break;
                    case 2:
                        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                            dayCount = 29;
                        } else {
                            dayCount = 28;
                        }
                        break;
                    default:
                        dayCount = 31;
                        break;
                }
                if(day == dayCount){
                    day = 1;
                    if(month == 12){
                        year++;
                        month = 1;
                    }else{
                        month ++;
                    }
                }else{
                    day ++;
                }
            }else{
                hour ++;
            }

            dateDialog.setDate(year, month, day, hour, min);
            dateDialog.bind(new DateSelectDialog.DateSelectListener() {
                @Override
                public void select(String date) {
                    timeView.setText(date);
                }
            });
        }
        dateDialog.show(DateSelectDialog.DateType.FETURE, new Date(System.currentTimeMillis()+3600000), "期望的送货时间必须是1小时之后");
    }

    // 显示价格
    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    private void initPrice() {
        if (products != null) {
            BigDecimal priceDecimal = new BigDecimal("0");
            int count = 0;
            for (BaseProductBean pb : products) {
                count = count + pb.quantity;
                BigDecimal unitPrice = new BigDecimal(pb.price);
                BigDecimal buyCount = new BigDecimal(pb.quantity);
                priceDecimal = priceDecimal.add(buyCount.multiply(unitPrice));
            }
            if (coupon != null) {
                priceDecimal = priceDecimal.subtract(new BigDecimal(coupon.price));
            }
            float price = priceDecimal.floatValue();
            if (price < 0) {
                price = 0;
            }
            priceView.setText(Html.fromHtml(String.format(getString(R.string.order_pay_price_format),
                    "<font color='red'>" + "￥" + decimalFormat.format(price) + "</font>")));
        }
    }

    class ShopAdapter extends MyBaseAdapter {
        public ShopAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_order, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            BaseProductBean pb = (BaseProductBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            Tools.setImage(context, ho.iconView, pb.image);
            ho.titleView.setText(pb.name);
            ho.priceView.setText("￥" + pb.price);
            ho.countView.setText("×" + pb.quantity);
        }

        class ViewHolder extends MyViewHolder {
            ImageView iconView;
            TextView titleView, priceView, countView;

            public ViewHolder(View view) {
                super(view);
                iconView = (ImageView) view.findViewById(R.id.shop_detail_photo);
                titleView = (TextView) view.findViewById(R.id.shop_detail_title);
                priceView = (TextView) view.findViewById(R.id.shop_detail_price);
                countView = (TextView) view.findViewById(R.id.shop_detail_count);
            }
        }
    }
}
