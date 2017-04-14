package net.guanjiale.lmq.server;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.ServerBean;

/**
 * Created by cjx on 2016/12/22.
 * 服务详情
 */

public class ServerDetailActivity extends BaseActivity {
    WebView wv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_detail);
        Intent intent = getIntent();
        setToolBar(true, null, intent.getAction());

        loadData(intent.getStringExtra("type"), intent.getStringExtra("key"));
        registerReceiver(new IntentFilter(CustomApplication.ACTION_ORDER_CREATE));
    }

    // 收到广播回调
    @Override
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    @Override
    public void finish() {
        if(wv != null){
            wv.stopLoading();
            wv.clearCache(true);
            wv.clearHistory();
            wv.clearFormData();
            wv.removeAllViews();
        }
        super.finish();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        wv.destroy();
    }

    // 预约服务
    public void submitClick(View v){
        if(!MyApplication.getInstance().isLogin()){
            MyApplication.getInstance().startLogin();
            return ;
        }
        String type = getIntent().getStringExtra("type");
        Intent intent;
        if(type.equals("14")) { //送水服务
            intent = new Intent(this, OrderWaterActivity.class);
        }else{
            intent = new Intent(this, ServerOrderActivity.class);
            intent.putExtra("type", type);
        }
        intent.putExtra("key", getIntent().getStringExtra("key"));
        intent.setAction(getIntent().getAction());
        startActivity(intent);
    }

    private void loadData(final String type, String key) {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return JsonParser.getInstance().fromJson(response.datum, ServerBean.class);
            }

            @Override
            public void success(Object result) {
                findViewById(R.id.bottom_button).setVisibility(View.VISIBLE);
                if(result != null){
                    ServerBean sb = (ServerBean) result;
                    initWebView(sb.key);
                }
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/thirdPage", "type", type, "key", key);
    }

    private void initWebView(String url) {
        // 加载需要显示的网页
        wv = (WebView) findViewById(R.id.webview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wv.removeJavascriptInterface("searchBoxJavaBredge_");
            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
//        wv.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                pb.setProgress(progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                webTitle = title;
//            }
//        });

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("sms:")) {
                    // 针对webview里的短信注册流程，需要在此单独处理sms协议
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address", url.replace("sms:", ""));
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    ServerDetailActivity.this.startActivity(sendIntent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!wv.getSettings().getLoadsImagesAutomatically()) {
                    wv.getSettings().setLoadsImagesAutomatically(true);
                }
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= 19) {
            ws.setLoadsImagesAutomatically(true);
        } else {
            ws.setLoadsImagesAutomatically(false);
        }
        wv.loadUrl(url);
    }
}
