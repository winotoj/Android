package com.ipd10.frienddb;

/**
 * Created by 1795661 on 1/17/2018.
 */

public class Friend {
    public Friend(){

    }
    public Friend(long _id, String name) {
        this._id = _id;
        this.name = name;
    }

    long _id;
    String name;
    @Override
    public String toString(){
        return _id + ": " + name;
    }
}
