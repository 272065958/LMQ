package net.guanjiale.lmq.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.http.HttpUtils;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.R;
import net.guanjiale.lmq.bean.ServerBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/3/2.
 * 所有服务类
 */
public class ServerListActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);
        setToolBar(R.drawable.back_icon, null, getIntent().getAction());
        loadData();
    }

    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public Object parser(ResponseBean response) {
                return JsonParser.getInstance().fromJson(response.datum, new TypeToken<ArrayList<ServerBean>>() {
                }.getType());
            }

            @Override
            public void success(Object result) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                if(result != null){
                    displayData((ArrayList<ServerBean>)result);
                }

            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        Intent intent = getIntent();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "app/newSecondIndex", "type", intent.getStringExtra("type"),
                "key", intent.getStringExtra("key"));
    }

    private void displayData(ArrayList<ServerBean> serverBeans) {
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ServerAdapter(serverBeans, this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServerBean sb = (ServerBean) parent.getAdapter().getItem(position);
                Intent intent = new Intent(ServerListActivity.this, ServerSelectActivity.class);
                intent.putExtra("key", sb.key);
                intent.putExtra("type", sb.type);
                intent.setAction(sb.name);
                startActivity(intent);
            }
        });
        gridView.setVisibility(View.VISIBLE);
    }

    class ServerAdapter extends MyBaseAdapter {

        ServerAdapter(ArrayList<ServerBean> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_server, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            ServerBean sb = (ServerBean) getItem(position);
            ho.nameView.setText(sb.name);
            Tools.setImage(context, ho.iconView, sb.image);
        }

        class ViewHolder extends MyViewHolder {
            ImageView iconView;
            TextView nameView;
            ViewHolder(View v) {
                super(v);
                iconView = (ImageView) v.findViewById(R.id.server_icon);
                nameView = (TextView) v.findViewById(R.id.server_name);
            }
        }
    }
}
