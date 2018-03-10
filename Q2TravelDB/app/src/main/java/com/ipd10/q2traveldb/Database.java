package com.ipd10.q2traveldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 1795661 on 1/19/2018.
 */
enum sort {
    name,
    destination,
    departureDate
}

public class Database extends SQLiteOpenHelper {
    private static final String TAG = Database.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FILENAME = "travelsDB.sqlite";
    private static final String TABLE_TRAVELS = "travels";
    private static final String TRAVELS_ID = "_id";
    private static final String TRAVELS_NAME = "name";
    private static final String TRAVELS_DEPARTUREDATE = "departureDate";
    private static final String TRAVELS_DESTINATION = "destination";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] TRAVELS_ALL_COLUMNS = { TRAVELS_ID, TRAVELS_NAME, TRAVELS_DESTINATION, TRAVELS_DEPARTUREDATE};

    private static final String TABLE_TRAVELS_CREATE = "CREATE TABLE " + TABLE_TRAVELS
            + " ( " + TRAVELS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRAVELS_NAME + " TEXT NOT NULL, " + TRAVELS_DESTINATION + " TEXT NOT NULL, " + TRAVELS_DEPARTUREDATE + " TEXT NOT NULL)";

    public Database(Context context){
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_TRAVELS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.wtf(TAG, "Fatal Error: Database.onUpgrade() not implemented" );
        throw new InvalidParameterException("Fatal Error: Database.onUpgrade() not implemented");

    }
    //GetTravel
    public Travel getTravel(int id) throws DataStorageException {
        Cursor cursor;
        SQLiteDatabase database = null;
        Travel travel= null;
        try{
            database= getWritableDatabase();
            cursor = database.query(TABLE_TRAVELS, TRAVELS_ALL_COLUMNS, TRAVELS_ID + " =? ",new String[] {id +""}, null, null, null);
            if(cursor!=null && cursor.moveToFirst()) {
                travel = new Travel(id, cursor.getString(1), cursor.getString(2), dateFormat.parse(cursor.getString(3)));
                cursor.close();
            }

        }catch(SQLException |ParseException ex){
            throw new DataStorageException(ex);
        } finally {
            if (database !=null){
                database.close();
            }
        }
        return travel;
    }

    //Get All travels

    public ArrayList<Travel> getAllTravels(String eSort) throws DataStorageException {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        String orderBy;
        sort s = sort.valueOf(eSort);
        ArrayList<Travel> result = new ArrayList<>();
        if(s == sort.name){
            orderBy = "name";
        }else if(s == sort.destination){
            orderBy = "destination";
        }
        else{
            orderBy = "departureDate";
        }
        try{
            database= getWritableDatabase();
            cursor = database.query(TABLE_TRAVELS, TRAVELS_ALL_COLUMNS, null, null, null, null, orderBy);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String destination = cursor.getString(2);
                Date departureDate = dateFormat.parse(cursor.getString(3));
                Travel travel = new Travel(id, name, destination, departureDate);
                result.add(travel);
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
    //Add Travel
    public long addTravel(Travel travel) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TRAVELS_NAME, travel.name);
            values.put(TRAVELS_DESTINATION, travel.destination);
            values.put(TRAVELS_DEPARTUREDATE, dateFormat.format(travel.departureDate));
            return database.insert(TABLE_TRAVELS, null, values);

        }catch(SQLException ex) {
            throw new DataStorageException(ex);
        }
        finally {
            if (database !=null){
                database.close();
            }
        }

    }
    //Edit Travel
    public void updateTravel(Travel travel) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRAVELS_ID, travel.id);
            contentValues.put(TRAVELS_NAME, travel.name);
            contentValues.put(TRAVELS_DESTINATION, travel.destination);
            contentValues.put(TRAVELS_DEPARTUREDATE, dateFormat.format(travel.departureDate));
            database.update(TABLE_TRAVELS, contentValues, TRAVELS_ID + " = ?", new String[]{travel.id + ""});
        }catch(SQLException ex) {
            throw new DataStorageException(ex);
        }
        finally {
            if (database !=null){
                database.close();
            }
        }
    }
    //Delete Travel
    public void deleteTravel(int id) throws DataStorageException {
        SQLiteDatabase database = null;
        try{
            database= getWritableDatabase();
            System.out.println("Comment deleted with id: " + id);
            database.delete(TABLE_TRAVELS, TRAVELS_ID + " = " + id + "", null);
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

