package net.guanjiale.lmq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;

/**
 * Created by cjx on 2016/12/19.
 */

public class LoginActivity extends BaseActivity {
    EditText accView, pwdView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        setToolBar(R.drawable.back_icon, null, R.string.button_login);

        findViewById();
        sharedPreferences = getSharedPreferences(getString(R.string.cjx_preference), MODE_PRIVATE);
        String username = sharedPreferences.getString(MyApplication.PREFERENCE_ACCOUNT, null);
        String password = sharedPreferences.getString(MyApplication.PREFERENCE_PASSWORD, null);
        if (username != null) {
            accView.setText(username);
            accView.setSelection(username.length());
            pwdView.setText(password);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    private void findViewById() {
        accView = (EditText) findViewById(R.id.login_name);
        pwdView = (EditText) findViewById(R.id.login_password);
    }

    public void onClick(View v) {
        final String phone = accView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.login_name_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        final String password = pwdView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {

            @Override
            public Object parser(ResponseBean response) {
                MyApplication.getInstance().setUser(response.datum);
                return response.message;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                if(MyApplication.getInstance().isLogin()){
                    Toast.makeText(LoginActivity.this, (String)result, Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent(CustomApplication.ACTION_LOGIN));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MyApplication.PREFERENCE_ACCOUNT, phone);
                    editor.putString(MyApplication.PREFERENCE_PASSWORD, password);
                    editor.apply();
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "获取登录信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "member/login", "username", phone, "password", password);
    }

    public void register(View view) {
        // 注册
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
    }

    public void findpassword(View view) {
        // 找回密码
        Intent intent = new Intent(this, FindPasswordActivity.class);
        intent.setAction(getString(R.string.find_password));
        startActivity(intent);
    }
}
