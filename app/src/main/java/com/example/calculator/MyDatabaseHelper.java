package com.example.calculator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "calculator.db";
    private static final String TABLE_NAME = "equations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EQUATION = "equation";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EQUATION + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertEquation(String equation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EQUATION, equation);
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    public List<String> getAllEquations() {
        List<String> equations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String equation = cursor.getString(cursor.getColumnIndex(COLUMN_EQUATION));
                equations.add(equation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return equations;
    }
    public List<String> getRecentEquations(int limit) {
        List<String> equationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + COLUMN_ID + " DESC" +
                " LIMIT " + limit;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String equation = cursor.getString(cursor.getColumnIndex(COLUMN_EQUATION));
                equationList.add(equation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return equationList;
    }


}
