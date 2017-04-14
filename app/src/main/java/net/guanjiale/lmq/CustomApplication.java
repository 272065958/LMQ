package net.guanjiale.lmq;

import android.content.Intent;
import android.content.SharedPreferences;

import com.model.cjx.MyApplication;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallback;
import com.model.cjx.util.JsonParser;

import net.guanjiale.lmq.activity.LoginActivity;
import net.guanjiale.lmq.bean.UserBean;

/**
 * Created by cjx on 17-3-14.
 */

public class CustomApplication extends MyApplication {

    public final static String ACTION_LOGIN = "action_login_omengo";
    public final static String ACTION_WEIXIN_PAY = "action_weixin_pay_omengo";
    public final static String ACTION_ORDER_CREATE = "action_order_create_omengo";
    public final static String ACTION_CART_BUY = "action_cart_buy_omengo";
    public final static String ACTION_INFO_UPDATE = "action_info_update_omengo";
    public final static String ACTION_HEAD_UPDATE = "action_head_update_omengo";
    public final static String ACTION_CART_COUNT_UPDATE = "action_cart_count_update_omengo";

    public final static String WEIXIN_APPID = "wxfe31593ab3199bdf";
    public UserBean user = null;

    @Override
    public void onCreate() {
        super.onCreate();
        HttpUtils.getInstance().setServerApiUri(BuildConfig.SERVER_API_URI);
    }

    @Override
    public void setUser(String data) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (data == null) {
            editor.remove("userInfo");
            user = null;
        } else {
            editor.putString("userInfo", data);
            login(data);
        }
        editor.apply();
    }

    @Override
    public void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void autoLogin(MyCallback.CustomCallback myCallback, String account, String pwd) {
        HttpUtils.getInstance().postEnqueue(myCallback, "member/login", "username", account, "password", pwd);
    }

    // 获取上次登录信息
    private void login(String body) {
        user = JsonParser.getInstance().fromJson(body, UserBean.class);
    }

    @Override
    public boolean isLogin() {
        return user != null;
    }

    @Override
    public int getBackRes() {
        return R.drawable.back_icon;
    }

    @Override
    public int getToolbarBg(){
        return R.drawable.toolbar_bg;
    }
}
