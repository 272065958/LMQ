package net.guanjiale.lmq.server;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.base.GoodBuyActivity;
import net.guanjiale.lmq.bean.BaseProductBean;
import net.guanjiale.lmq.bean.ServerProductBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/23.
 * 购买桶装水
 */

public class OrderWaterActivity extends GoodBuyActivity {
    BaseProductBean product;
    ArrayList<BaseProductBean> products;
    TextView bucketCountView, waterCountView;
    TextView pumpCountView;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    int cartCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_water);
        setToolBar(true, null, getIntent().getAction());

        loadData(getIntent().getStringExtra("key"));
        registerReceiver(new IntentFilter(CustomApplication.ACTION_ORDER_CREATE));
    }

    @Override
    protected ArrayList<BaseProductBean> getProducts() {
        ArrayList<BaseProductBean> list = new ArrayList<>();
        int count = Integer.parseInt(waterCountView.getText().toString());
        product.quantity = count;
        product.price = decimalFormat.format(Float.parseFloat(product.price));
        list.add(product);
        int bucketCount = bucketCountView == null ? 0 : Integer.parseInt(bucketCountView.getText().toString());
        if (bucketCount > 0) {
            BaseProductBean pb = (BaseProductBean) bucketCountView.getTag(R.id.product_content);
            pb.quantity = bucketCount;
            pb.price = decimalFormat.format(Float.parseFloat(pb.price));
            list.add(pb);
        }
        int pumpCount = pumpCountView == null ? 0 : Integer.parseInt(pumpCountView.getText().toString());
        if (pumpCount > 0) {
            BaseProductBean pb = (BaseProductBean) pumpCountView.getTag(R.id.product_content);
            pb.quantity = pumpCount;
            pb.price = decimalFormat.format(Float.parseFloat(pb.price));
            list.add(pb);
        }
        return list;
    }

    @Override
    protected String[] getProductIdAndCount() {
        StringBuffer ids = new StringBuffer();
        final StringBuffer quantitys = new StringBuffer();
        ids.append(waterCountView.getTag(R.id.product_content));
        String countStr = waterCountView.getText().toString();
        quantitys.append(countStr);

        int bucketCount = bucketCountView == null ? 0 : Integer.parseInt(bucketCountView.getText().toString());
        if (bucketCount > 0) {
            ids.append(",");
            ids.append(((ServerProductBean) bucketCountView.getTag(R.id.product_content)).id);
            quantitys.append(",");
            quantitys.append(bucketCount);
        }

        int pumpCount = pumpCountView == null ? 0 : Integer.parseInt(pumpCountView.getText().toString());
        if (pumpCount > 0) {
            ids.append(",");
            ids.append(((ServerProductBean) pumpCountView.getTag(R.id.product_content)).id);
            quantitys.append(",");
            quantitys.append(pumpCount);
        }
        return new String[]{ids.toString(), quantitys.toString()};
    }

    // 收到广播回调
    @Override
    protected void onBroadcastReceive(Intent intent) {
        finish();
    }

    protected void findViewById() {
        super.findViewById();
        if (product == null) {
            return;
        }
        findViewById(R.id.product_content).setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_button).setVisibility(View.VISIBLE);
        View v = findViewById(R.id.water_shop);
        ImageView iconView = (ImageView) v.findViewById(R.id.shop_detail_photo);
        Tools.setImage(this, iconView, product.image);
        TextView titleView = (TextView) v.findViewById(R.id.shop_detail_title);
        titleView.setText(product.name);
        TextView priceView = (TextView) v.findViewById(R.id.shop_detail_price);
        priceView.setText("￥" + decimalFormat.format(Float.parseFloat(product.price)));
        waterCountView = (TextView) v.findViewById(R.id.count_view);
        waterCountView.setSelected(true); // 标记不能小于1
        waterCountView.setTag(Float.parseFloat(product.price));
        waterCountView.setTag(R.id.product_content, product.id);

        View addView = v.findViewById(R.id.count_add);
        addView.setTag(waterCountView);
        addView.setOnClickListener(clickListener);
        View minusVuew = v.findViewById(R.id.count_minus);
        minusVuew.setTag(waterCountView);
        minusVuew.setSelected(true);
        minusVuew.setOnClickListener(clickListener);

        if (products != null && products.size() > 1) {
            findViewById(R.id.water_bucket).setVisibility(View.VISIBLE);
            findViewById(R.id.water_shop_line).setVisibility(View.VISIBLE);
            findViewById(R.id.water_pump).setVisibility(View.VISIBLE);
            BaseProductBean pb = products.get(0);
            v = findViewById(R.id.water_bucket);
            iconView = (ImageView) v.findViewById(R.id.shop_detail_photo);
            Tools.setImage(this, iconView, pb.image);
            titleView = (TextView) v.findViewById(R.id.shop_detail_title);
            titleView.setText(pb.name);
            priceView = (TextView) v.findViewById(R.id.shop_detail_price);
            priceView.setText("￥" + decimalFormat.format(Float.parseFloat(pb.price)));
            bucketCountView = (TextView) v.findViewById(R.id.count_view);
            bucketCountView.setText("0");
            bucketCountView.setTag(Float.parseFloat(pb.price));
            bucketCountView.setTag(R.id.product_content, pb);

            addView = v.findViewById(R.id.count_add);
            addView.setTag(bucketCountView);
            addView.setOnClickListener(clickListener);
            minusVuew = v.findViewById(R.id.count_minus);
            minusVuew.setTag(bucketCountView);
            minusVuew.setOnClickListener(clickListener);

            pb = products.get(1);
            v = findViewById(R.id.water_pump);
            iconView = (ImageView) v.findViewById(R.id.shop_detail_photo);
            Tools.setImage(this, iconView, pb.image);
            titleView = (TextView) v.findViewById(R.id.shop_detail_title);
            titleView.setText(pb.name);
            priceView = (TextView) v.findViewById(R.id.shop_detail_price);
            priceView.setText("￥" + decimalFormat.format(Float.parseFloat(pb.price)));
            pumpCountView = (TextView) v.findViewById(R.id.count_view);
            pumpCountView.setText("0");
            pumpCountView.setTag(Float.parseFloat(pb.price));
            pumpCountView.setTag(R.id.product_content, pb);

            addView = v.findViewById(R.id.count_add);
            addView.setTag(pumpCountView);
            addView.setOnClickListener(clickListener);
            minusVuew = v.findViewById(R.id.count_minus);
            minusVuew.setTag(pumpCountView);
            minusVuew.setOnClickListener(clickListener);
        }
        initPrice();
        showCartCount();
    }

    private void loadData(String key) {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                try {
                    JSONObject obj = new JSONObject(response.datum);
                    JsonParser parser = JsonParser.getInstance();
                    if (obj.has("cartCount")) {
                        cartCount = obj.getInt("cartCount");
                    }
                    if (obj.has("product")) {
                        product = parser.fromJson(obj.getString("product"),
                                new TypeToken<ServerProductBean>() {
                                }.getType());
                    }
                    if (obj.has("products")) {
                        products = parser.fromJson(obj.getString("products"),
                                new TypeToken<ArrayList<ServerProductBean>>() {
                                }.getType());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void success(Object result) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                if(product != null){
                    findViewById();
                }
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/fourPage", "key", key);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView countView = (TextView) v.getTag();
            int count = Integer.parseInt(countView.getText().toString());
            switch (v.getId()) {
                case R.id.count_add:
                    count++;
                    break;
                case R.id.count_minus:
                    if (v.isSelected()) { // 不能小于1
                        if (count > 1) {
                            count--;
                        }
                    } else {
                        if (count > 0) {
                            count--;
                        }
                    }
                    break;
            }
            countView.setText(String.valueOf(count));
            initPrice();
        }
    };

    // 显示总价格
    private void initPrice() {
        float waterPrice = ((float) waterCountView.getTag()) * Integer.parseInt(waterCountView.getText().toString());
        float bucketPrice = bucketCountView == null ? 0 :
                ((float) bucketCountView.getTag()) * Integer.parseInt(bucketCountView.getText().toString());
        float pumpPrice = pumpCountView == null ? 0 :
                ((float) pumpCountView.getTag()) * Integer.parseInt(pumpCountView.getText().toString());
        allPrice = decimalFormat.format(waterPrice + bucketPrice + pumpPrice);
        showPrice();
    }

}
