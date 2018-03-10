package com.ipd10.friendsapi;

/**
 * Created by 1795693 on 1/23/2018.
 */

public class Friend {
    long id;
    String name;
    int age;

    public Friend(){}
    public Friend(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("%d: %s is %d y/o", id, name, age);
    }
}
