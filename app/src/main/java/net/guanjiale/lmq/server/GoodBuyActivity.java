package net.guanjiale.lmq.server;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.activity.OrderActivity;
import net.guanjiale.lmq.bean.BaseProductBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/1/11.
 */
public abstract class GoodBuyActivity extends BaseActivity {
    int cartCount = 0;
    String allPrice = "0";
    TextView countView, priceView;

    protected void findViewById(){
        countView = (TextView) findViewById(R.id.shop_count);
        priceView = (TextView) findViewById(R.id.order_price);
    }

    // 提交订单
    public void buyClick(View v) {
        switch (v.getId()) {
            case R.id.shop_cart_add:
                addCart();
                break;
            case R.id.shop_cart_pay:
                pay();
                break;
            case R.id.shop_cart_icon:
                Intent intent = new Intent(this, ShopCartActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 显示购物车数量
    protected void showCartCount() {
        if (cartCount > 0) {
            countView.setVisibility(View.VISIBLE);
            countView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.cart_notice));
            countView.setText(String.valueOf(cartCount));
        } else {
            countView.setVisibility(View.GONE);
        }
    }

    protected void showPrice() {
        priceView.setText(String.format(getString(R.string.price_format), allPrice));
    }

    // 添加购物车
    private void addCart() {
        String[] params = getProductIdAndCount();
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                cartCount = Integer.parseInt(response.datum);
                return response.message;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                showToast((String) result);
                try{
                    showCartCount();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "cart/add", "id", params[0],
                "quantity", params[1]);
    }

    private void pay() {
        ArrayList<BaseProductBean> list = getProducts();
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("product", list);
        intent.setAction(getIntent().getAction());
        startActivity(intent);
    }

    protected abstract ArrayList<BaseProductBean> getProducts();

    protected abstract String[] getProductIdAndCount();
}
