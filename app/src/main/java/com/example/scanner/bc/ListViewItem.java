package com.example.scanner.bc;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private String prod_name;
    private int price;
    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProd_name() {
        return this.prod_name;
    }

    public int getPrice() {
        return this.price;
    }

}
