package com.example.alarmapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_ALARMS = "alarms";

    public AlarmDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE alarms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hour INTEGER, " +
                "minute INTEGER, " +
                "active INTEGER, " +
                "repeatCount INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // Версия 2 включает поле repeatCount
            db.execSQL("ALTER TABLE alarms ADD COLUMN repeatCount INTEGER DEFAULT 1");
        }
    }

    public void addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("active", alarm.isActive() ? 1 : 0);
        values.put("repeatCount", alarm.getRepeatCount()); // Убедитесь, что добавляете это поле
        db.insert("alarms", null, values);
        db.close();
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("alarms", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("active", alarm.isActive() ? 1 : 0);
        values.put("repeatCount", alarm.getRepeatCount());

        db.update("alarms", values, "id = ?", new String[]{String.valueOf(alarm.getId())});
        db.close();
    }

    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);

        if (cursor.moveToFirst()) {
            do {
                int idColumn = cursor.getColumnIndex("id");
                int hourColumn = cursor.getColumnIndex("hour");
                int minuteColumn = cursor.getColumnIndex("minute");
                int activeColumn = cursor.getColumnIndex("active");
                int repeatCountColumn = cursor.getColumnIndex("repeatCount");

                if (idColumn != -1 && hourColumn != -1 && minuteColumn != -1 && activeColumn != -1 && repeatCountColumn != -1) {
                    int id = cursor.getInt(idColumn);
                    int hour = cursor.getInt(hourColumn);
                    int minute = cursor.getInt(minuteColumn);
                    boolean active = cursor.getInt(activeColumn) == 1;
                    int repeatCount = cursor.getInt(repeatCountColumn);

                    alarmList.add(new Alarm(id, hour, minute, active, repeatCount));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return alarmList;
    }

    public Alarm getAlarmById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM alarms WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            int hourIndex = cursor.getColumnIndex("hour");
            int minuteIndex = cursor.getColumnIndex("minute");
            int activeIndex = cursor.getColumnIndex("active");
            int repeatCountIndex = cursor.getColumnIndex("repeatCount");

            if (hourIndex == -1 || minuteIndex == -1 || activeIndex == -1 || repeatCountIndex == -1) {
                throw new IllegalStateException("Ошибка в структуре таблицы базы данных.");
            }

            int hour = cursor.getInt(hourIndex);
            int minute = cursor.getInt(minuteIndex);
            boolean active = cursor.getInt(activeIndex) == 1;
            int repeatCount = cursor.getInt(repeatCountIndex);

            cursor.close();
            return new Alarm(id, hour, minute, active, repeatCount);
        }

        if (cursor != null) cursor.close();
        return null;
    }
}