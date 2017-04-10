package net.guanjiale.lmq.bean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/12/30.
 */
public class CartBean {
    public boolean isSelect;
    public String name;
    public ArrayList<ShopItemBean> cartItems;

    public void click() {
        if (isSelect) {
            for (ShopItemBean sib : cartItems) {
                sib.isSelect = false;
            }
            isSelect = false;
        } else {
            for (ShopItemBean sib : cartItems) {
                sib.isSelect = true;
            }
            isSelect = true;
        }
    }
}
