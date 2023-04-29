package com.eduard.eventsreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import entities.EventModel;
import entities.UserModel;

public class dbManager extends SQLiteOpenHelper {

    private static final String dbName = "reminderDataBase";
    private static final String TABLE_USERS = "users";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String TABLE_EVENTS = "events";
    public static final String EVENTNAME = "eventname";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String USERID = "userid";
    public dbManager(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryUsers = " CREATE TABLE " + TABLE_USERS + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT, " +
                EMAIL + " TEXT, " +
                PASSWORD + " TEXT)";
        db.execSQL(queryUsers);

        String queryEvents = " CREATE TABLE " + TABLE_EVENTS + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENTNAME + " TEXT, " +
                DATE + " TEXT, " +
                TIME + " TEXT, " +
                USERID + " INTEGER )";
        db.execSQL(queryEvents);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = " DROP TABLE IF EXISTS " + TABLE_EVENTS;
        db.execSQL(query);
        onCreate(db);
    }


    /** method for logging in
     *  Returns: 1 if the user exist in db, otherwise returns -1
     * */
    public int login(UserModel userModel){
        int id=-1;
        String [] str = new String[1];
        str[0]= userModel.getUsername();
        SQLiteDatabase db = getReadableDatabase();
        String queryLogin = "SELECT " + ID + " FROM " + TABLE_USERS + " WHERE " + USERNAME + " = ? ";
        Cursor cursor = db.rawQuery(queryLogin,str);
        if (cursor.moveToFirst()) {
            id=1;
        }
        return id;
    }


    /** method for creating an user in db
     * */
    public void register(UserModel user){
        ContentValues cv = new ContentValues();
        cv.put("username",user.getUsername());
        cv.put("email",user.getEmail());
        cv.put("password",user.getPassword());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USERS,null,cv);
        db.close();
    }


    /** method for creating an event in db
     * calling the insert method which returns the row ID of the newly inserted row, or -1 if an error occurred
     * returns a string "failed" if the result returned by insert method is -1,
     * otherwise returns a string "succesfully inserted"
     * */
    public String createEvent(EventModel eventModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENTNAME,eventModel.getEventName());
        cv.put(DATE,eventModel.getDate());
        cv.put(TIME,eventModel.getTime());
        cv.put(USERID, eventModel.getIduser());

        long result = db.insert(TABLE_EVENTS,null,cv);

        if(result==-1){
            return "Failed";
        }else {
            return "Succesfully inserted";
        }
    }


    /** method for getting the id of an user from db
     * returns the id of an user from db
     * */
    public int getUserId(UserModel userModel){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] str = new String[1];
        str[0] = userModel.getUsername();
        Cursor cursor = db.rawQuery("SELECT " + ID + " FROM " + TABLE_USERS + " WHERE USERNAME = ? ",str );
        cursor.moveToFirst();
        return  cursor.getInt(0);
    }


    /** method for getting the events of an user with a particular id in db
     * */
    public List<EventModel> getEvents(int id){
        List<EventModel> eventList = new ArrayList<>();
        String query = "select * from " + TABLE_EVENTS + " where " + USERID + " = " + id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(0);
                String eventname = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                int iduser = cursor.getInt(4);
                EventModel eventModel = new EventModel(id,eventname,date,time,iduser);
                eventList.add(eventModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }


    /** method to delete an event from db
     * */
    public boolean deleteEvent(EventModel eventModel){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " DELETE FROM " + TABLE_EVENTS + " WHERE " + ID + " = " + eventModel.getId();
        Cursor cursor = db.rawQuery(query,null);
        return cursor.moveToFirst();
    }
}
