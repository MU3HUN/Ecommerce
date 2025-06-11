package com.Ecommerce.code.domain;

public class QrCodeVO extends QrCode {
    private String userName; // 批次关联的用户名

    // Getter和Setter
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}