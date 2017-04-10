package net.guanjiale.lmq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.model.cjx.activity.BaseListActivity;
import com.model.cjx.adapter.MyBaseAdapter;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.adapter.CouponAdapter;
import net.guanjiale.lmq.bean.CouponBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/29.
 */
public class CouponSelectActivity extends BaseListActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBar(R.drawable.back_icon, null, R.string.order_select_coupon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.un_use, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK, new Intent());
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList list) {
        return new CouponAdapter(list, this, 0);
    }

    @Override
    protected void loadData() {
        ArrayList<CouponBean> list = (ArrayList<CouponBean>) getIntent().getSerializableExtra("coupon");
        onLoadResult(list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CouponBean cb = (CouponBean) parent.getAdapter().getItem(position);
        Intent data = new Intent();
        data.putExtra("coupon", cb);
        setResult(RESULT_OK, data);
        finish();
    }
}
