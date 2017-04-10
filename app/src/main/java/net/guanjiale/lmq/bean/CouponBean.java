package net.guanjiale.lmq.bean;

import java.io.Serializable;

/**
 * Created by cjx on 2016/12/27.
 */
public class CouponBean implements Serializable{
    public String end_date;
    public String name;
    public String id;
    public String price_expression;
    public String price;

    public void initPrice(){
        price = price_expression.replace("price - ", "");
    }
}
