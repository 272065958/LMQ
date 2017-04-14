package net.guanjiale.lmq.server;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.GoodsBean;
import net.guanjiale.lmq.bean.SortBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/7.
 * 购买商品界面
 */
public class ShopListActivity extends BaseActivity {
    ArrayList<SortBean> sortList;
    int cartCount;
    TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Intent intent = getIntent();
        setToolBar(true, null, intent.getAction());
        loadData();
        registerReceiver(new IntentFilter(CustomApplication.ACTION_CART_COUNT_UPDATE));
    }

    // 收到广播回调
    @Override
    protected void onBroadcastReceive(Intent intent) {
        cartCount = intent.getIntExtra("count", cartCount);
        showCartCount();
    }

    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                if (response.datum != null) {
                    try {
                        JSONArray array = new JSONArray(response.datum);
                        sortList = JsonParser.getInstance().fromJson(array.getString(0), new TypeToken<ArrayList<SortBean>>() {
                        }.getType());
                        cartCount = array.getInt(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            public void success(Object result) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                if (sortList == null || sortList.isEmpty()) {
                    showToast("没有数据");
                    finish();
                    return;
                }
                findViewById();
                diaplayData(sortList);
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/newSecondIndex", "key", getIntent().getStringExtra("key"),
                "type", getIntent().getStringExtra("type"));
    }

    protected void findViewById() {
        countView = (TextView) findViewById(R.id.shop_count);
        findViewById(R.id.goods_content).setVisibility(View.VISIBLE);
        showCartCount();
    }

    Animation countAnimation;

    // 显示购物车数量
    private void showCartCount() {
        if (cartCount > 0) {
            if (countView.getVisibility() == View.GONE) {
                countView.setVisibility(View.VISIBLE);
            }
            if (countAnimation == null) {
                countAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.cart_notice);
            }
            countView.setText(String.valueOf(cartCount));
            countView.startAnimation(countAnimation);
        } else {
            if (countView.getVisibility() == View.VISIBLE) {
                countView.setVisibility(View.GONE);
            }
        }
    }

    GoodsAdapter goodsAdapter;
    SortAdapter sortAdapter;
    SortBean currentSort;

    // 显示商品
    private void diaplayData(ArrayList<SortBean> list) {
        ListView sortView = (ListView) findViewById(R.id.shop_class_view);
        assert sortView != null;
        sortView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortAdapter adapter = (SortAdapter) parent.getAdapter();
                SortBean sb = (SortBean) adapter.getItem(position);
                currentSort = sb;
                adapter.setCurrentSelect(position);
                goodsAdapter.notifyDataSetChanged(sb.detail);
            }
        });
        sortAdapter = new SortAdapter(list, this);
        sortView.setAdapter(sortAdapter);
        ListView goodsView = (ListView) findViewById(R.id.shop_list_view);
        assert goodsView != null;
        goodsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        currentSort = list.get(0);
        goodsAdapter = new GoodsAdapter(currentSort.detail, this);
        goodsView.setAdapter(goodsAdapter);
    }

    // 添加购物车
    private void addCart(String id) {
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response;
            }

            @Override
            public void success(Object result) {
                ResponseBean response = (ResponseBean) result;
                dismissLoadDialog();
                showToast(response.message);
                try {
                    cartCount = Integer.parseInt(response.datum);
                    showCartCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "cart/add", "id", id,
                "quantity", "1");
    }
    // 进入购物车
    public void buyClick(View v) {
        Intent intent = new Intent(this, ShopCartActivity.class);
        startActivity(intent);
    }

    class SortAdapter extends MyBaseAdapter {

        int currentSelectPosition = 0;

        SortAdapter(ArrayList<SortBean> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        public View createView(Context context) {
            return View.inflate(context, R.layout.item_sort, null);
        }

        @Override
        public void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            View v = ho.getView();
            if (position == currentSelectPosition) {
                v.setBackgroundColor(Color.WHITE);
            } else {
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.cjx_background_color));
            }
            SortBean sb = (SortBean) getItem(position);
            ho.nameView.setText(sb.name);
            if (sb.buyList.isEmpty()) {
                ho.countView.setVisibility(View.GONE);
            } else {
                ho.countView.setVisibility(View.VISIBLE);
                int count = 0;
                for (GoodsBean gb : sb.buyList) {
                    count += gb.quantity;
                }
                ho.countView.setText(String.valueOf(count));
            }
        }

        @Override
        public MyViewHolder bindViewHolder(View v) {
            return new ViewHolder(v);
        }

        void setCurrentSelect(int position) {
            currentSelectPosition = position;
            notifyDataSetChanged();
        }

        class ViewHolder extends MyViewHolder {
            TextView countView, nameView;

            public ViewHolder(View v) {
                super(v);
                countView = (TextView) v.findViewById(R.id.shop_count);
                nameView = (TextView) v.findViewById(R.id.shop_sort);
            }
        }
    }

    class GoodsAdapter extends MyBaseAdapter {

        GoodsAdapter(ArrayList<GoodsBean> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        public View createView(Context context) {
            return View.inflate(context, R.layout.item_goods, null);
        }

        @Override
        public void bindData(int position, MyViewHolder holder) {
            GoodsBean gb = (GoodsBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.priceView.setText(String.format(getString(R.string.price_format), gb.price));
            ho.titleView.setText(gb.name);
            Tools.setImage(context, ho.imageView, gb.image);
            ho.addCartView.setTag(gb.id);
        }

        @Override
        public MyViewHolder bindViewHolder(View v) {
            return new ViewHolder(v);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            View addCartView;
            ImageView imageView;
            TextView titleView, priceView;

            ViewHolder(View v) {
                super(v);
                addCartView = v.findViewById(R.id.goods_add_cart);
                addCartView.setOnClickListener(this);
                imageView = (ImageView) v.findViewById(R.id.goods_image);
                titleView = (TextView) v.findViewById(R.id.goods_title);
                priceView = (TextView) v.findViewById(R.id.goods_price);
            }

            @Override
            public void onClick(View v) {
                String id = (String) v.getTag();
                addCart(id);
            }
        }
    }
}