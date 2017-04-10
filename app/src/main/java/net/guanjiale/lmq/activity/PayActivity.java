package net.guanjiale.lmq.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.util.PayResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjx on 2016/12/12.
 * 支付页面
 */
public class PayActivity extends BaseActivity {
    final String WEIXIN_PAY = "1", ALIPAY_PAY = "2";
    private static final int SDK_PAY_FLAG = 1;

    View payWeixinView, payAlipayView;
    TextView payPriceView, payTipView;

    protected String currentPayType;
    String id, sn; // 订单id和订单号

//    /**
//     * 支付宝支付业务：入参app_id
//     */
//    public static final String APPID = "2016072900118878";
//
//    /**
//     * 商户私钥，pkcs8格式
//     */
//    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKS/ZxyV261Xzv/S" +
//            "4VSbr2jd46q2qo1mC/9p+NUa0izTmXBJVl7AJFxwZBK7DSeb8vGvmHrsDg5hbukt" +
//            "Pb/R4ozxyg9DaWM+/mRd8if9cjjz6SPC09dL+sWXnWnOniKMb3/TqrgMZxLTlemK" +
//            "8fvbAXrFNRJxXTcKdBvzH8ocA+6FAgMBAAECgYEAjQb/2Ft8Oo3xN9Moasn+xRE0" +
//            "w70skHArkw//H0WfZxoXviQ5WFC4j5zyPYp7v01jEXsUVx1dBvhm/hppQpwDhu3+" +
//            "Jp72l/lnF8cZgLZUG0HDSnRaBFbbHsd2rk0fIOffJcHQ5yfrTmZa75DS3AJWmHTB" +
//            "QR0zHwdcNWh8VqcjZWECQQDSJYJZReHFG6+TqW2o/yza9NFdJXfqwvQ828YaLPLs" +
//            "+x/r14uAo4K/ftPUPZe+diFY+OodoOCDmBudI6q8etpJAkEAyLH6Jd63QOCR3yem" +
//            "a+Hbd9NJsmG9WIE6RgKPqN0DqA0pTXCTvSrlHX96O6Dxh1feRQP/tBPSlZMZyBtn" +
//            "E72SXQJAAeRQjhkw9SNQq1WlJRZXAwmdMOd7cuOaa0nXOLka3sNMAEWKfff631p2" +
//            "8bw0N4S8vB5RjT67hrPB2+JMUTIR8QJBAJnNSxaah3WApEmP6Unj3H0HvwKlRKX+" +
//            "4tmkPNP8DEFNVMmz7ISbytnAR7xKqs492Girl9o5Frjp8mgYF/ZY4UECQQC54jOx" +
//            "JW6tD3RmkUVI1LAC7MjK5e3M5XqWN/Jyu79GInkdT+z42pfgylHJVarLNzCS2SYU" +
//            "3Ndnd94CGmwmDRh6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        setToolBar(R.drawable.back_icon, null, R.string.product_pay_title);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sn = intent.getStringExtra("sn");
        payAlipayView = findViewById(R.id.pay_alipay);
        payWeixinView = findViewById(R.id.pay_weixin);
        payPriceView = (TextView) findViewById(R.id.pay_price);
        payTipView = (TextView) findViewById(R.id.pay_tip);
        payPriceView.setText(String.format(getString(R.string.price_format), intent.getStringExtra("price")));
        payTipView.setText(intent.getStringExtra("title"));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_weixin:
                if (!v.isSelected()) {
                    if (api == null) {
                        api = WXAPIFactory.createWXAPI(this, CustomApplication.WEIXIN_APPID);
                    }
                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                    if (!isPaySupported) {
                        showToast("当前微信版本不支持支付功能");
                        return;
                    }
                    currentPayType = WEIXIN_PAY;
                    payAlipayView.setSelected(false);
                    v.setSelected(true);
                }
                break;
            case R.id.pay_alipay:
                if (!v.isSelected()) {
                    currentPayType = ALIPAY_PAY;
                    payWeixinView.setSelected(false);
                    v.setSelected(true);
                }
                break;
            case R.id.pay_button:
                if (currentPayType != null) {
                    pay();
//                    Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID);
//                    String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//                    String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
//                    final String orderInfo = orderParam + "&" + sign;
//                    alipayPay(orderInfo);
                }else{
                    showToast("请选择支付方式");
                }
                break;
        }
    }

    private void pay() {
        // 获取订单信息回调
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.datum;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                switch (currentPayType) {
                    case WEIXIN_PAY: // 微信支付
                        weixinPay((String) result);
                        break;
                    case ALIPAY_PAY: // 支付宝支付
                        alipayPay((String) result);
                        break;
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "pay/appPay", "payWay", currentPayType, "id", id);
    }

    @Override
    protected void onBroadcastReceive(Intent intent) {
        super.onBroadcastReceive(intent);
        checkPayResult();
    }

    // 访问后台是否成功
    private void checkPayResult() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.message;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                showToast((String) result);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "ec_order/getPaymentResult", "sn", sn, "payWay", currentPayType);
    }

    /************************************/
    /*************  支付宝  *************/
    /************************************/
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showToast("正在查询支付结果");
                        checkPayResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 支付宝支付
     */
    protected void alipayPay(final String orderInfo) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /************************************/
    /**************  微信  **************/
    /************************************/

    private IWXAPI api;

    private void weixinPay(String json) {
        if (TextUtils.isEmpty(json)) {
            showToast("调用微信失败");
            return;
        }
        HashMap<String, String> map = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {
        }.getType());
        PayReq req = new PayReq();
        req.appId = CustomApplication.WEIXIN_APPID;
        req.partnerId = map.get("partnerid"); // 商户id
        req.prepayId = map.get("prepayid");
        req.nonceStr = map.get("noncestr");
        req.timeStamp = map.get("timestamp"); // 时间
        req.packageValue = map.get("package");
        req.sign = map.get("sign");
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }

}
