package com.ipd10.frienddb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Created by 1795661 on 1/17/2018.
 */
//sqliteopenhelper: create db if doesnt exist and upgrade DB
// upgrade is example to alter table (adding column), add table
// upgrade use database version
public class Database extends SQLiteOpenHelper {
    private static final String TAG = Database.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "friendsDB.sqlite";

    private static final String TABLE_FRIENDS = "friends";
    private static final String FRIENDS_ID = "_id";
    private static final String FRIENDS_NAME = "name";

    private static final String TABLE_FRIENDS_CREATE = "CREATE TABLE " + TABLE_FRIENDS
            + " ( " + FRIENDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FRIENDS_NAME + " TEXT NOT NULL)";

    public Database(Context context){
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_FRIENDS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.wtf(TAG, "Fatal Error: Database.onUpgrade() not implemented" );
        throw new InvalidParameterException("Fatal Error: Database.onUpgrade() not implemented");
        //NEVER DELETE / DROP TABLES WHEN UPGRADING !!!
        //DO THIS IF YUO DON'T WANT TO GET FIRED
        //check the bitbucket
    }

    public long addFriend(Friend friend){
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FRIENDS_NAME, friend.name);
            long insertId = database.insert(TABLE_FRIENDS, null, values);
            return insertId;
        } finally {
            if (database !=null){
                database.close();
            }
        }

    }


    public void deleteFriend(long id){
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            System.out.println("Comment deleted with id: " + id);
            database.delete(TABLE_FRIENDS, FRIENDS_ID + " = " + id, null);
        } finally {
            if (database !=null){
                database.close();
            }
        }
    }

    private String[] FRIENDS_ALL_COLUMNS = { FRIENDS_ID, FRIENDS_NAME};
    public ArrayList<Friend> getAllFriends(){
        SQLiteDatabase database = null;
        Cursor cursor = null;
        ArrayList<Friend> result = new ArrayList<>();
        try{
            database= getWritableDatabase();
            cursor = database.query(TABLE_FRIENDS, FRIENDS_ALL_COLUMNS, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Friend friend = new Friend(id, name);
                result.add(friend);
                cursor.moveToNext();
            }
            //make sure close the cursor
            cursor.close();
        } finally {
            if (database !=null){
                database.close();
            }
            if (cursor !=null){
                cursor.close();
            }
        }
        return result;
    }

    public void updateFriend(Friend friend){
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FRIENDS_ID, friend._id);
            contentValues.put(FRIENDS_NAME, friend.name);
            database.update(TABLE_FRIENDS, contentValues, FRIENDS_ID + " = ?", new String[]{friend._id + ""});
        } finally {
            if (database !=null){
                database.close();
            }
        }
    }
}
