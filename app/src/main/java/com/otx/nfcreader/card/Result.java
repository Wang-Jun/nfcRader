package com.otx.nfcreader.card;

import java.util.Objects;

/**
 * Created by wangjun on 2017/2/10.
 */
public class Result {
    public enum CardType {
        PbocCard,OctopusCard
    }
    CardType type;
    Object res;

    public Result(CardType type, Object res){
        this.type=type;
        this.res=res;
    }

    public CardType getType() {
        return type;
    }

    public Object getRes() {
        return res;
    }
}
