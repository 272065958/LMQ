package net.guanjiale.lmq.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.model.cjx.activity.BaseActivity;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.fragment.MyselfFragment;
import net.guanjiale.lmq.fragment.ServerFragment;
import net.guanjiale.lmq.fragment.ShopCartFragment;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    ServerFragment sreverFragment;
    ShopCartFragment shopcartFragment;
    MyselfFragment myselfFragment;
    Fragment[] fragments;
    TextView[] itemTexts;
    View[] itemIcons;

    ViewPager viewPager;
    MainPagerAdapter adapter;

    int prevIndex; // 指定当前tab选中的位置

    long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, LauncherActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(viewPager == null){
            checkVersion();
            initView();
            IntentFilter filter = new IntentFilter();
            filter.addAction(CustomApplication.ACTION_LOGIN);
            filter.addAction(CustomApplication.ACTION_CART_BUY);
            filter.addAction(CustomApplication.ACTION_INFO_UPDATE);
            filter.addAction(CustomApplication.ACTION_HEAD_UPDATE);
            registerReceiver(filter);
        }
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        switch(intent.getAction()){
            case CustomApplication.ACTION_LOGIN:
                if(myselfFragment != null){
                    myselfFragment.checkLogin();
                }
                if(shopcartFragment != null){
                    shopcartFragment.onRefresh();
                }
                break;
            case CustomApplication.ACTION_INFO_UPDATE:
                if(myselfFragment != null){
                    myselfFragment.checkLogin();
                }
                break;
            case CustomApplication.ACTION_CART_BUY:
                if(shopcartFragment != null){
                    shopcartFragment.onRefresh();
                }
                break;
            case CustomApplication.ACTION_HEAD_UPDATE:
                if(myselfFragment != null){
                    myselfFragment.setHeader();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getBaseContext(),
                    getString(R.string.press_to_exit),
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    // 初始化首页各个对象
    private void initView() {
        final int page_count = 3;
        itemTexts = new TextView[page_count];
        itemTexts[0] = (TextView) findViewById(R.id.main_item_server_text);
        itemTexts[1] = (TextView) findViewById(R.id.main_item_shopcart_text);
        itemTexts[2] = (TextView) findViewById(R.id.main_item_myself_text);

        itemIcons = new View[page_count];
        itemIcons[0] = findViewById(R.id.main_item_server);
        itemIcons[1] = findViewById(R.id.main_item_shopcart);
        itemIcons[2] = findViewById(R.id.main_item_myself);

        viewPager = (ViewPager) findViewById(R.id.main_view);
        fragments = new Fragment[page_count];
        sreverFragment = new ServerFragment();
        shopcartFragment = new ShopCartFragment();
        myselfFragment = new MyselfFragment();
        fragments[0] = sreverFragment;
        fragments[1] = shopcartFragment;
        fragments[2] = myselfFragment;

        adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        itemIcons[0].setSelected(true);
        itemTexts[0].setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 主界面tab的点击回调
     *
     * @param v
     */
    public void tabClick(View v) {
        switch (v.getId()) {
            case R.id.main_server:
                setSelection(0);
                break;
            case R.id.main_shopcart:
                setSelection(1);
                break;
            case R.id.main_myself:
                setSelection(2);
                break;
        }
    }

    public void propertySelect(View v){

    }

    public void balanceClick(View view){

    }

    public void myselfOnClick(View view) {
        myselfFragment.myselfOnClick(this, view);
    }

    // 检测新版本
    private void checkVersion() {
//        try {
//            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
//            HttpUtils.getInstance().postEnqueueWithServer(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String body = response.body().string();
//                    final DatumResponse rb = JsonParser.getInstance().getDatumResponse(body);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (rb != null && rb.code == Code.SUCCESS) {
//                                // 删除缓存文件
//                                Type type = new TypeToken<LinkedTreeMap<String, Object>>() {
//                                }.getType();
//                                final LinkedTreeMap<String, Object> map = JsonParser.getInstance().fromJson(rb.datum, type);
//                                final DownloadDialog dialog = new DownloadDialog(MainActivity.this);
//                                dialog.setText("发现新版本", map.get("message").toString().replace(";", ";\n"),
//                                        getString(R.string.button_sure), map.get("url").toString()).show();
//                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                    @Override
//                                    public void onCancel(DialogInterface dialogInterface) {
//                                        dialog.onCancel();
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }, "http://oms.kamfat.net/api/version/check", "version", String.valueOf(pi.versionCode), "client", "android");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 滑动主页到指定位置
     *
     * @param position 要滑动的位置
     */
    private void setSelection(int position) {
        if (prevIndex == position) {
            return;
        }
        setCurrentTab(position);
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前选中的tab
     *
     * @param position
     */
    private void setCurrentTab(int position) {
        itemIcons[prevIndex].setSelected(false);
        itemTexts[prevIndex].setTextColor(ContextCompat.getColor(this, R.color.cjx_text_title_color));
        itemIcons[position].setSelected(true);
        itemTexts[position].setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        prevIndex = position;
    }

    class MainPagerAdapter extends FragmentPagerAdapter {
        Fragment[] l;
        int viewCount;
        int currentRefreshIndex = -1; // 当前要刷新的位置
        Fragment currentRefreshFragment; // 当前要替换的fragment
        FragmentManager fm;

        MainPagerAdapter(FragmentManager fm, Fragment[] l) {
            super(fm);
            this.fm = fm;
            this.l = l;
            viewCount = l.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            // 当前位置是刷新的位置时, 替换显示的fragment
            if (position == currentRefreshIndex) {
                currentRefreshIndex = -1;
                String fragmentTag = fragment.getTag();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                fragment = l[position];
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();
            }
            return fragment;
        }

        @Override
        public Fragment getItem(int i) {
            return l[i];
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == currentRefreshFragment) {
                currentRefreshFragment = null;
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }

        @Override
        public int getCount() {
            return viewCount;
        }

        /**
         * 通知viewpager刷新fragment
         *
         * @param index    要更新的位置
         * @param fragment 要更新的fragment
         */
        public void notifyDataSetChanged(int index, Fragment fragment) {
            currentRefreshIndex = index;
            currentRefreshFragment = this.l[index];
            this.l[index] = fragment;
            notifyDataSetChanged();
        }
    }
}
