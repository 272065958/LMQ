package net.guanjiale.lmq.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.adapter.MyBaseAdapter;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.CouponBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/27.
 */
public class CouponAdapter extends MyBaseAdapter {
    int position;
    public CouponAdapter(ArrayList<?> list, BaseActivity context, int position){
        super(list, context);
        this.position = position;
    }

    @Override
    public View createView(Context context) {
        return View.inflate(context, R.layout.item_coupon, null);
    }

    @Override
    public void bindData(int position, MyViewHolder holder) {
        CouponBean cb = (CouponBean) getItem(position);
        ViewHolder ho = (ViewHolder) holder;
        ho.timeView.setText(String.format(context.getString(R.string.coupon_valid_time_format), cb.end_date));
        ho.nameView.setText(cb.name);
        ho.priceView.setText("￥"+cb.price);
    }

    @Override
    public MyViewHolder bindViewHolder(View v) {
        return new ViewHolder(v);
    }

    class ViewHolder extends MyViewHolder{
        TextView nameView, timeView, priceView;
        public ViewHolder(View v){
            super(v);
            priceView = (TextView) v.findViewById(R.id.coupon_price);
            nameView = (TextView) v.findViewById(R.id.coupon_name);
            timeView = (TextView) v.findViewById(R.id.coupon_time);
            if(position > 0){
                priceView.setBackgroundColor(ContextCompat.getColor(context, R.color.invalid_color));
            }
        }
    }
}