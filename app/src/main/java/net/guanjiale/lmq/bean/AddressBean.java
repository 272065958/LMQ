package net.guanjiale.lmq.bean;

import java.io.Serializable;

/**
 * Created by cjx on 2016/12/22.
 */

public class AddressBean implements Serializable{
    public String id;
    public String consignee;
    public String phone;
    public String address;
    public boolean is_default;
    public String user_area;
}
