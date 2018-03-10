package com.ipd10.tododb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 1795661 on 1/17/2018.
 */

public class Database extends SQLiteOpenHelper {
    private static final String TAG = Database.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "todosDB.sqlite";
    private static final String TABLE_TODOS = "todos";
    private static final String TODOS_ID = "_id";
    private static final String TODOS_TASK = "task";
    private static final String TODOS_DUEDATE = "dueDate";
    private static final String TODOS_ISDONE = "isDone";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] TODOS_ALL_COLUMNS = { TODOS_ID, TODOS_TASK, TODOS_ISDONE, TODOS_DUEDATE};

    private static final String TABLE_TODOS_CREATE = "CREATE TABLE " + TABLE_TODOS
            + " ( " + TODOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TODOS_TASK + " TEXT NOT NULL, " + TODOS_ISDONE + " INTEGER NOT NULL, " + TODOS_DUEDATE + " TEXT NOT NULL)";

    public Database(Context context){
        //FIX-ME: ADD 5TH PARAMETER - DATABASEERRORHANDLER TO HANDLE PROBLEMS
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_TODOS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.wtf(TAG, "Fatal Error: Database.onUpgrade() not implemented" );
        throw new InvalidParameterException("Fatal Error: Database.onUpgrade() not implemented");
        //NEVER DELETE / DROP TABLES WHEN UPGRADING !!!
        //DO THIS IF YUO DON'T WANT TO GET FIRED
        //check the bitbucket
    }

    //GetTodo
    public Todo getTodo(long id) throws DataStorageException {
        Cursor cursor;
        SQLiteDatabase database = null;
        Todo todo= null;
        try{
            database= getWritableDatabase();
            cursor = database.query(TABLE_TODOS, TODOS_ALL_COLUMNS, TODOS_ID + " =? ",new String[] {id +""}, null, null, null);
            if(cursor!=null && cursor.moveToFirst()) {
                todo = new Todo(id, cursor.getString(1), cursor.getInt(2) !=0, dateFormat.parse(cursor.getString(3)));
                cursor.close();
            }

            }catch(SQLException |ParseException ex){
            throw new DataStorageException(ex);
        } finally {
            if (database !=null){
                database.close();
            }
        }
        return todo;
    }

    //Get All todos

    public ArrayList<Todo> getAllTodos() throws DataStorageException {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        ArrayList<Todo> result = new ArrayList<>();
        try{
            database= getWritableDatabase();
            cursor = database.query(TABLE_TODOS, TODOS_ALL_COLUMNS, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int id = cursor.getInt(0);
                String task = cursor.getString(1);
                boolean isDone = cursor.getInt(2) !=0 ;
                Date dueDate = dateFormat.parse(cursor.getString(3));
                Todo todo = new Todo(id, task, isDone, dueDate);
                result.add(todo);
                cursor.moveToNext();
            }

            //make sure close the cursor
        }catch(SQLException | ParseException ex){
            throw new DataStorageException(ex);
        }

        finally {
            if (database !=null){
                database.close();
            }
            if (cursor !=null){
                cursor.close();
            }
        }
        return result;
    }
    //Add Todo
    public long addTodo(Todo todo) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TODOS_TASK, todo.task);
            values.put(TODOS_ISDONE, todo.isDone ? 1 : 0);
            values.put(TODOS_DUEDATE, dateFormat.format(todo.dueDate));
            return database.insert(TABLE_TODOS, null, values);

        }catch(SQLException ex) {
            throw new DataStorageException(ex);
        }
        finally {
            if (database !=null){
                database.close();
            }
        }

    }
    //Edit Todo
    public void updateTodo(Todo todo) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TODOS_ID, todo._id);
            contentValues.put(TODOS_TASK, todo.task);
            contentValues.put(TODOS_ISDONE, todo.isDone ? 1 : 0);
            contentValues.put(TODOS_DUEDATE, dateFormat.format(todo.dueDate));
            database.update(TABLE_TODOS, contentValues, TODOS_ID + " = ?", new String[]{todo._id + ""});
        }catch(SQLException ex) {
            throw new DataStorageException(ex);
        }
        finally {
            if (database !=null){
                database.close();
            }
        }
    }
    //Delete Todo
    public void deleteTodo(long id) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            System.out.println("Comment deleted with id: " + id);
            database.delete(TABLE_TODOS, TODOS_ID + " = " + id + "", null);
        }
        catch(SQLException ex) {
            throw new DataStorageException(ex);
        }finally {
            if (database !=null){
                database.close();
            }
        }
    }

}
