package com.ipd10.tododb;

import java.util.Date;

/**
 * Created by 1795661 on 1/17/2018.
 */

public class Todo {
    public Todo(long _id, String task, boolean isDone, Date dueDate){
    this._id = _id;
    this.task = task;
    this.isDone = isDone;
    this.dueDate = dueDate;
    }
    long _id;
    String task;
    boolean isDone;
    Date dueDate;

    public Todo(long id, String string, boolean b) {
    }

    @Override
    public String toString(){
        return String.format("ID %d %s by %s is %s", _id, task, Database.dateFormat.format(dueDate), isDone?"Done":"Pending" );
    }
}
