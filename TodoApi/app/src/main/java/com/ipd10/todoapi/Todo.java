package com.ipd10.todoapi;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by minx1 on 2018-01-24.
 */

public class Todo {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public Todo(){}
    public Todo(long id, String task, boolean isDone, Date dueDate){
        this.id = id;
        this.task = task;
        this.isDone = isDone;
        this.dueDate = dueDate;
    }
    long id;
    String task;
    boolean isDone;
    Date dueDate;


    @Override
    public String toString(){
        return String.format("ID %d %s by %s is %s", id, task, dateFormat.format(dueDate), isDone?"Done":"Pending" );
    }
}

