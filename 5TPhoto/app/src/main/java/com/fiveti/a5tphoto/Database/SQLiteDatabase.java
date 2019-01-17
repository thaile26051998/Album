package com.fiveti.a5tphoto.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabase extends SQLiteOpenHelper {
    public SQLiteDatabase(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //truy vấn không trả kết quả CREATE, INSERT, UPDATE, DELETE
    public void QueryData(String query)
    {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    //truy vấn có trả kết quả: SELECT
    public Cursor GetData(String query)
    {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

