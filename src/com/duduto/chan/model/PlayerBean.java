/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.model;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.duduto.chan.enums.Field;

/**
 *
 * @author Blacker
 */
public class PlayerBean {

    private int pId;
    private String username;
    private String password;
    private String email;
    private String timeRegister;
    private int fakeMoney;
    private int money;
    private int status;

    public PlayerBean() {
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeRegister() {
        return timeRegister;
    }

    public void setTimeRegister(String timeRegister) {
        this.timeRegister = timeRegister;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFakeMoney() {
        return fakeMoney;
    }

    public void setFakeMoney(int fakeMoney) {
        this.fakeMoney = fakeMoney;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public EsObject toEsObject() {
        EsObject es = new EsObject();
        es.setString(Field.UserName.getName(), username);
        es.setString(Field.Email.getName(), email);
        es.setInteger(Field.Money.getName(), money);
        es.setInteger(Field.FakeMoney.getName(), fakeMoney);
        return es;
    }
}
