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
    private int coin;
    private int gold;
    private int status;
    private int level;
    private String appellation;
    private boolean isAuto = false;

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

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public boolean isIsAuto() {
        return isAuto;
    }

    public void setIsAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public EsObject toEsObject() {
        EsObject es = new EsObject();
        es.setBoolean("isAuto", isAuto);
        es.setString(Field.UserName.getName(), username);
        es.setString(Field.Email.getName(), email);
        es.setInteger(Field.Gold.getName(), gold);
        es.setInteger(Field.Level.getName(), level);
        es.setString(Field.Appellation.getName(), appellation);
        es.setInteger(Field.Coin.getName(), coin);
        return es;
    }
}
