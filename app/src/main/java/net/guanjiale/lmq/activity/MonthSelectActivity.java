package net.guanjiale.lmq.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.cjx.activity.BaseActivity;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;

import java.util.Calendar;

/**
 * Created by cjx on 17-3-20.
 */

public class MonthSelectActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_select);

        initView();
    }

    @Override
    public void onClick(View v) {

    }

    private void initView() {
        int color = ContextCompat.getColor(this, R.color.cjx_text_deep_color);
        int invaildColor = ContextCompat.getColor(this, R.color.cjx_text_secondary_color);
        int padding = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        int width = CustomApplication.getInstance().getScreen_width();
        int size = (int) (width / 6f);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(size, size);
        ViewGroup.LayoutParams lineLp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.divider_height));

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        int start = 12 - month;
        LinearLayout monthSelectView = (LinearLayout) findViewById(R.id.month_select_view);
        int count;
        if (start == 0) {
            count = 3;
        } else {
            count = 4;
        }
        for (int i = 0; i < count; i++) {
            monthSelectView.addView(getYearView(padding, color, String.valueOf(year - i)));
            monthSelectView.addView(getLineMonth(year - i, 12, color, invaildColor, this, lp, year, month));
            monthSelectView.addView(getLineMonth(year - i, 6, color, invaildColor, this, lp, year, month));
            monthSelectView.addView(getLineView(), lineLp);
        }
    }


    private View getYearView(int padding, int color, String year) {
        TextView yearView = new TextView(this);
        yearView.setPadding(padding, padding, padding, padding);
        yearView.setTextColor(color);
        yearView.setText(year);
        return yearView;
    }

    private LinearLayout getLineMonth(int year, int start, int vaildColor, int invaildColor, View.OnClickListener listener,
                                      ViewGroup.LayoutParams lp, int currentYear, int currentMonth) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 6; i++) {
            int month = start - i;
            int distance = currentMonth - month + (currentYear - year) * 12;
            if(distance > -1 && distance < 36){
                linearLayout.addView(getMonthView(year, month, vaildColor, listener), lp);
            }else{
                linearLayout.addView(getMonthView(year, month, invaildColor, null), lp);
            }

        }
        return linearLayout;
    }

    private View getMonthView(int year, int month, int color, View.OnClickListener clickListener) {
        TextView monthView = new TextView(this);
        monthView.setTextColor(color);
        monthView.setGravity(Gravity.CENTER);
        monthView.setText(month+"月");
        monthView.setTag(month);
        monthView.setTag(R.id.tag_type, year);
        if (clickListener != null) {
            monthView.setOnClickListener(clickListener);
        }
        return monthView;
    }

    private View getLineView(){
        View view = new View(this);
        view.setBackgroundResource(R.color.cjx_divider_color);
        return view;
    }
}
