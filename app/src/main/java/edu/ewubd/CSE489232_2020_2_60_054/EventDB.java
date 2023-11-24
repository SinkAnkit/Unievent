package edu.ewubd.CSE489232_2020_2_60_054;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDB extends SQLiteOpenHelper {

    public EventDB(Context context) {
        super(context, "EventDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE events  ("
                + "ID TEXT PRIMARY KEY,"
                + "title TEXT NOT NULL,"
                + "place TEXT NOT NULL,"
                + "datetime INT,"
                + "capacity INT,"
                + "budget REAL,"
                + "email TEXT,"
                + "phone TEXT,"
                + "description TEXT,"
                + "type TEXT,"
                + "reminder INT"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
        // db.execSQL("ALTER table my_table  ......");
    }
    public void insertEvent(String ID, String title, String place, long datetime, int capacity, double budget, String email, String phone, String description, String type, long reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("title", title);
		cols.put("place", place);
        cols.put("datetime", datetime);
        cols.put("type", type);
        cols.put("capacity", capacity);
        cols.put("budget", budget);
        cols.put("email", email);
        cols.put("phone", phone);
        cols.put("description", description);
        cols.put("reminder", reminder);
        db.insert("events", null ,  cols);
        db.close();
    }

    /*
    Use hashmap (key, value will be matched) to custom updating feature, for updating particular data
    * */
    public void updateEvent(String ID, String title, String place, long datetime, int capacity, double budget, String email, String phone, String description, String type, long reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("place", place);
        values.put("datetime", datetime);
        values.put("type", type);
        values.put("capacity", capacity);
        values.put("budget", budget);
        values.put("email", email);
        values.put("phone", phone);
        values.put("description", description);
        values.put("reminder", reminder);
        db.update("events", values, "ID=?", new String[ ] {ID} );
        db.close();
    }
    public void deleteEvent(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("events", "ID=?", new String[ ] {ID} );
        db.close();
    }

    public Cursor selectEvents(String query) {
        /*
        * query = sel
        * */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}