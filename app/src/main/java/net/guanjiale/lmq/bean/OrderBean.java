package net.guanjiale.lmq.bean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/8.
 */
public class OrderBean {
    public String creation_date;
    public String id;
    public String sn;
    public int order_status; // 2 : 已完成
    public int payment_status;  // 0 : 待付款
    public int shipping_status;  // ??
    public String amount;
    public ArrayList<OrderItemBean> itemList;

}
