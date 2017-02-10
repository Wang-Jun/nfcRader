package com.otx.nfcreader.card;

/**
 * Created by wangjun on 2017/2/10.
 */
public class Record {
    String date;
    String time;
    String money;
    String o;
    String other;

    public Record(String date,String time,String money,String o,String other){
        this.date=date;
        this.time=time;
        this.money=money;
        this.o=o;
        this.other=other;
    }

    public String getDate() {
        return date;
    }

    public String getMoney() {
        return money;
    }

    public String getO() {
        return o;
    }

    public String getOther() {
        return other;
    }

    public String getTime() {
        return time;
    }
}
