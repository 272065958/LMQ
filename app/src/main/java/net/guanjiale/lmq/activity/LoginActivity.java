package net.guanjiale.lmq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.model.cjx.activity.BaseActivity;

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

        setToolBar(true, null, R.string.button_login);

        findViewById();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
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
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
