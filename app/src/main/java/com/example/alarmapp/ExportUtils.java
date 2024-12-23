package com.example.alarmapp;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExportUtils {
    public static void exportToJSON(Context context, List<Alarm> alarms) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Alarm alarm : alarms) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hour", alarm.getHour());
                jsonObject.put("minute", alarm.getMinute());
                jsonObject.put("active", alarm.isActive());
                jsonArray.put(jsonObject);
            }

            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "alarms.json");
            FileWriter writer = new FileWriter(file);
            writer.write(jsonArray.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}