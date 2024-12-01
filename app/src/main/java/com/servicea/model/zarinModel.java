package com.servicea.model;

/**
 * @author haniye94 .
 * @since on 11/7/2024.
 */
public class zarinModel {

    private String merchant_id;
    private int amount;
    private String authority;


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
