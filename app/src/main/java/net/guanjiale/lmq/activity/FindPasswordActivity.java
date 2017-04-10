package net.guanjiale.lmq.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.model.cjx.activity.BaseGetCodeActivity;
import com.model.cjx.component.GetCodeView;

import net.guanjiale.lmq.R;

/**
 * Created by cjx on 2017/1/16.
 */
public class FindPasswordActivity extends BaseGetCodeActivity {
    EditText passwordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        setToolBar(R.drawable.back_icon, null, getIntent().getAction());
        type = "reset";
        findViewById();
    }

    @Override
    protected void requestCode(String phone) {

    }

    private void findViewById(){
        phoneView = (EditText) findViewById(R.id.register_phone);
        codeView = (EditText) findViewById(R.id.register_code);
        passwordView = (EditText) findViewById(R.id.register_password);
        getCodeView = (GetCodeView) findViewById(R.id.button_get_code);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register: // 修改
                submit();
                break;
            case R.id.button_get_code: // 获取验证码
                getCode();
                break;
        }
    }

    private void submit() {
        // 判断验证码
        if(!checkCode()){
            return ;
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
