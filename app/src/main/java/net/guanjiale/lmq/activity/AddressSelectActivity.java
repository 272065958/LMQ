package net.guanjiale.lmq.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.MyApplication;
import com.model.cjx.activity.BaseListActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.dialog.TipDialog;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.AddressBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/22.
 * 选择收货地址
 */

public class AddressSelectActivity extends BaseListActivity implements AdapterView.OnItemClickListener {

    CustomApplication app;
    AddressAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        app = (CustomApplication) MyApplication.getInstance();
        super.onCreate(savedInstanceState);
        setToolBar(true, null, R.string.server_address);
        setListViweDivider(ContextCompat.getDrawable(this, R.color.cjx_background_color),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadData();
        }
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList list) {
        if(adapter == null){
            adapter = new AddressAdapter(list, this);
        }
        return adapter;
    }

    @Override
    protected void loadData() {
        if (app.user != null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        HttpUtils.getInstance().postEnqueue(this, getMyCallbackInterface(new TypeToken<ArrayList<AddressBean>>() {
        }.getType()), "receiver/index", "userid", app.user.id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddressBean ab = (AddressBean) adapter.getItem(position);
        Intent data = new Intent();
        data.putExtra("address", ab);
        setResult(RESULT_OK, data);
        finish();
    }

    public void addClick(View view) {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("title", getString(R.string.receive_address_add));
        startActivityForResult(intent, 1);
    }

    class AddressAdapter extends MyBaseAdapter {
        public AddressAdapter(ArrayList list, Activity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_address, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            AddressBean ab = (AddressBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            if (ab.is_default) {
                ho.autoView.setSelected(true);
            } else {
                ho.autoView.setSelected(false);
            }
            ho.nameView.setText(ab.consignee);
            ho.phoneView.setText(ab.phone);
            ho.projectView.setText(ab.address);
            ho.autoView.setTag(ab);
            ho.deleteView.setTag(ab);
            ho.updateView.setTag(ab);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            View autoView, deleteView, updateView;
            TextView nameView, phoneView, projectView;

            public ViewHolder(View v) {
                super(v);
                nameView = (TextView) v.findViewById(R.id.property_name);
                phoneView = (TextView) v.findViewById(R.id.property_phone);
                projectView = (TextView) v.findViewById(R.id.property_location);
                autoView = v.findViewById(R.id.address_auto);
                updateView = v.findViewById(R.id.address_update);
                updateView.setOnClickListener(this);
                deleteView = v.findViewById(R.id.address_delete);
                deleteView.setOnClickListener(this);
                autoView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                AddressBean ab = (AddressBean) v.getTag();
                switch (v.getId()) {
                    case R.id.address_delete:
                        showDeleteDialog(ab);
                        break;
                    case R.id.address_update:
                        updateAddress(ab);
                        break;
                    case R.id.address_auto:
                        autoAddress(ab);
                        break;
                }
            }
        }
    }

    // 删除收货地址
    private void deleteAddress(final AddressBean ab) {
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.message;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                showToast((String) result);
                loadData();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "receiver/delete", "receiverid", ab.id);
    }

    TipDialog deleteDialog;

    // 显示删除收货地址提示对话框
    private void showDeleteDialog(Object obj) {
        if (deleteDialog == null) {
            deleteDialog = new TipDialog(this);
            deleteDialog.setText(R.string.tip_title, R.string.activity_exit, R.string.button_sure, R.string.button_cancel)
                    .setTipComfirmListener(new TipDialog.ComfirmListener() {
                        @Override
                        public void comfirm() {
                            deleteDialog.dismiss();
                            deleteAddress((AddressBean) deleteDialog.getTag());
                        }

                        @Override
                        public void cancel() {
                            deleteDialog.dismiss();
                        }
                    });
        }
        deleteDialog.setTag(obj);
        deleteDialog.show();
    }

    private void updateAddress(AddressBean ab) {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("title", getString(R.string.button_update));
        intent.setAction(ab.id);
        intent.putExtra("name", ab.consignee);
        intent.putExtra("phone", ab.phone);
        intent.putExtra("address", ab.address);
        startActivityForResult(intent, 1);
    }

    private void autoAddress(final AddressBean ab) {
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return response.message;
            }

            @Override
            public void success(Object result) {
                dismissLoadDialog();
                showToast((String)result);
                for(Object obj : adapter.list){
                    AddressBean addressBean = (AddressBean) obj;
                    if(addressBean == ab){
                        addressBean.is_default = true;
                    }else{
                        addressBean.is_default = false;

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "receiver/setDefault", "userid",
                app.user.id, "receiverid", ab.id);
    }
}
