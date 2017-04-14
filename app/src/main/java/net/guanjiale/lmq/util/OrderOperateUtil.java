package net.guanjiale.lmq.util;

import android.content.Intent;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.dialog.TipDialog;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.activity.PayActivity;

/**
 * Created by cjx on 17-3-8.
 * 工单操作工具
 */

public class OrderOperateUtil {
    private TipDialog cancelDialog, comfirmDialog;
    private CancelClickListener cancelListener;
    private ComfirmCliskListener comfirmListener;

    public OrderOperateUtil() {
    }

    // 取消订单
    private void cancelOrder(final CancelInterface cancelInterface, BaseActivity activity, String sn) {
        cancelInterface.beforeCancel();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.message;
            }

            @Override
            public void success(Object result) {
                cancelInterface.cancelSuccess((String) result);
            }

            @Override
            public void error() {
                cancelInterface.cancelFail();
            }
        };
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "order/cancel", "sn", sn);
    }

    // 确认收货
    private void comfirmOrder(final CancelInterface cancelInterface, BaseActivity activity, String sn) {
        cancelInterface.beforeCancel();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.message;
            }

            @Override
            public void success(Object result) {
                cancelInterface.cancelSuccess((String) result);
            }

            @Override
            public void error() {
                cancelInterface.cancelFail();
            }
        };
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "order/complete", "sn", sn);
    }

    public void goToPay(BaseActivity activity, String id, String price) {
        Intent payIntent = new Intent(activity, PayActivity.class);
        payIntent.putExtra("id", id);
        payIntent.putExtra("price", price);
        // String sn = jsonObject.getString("orderSn");
        payIntent.putExtra("title", "订单支付");
        activity.startActivity(payIntent);
    }

    // 提示取消订单的对话框
    public void showCancelTipDialog(CancelInterface cancelInterface, BaseActivity activity, String sn) {
        if (cancelDialog == null) {
            cancelDialog = new TipDialog(activity);
            cancelDialog.setText(R.string.tip_title, R.string.cancel_order_tip, R.string.button_sure,
                    R.string.button_cancel);
            cancelListener = new CancelClickListener(cancelInterface, activity, sn);
            cancelDialog.setTipComfirmListener(cancelListener);
        } else {
            cancelListener.setParams(cancelInterface, activity, sn);
        }
        cancelDialog.show();
    }

    public void showComfirmDialog(CancelInterface cancelInterface, BaseActivity activity, String sn) {
        if(comfirmDialog == null){
            comfirmDialog = new TipDialog(activity);
            comfirmDialog.setText(R.string.tip_title, R.string.comfirm_order_tip, R.string.button_sure,
                    R.string.button_cancel);
            comfirmListener = new ComfirmCliskListener(cancelInterface, activity, sn);
            comfirmDialog.setTipComfirmListener(comfirmListener);
        } else {
            comfirmListener.setParams(cancelInterface, activity, sn);
        }
        comfirmDialog.show();
    }

    private class CancelClickListener implements TipDialog.ComfirmListener {
        CancelInterface cancelInterface;
        BaseActivity activity;
        String sn;

        CancelClickListener(CancelInterface cancelInterface, BaseActivity activity, String sn) {
            setParams(cancelInterface, activity, sn);
        }

        void setParams(CancelInterface cancelInterface, BaseActivity activity, String sn) {
            this.sn = sn;
            this.cancelInterface = cancelInterface;
            this.activity = activity;
        }

        @Override
        public void comfirm() {
            cancelDialog.dismiss();
            cancelOrder(cancelInterface, activity, sn);
        }

        @Override
        public void cancel() {
            cancelDialog.dismiss();
        }
    }

    private class ComfirmCliskListener implements TipDialog.ComfirmListener {
        CancelInterface comfirmInterface;
        BaseActivity activity;
        String sn;
        ComfirmCliskListener(CancelInterface cancelInterface, BaseActivity activity, String sn) {
            setParams(cancelInterface, activity, sn);
        }

        void setParams(CancelInterface cancelInterface, BaseActivity activity, String sn) {
            this.sn = sn;
            this.comfirmInterface = cancelInterface;
            this.activity = activity;
        }

        @Override
        public void comfirm() {
            comfirmDialog.dismiss();
            comfirmOrder(comfirmInterface, activity, sn);
        }

        @Override
        public void cancel() {
            comfirmDialog.dismiss();
        }
    }

    public interface CancelInterface {
        void beforeCancel();

        void cancelSuccess(String message);

        void cancelFail();
    }
}
