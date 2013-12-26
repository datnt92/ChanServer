package com.netgame.database;

public class UserObject {

    private String name;
    private String password;
    private String sex;
    private Long money;
    private String avatar;
    public String frame;
    public Integer isMonster;
    private String ipAddress = "";
    public int email_verify;
    public int sms_verify;
    public int money_verify;
    public int group;
    public boolean isAuto = false;
    public int botIn;
    public int botOut;

    public boolean doesPasswordMatch(String pwd) {
        return password.equals(pwd);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return this.sex;
    }

    public void setMoney(Long value) {
        this.money = value;
    }

    public Long getMoney() {
        return this.money;
    }

    public void setAvatar(String value) {
        this.avatar = value;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setIP(String value) {
        this.ipAddress = value;
    }

    public String getIP() {
        return this.ipAddress;
    }
}
