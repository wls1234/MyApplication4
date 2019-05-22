package com.example.asus.myapplication;

public class RateItem {
    private int id;
    private String curname;
    private String curRate;

    public RateItem() {
        this.curname = "";
        this.curRate = "";

    }

    public RateItem(String curName, String curRate) {
        this.curname = curName;
        this.curRate = curRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curname;
    }

    public void setCurName(String curName) {
        this.curname = curName;
    }

    public String getCurRate() {
        return curRate;
    }

    public void setCurRate(String curRate) {
        this.curRate = curRate;
    }
}
