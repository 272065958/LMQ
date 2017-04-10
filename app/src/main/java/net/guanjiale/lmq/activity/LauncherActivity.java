package net.guanjiale.lmq.activity;

import android.os.Bundle;

import com.model.cjx.activity.BaseActivity;

import net.guanjiale.lmq.R;

/**
 * Created by cjx on 2017/2/28.
 */
public class LauncherActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        timeStart();
    }

    @Override
    public void onBackPressed() {

    }

    private void timeStart() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }.start();
    }
}
