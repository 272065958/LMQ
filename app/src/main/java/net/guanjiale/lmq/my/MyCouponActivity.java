package net.guanjiale.lmq.my;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseTabRefreshActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.http.HttpUtils;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.adapter.CouponAdapter;
import net.guanjiale.lmq.bean.CouponBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/27.
 * 我的优惠券
 */
public class MyCouponActivity extends BaseTabRefreshActivity {

    String[] value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBar(true, null, R.string.myself_coupon);
    }

    @Override
    protected String[] initTitle() {
        value = new String[]{"0", "1"};
        return new String[]{
                getString(R.string.coupon_no_use),
                getString(R.string.coupon_use), getString(R.string.coupon_outtime)};
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(int position, ArrayList list) {
        return new CouponAdapter(list, this, position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void onLoadResult(int position, ArrayList list){
        if(list != null){
            for(Object obj : list){
                ((CouponBean)obj).initPrice();
            }
        }
        super.onLoadResult(position, list);
    }

    // 加载数据
    @Override
    protected void loadData(int position) {
        if(position == 2){ // 已过期
            HttpUtils.getInstance().postEnqueue(this, getMyCallbackInterface(position, new TypeToken<ArrayList<CouponBean>>() {
            }.getType()), "coupon/index", "useDate", "overdate");
        }else{
            HttpUtils.getInstance().postEnqueue(this, getMyCallbackInterface(position, new TypeToken<ArrayList<CouponBean>>() {
            }.getType()), "coupon/index", "searchProperty", "is_used", "searchValue", value[position]);
        }
    }

}
