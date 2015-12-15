package com.estimote.examples.demos.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TK on 2015/10/26.
 */
public class ListViewDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "listview_database";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "listview_table";
    private final static String FEILD_ID = "_id";
    private final static String FEILD_TEXT = "item_text";
    private String sql =
            "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+
                    FEILD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    FEILD_TEXT+" TEXT"+
                    ")";
    private SQLiteDatabase database;
    public ListViewDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }
    public Cursor select(){
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
    public void insert(String itemText){
        ContentValues values = new ContentValues();
        values.put(FEILD_TEXT, itemText);
        database.insert(TABLE_NAME, null, values);
    }
    public void delete(int id){
        database.delete(TABLE_NAME, FEILD_ID + "=" + Integer.toString(id), null);
    }
    public void update(int id, String itemText){
        ContentValues values = new ContentValues();
        values.put(FEILD_TEXT, itemText);
        database.update(TABLE_NAME, values, FEILD_ID + "=" + Integer.toString(id), null);
    }
    public void close(){
        database.close();
    }
}
