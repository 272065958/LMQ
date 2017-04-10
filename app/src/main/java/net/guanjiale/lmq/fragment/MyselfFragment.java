package net.guanjiale.lmq.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.dialog.TipDialog;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.activity.FindPasswordActivity;
import net.guanjiale.lmq.activity.LoginActivity;
import net.guanjiale.lmq.bean.UserBean;

/**
 * Created by cjx on 2016/8/22.
 */
public class MyselfFragment extends BaseFragment {
    TextView nameView;
    ImageView headView;
    UserBean currentUser;
    View logoutView, loginView;

    boolean isVisibleToUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_myself, null);
            findViewById();
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (view != null && isVisibleToUser) {
            checkLogin();
        }
    }

    private void findViewById() {
        nameView = (TextView) view.findViewById(R.id.user_name);
        headView = (ImageView) view.findViewById(R.id.user_head);
        loginView = view.findViewById(R.id.login_content);
        logoutView = view.findViewById(R.id.myself_logout);
        int width = CustomApplication.getInstance().getScreen_width();
        int height = (int) (281 * width / 750f);
        loginView.getLayoutParams().height = height;
        if (isVisibleToUser) {
            checkLogin();
        }
    }

    // 根据登录状态显示/隐藏控件
    public void checkLogin() {
        if(getActivity() == null){
            return;
        }
        UserBean user = ((CustomApplication)MyApplication.getInstance()).user;
        if (currentUser == user) {
            if(user == null){
                startLogin();
            }
            return;
        }
        currentUser = user;
        if (user == null) {
            logoutView.setVisibility(View.GONE);
            loginView.setClickable(true);
            nameView.setText(R.string.myself_login);

            headView.setImageResource(R.drawable.user_head);
            startLogin();
        } else {
            if(logoutView.getVisibility() == View.GONE){
                logoutView.setVisibility(View.VISIBLE);
            }
            loginView.setClickable(false);
            nameView.setText(currentUser.username);
            setHeader();
        }
    }

    private void startLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public void myselfOnClick(BaseActivity activity, View view) {
        switch (view.getId()) {
            case R.id.user_balance: // 余额
//                Intent balanceIntent = new Intent(activity, PropertyBalanceActivity.class);
//                startActivity(balanceIntent);
                Toast.makeText(getActivity(), "功能开放中...", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.user_order: // 订单
//                Intent orderIntent = new Intent(activity, MyOrderActivity.class);
//                startActivity(orderIntent);
//                break;
//            case R.id.user_coupon: // 优惠券
//                Intent couponIntent = new Intent(activity, MyCouponActivity.class);
//                startActivity(couponIntent);
//                break;
//            case R.id.myself_info: // 个人资料
//                Intent infoIntent = new Intent(activity, UserInfoActivity.class);
//                startActivity(infoIntent);
//                break;
            case R.id.myself_change_pwd: // 修改密码
                Intent pwdIntent = new Intent(activity, FindPasswordActivity.class);
                pwdIntent.setAction(getString(R.string.myself_change_pwd));
                startActivity(pwdIntent);
                break;
//            case R.id.myself_feedback: // 反馈
//                Intent feedbackIntent = new Intent(activity, FeedbackActivity.class);
//                startActivity(feedbackIntent);
//                break;
            case R.id.myself_server://联系客服
                showCallDialog(activity);
                break;
            case R.id.myself_logout: // 注销
                CustomApplication.getInstance().setUser(null);
                checkLogin();
                break;
            case R.id.login_content:
                startLogin();
                break;
        }
    }

    TipDialog callDialog;

    private void showCallDialog(BaseActivity activity) {
        if (callDialog == null) {
            callDialog = new TipDialog(activity);
            callDialog.setText("联系客服", "拨打客服电话400-930-2220",
                    getString(R.string.button_sure), getString(R.string.button_cancel)).setTipComfirmListener(
                    new TipDialog.ComfirmListener() {
                        @Override
                        public void comfirm() {
                            callDialog.dismiss();
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:400-930-2220"));
                            startActivity(callIntent);
                        }

                        @Override
                        public void cancel() {
                            callDialog.dismiss();
                        }
                    });
        }
        callDialog.show();
    }

    public void setHeader() {
        Tools.setHeadImage(getActivity(), headView, currentUser.avatar);
    }
}
