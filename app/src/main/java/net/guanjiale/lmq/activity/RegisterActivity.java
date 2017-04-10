package net.guanjiale.lmq.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.model.cjx.activity.BaseGetCodeActivity;
import com.model.cjx.component.GetCodeView;

import net.guanjiale.lmq.R;

/**
 * Created by cjx on 2016/12/19.
 * 注册界面
 */

public class RegisterActivity extends BaseGetCodeActivity {
    EditText nameView, passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setToolBar(R.drawable.back_icon, null, R.string.button_register);
        type = "register";
        findViewById();
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    @Override
    protected void requestCode(String phone){

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register: // 注册
                register();
                break;
            case R.id.button_get_code: // 获取验证码
                getCode();
                break;
        }
    }

    // 获取界面元素
    private void findViewById() {
        nameView = (EditText) findViewById(R.id.register_name);
        phoneView = (EditText) findViewById(R.id.register_phone);
        codeView = (EditText) findViewById(R.id.register_code);
        passwordView = (EditText) findViewById(R.id.register_password);
        getCodeView = (GetCodeView) findViewById(R.id.button_get_code);
    }

    private void register() {
        // 判断验证码
        if(!checkCode()){
            return ;
        }
        String name = nameView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.register_name_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        final String phone = phoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.register_phone_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        final String password = passwordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        finish();

    }

}
