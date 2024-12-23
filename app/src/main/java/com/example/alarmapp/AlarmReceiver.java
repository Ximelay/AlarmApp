package com.example.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int repeatCount = intent.getIntExtra("repeatCount", 1);

        Log.d("AlarmReceiver", "Повторный будильник сработал! Осталось повторов: " + repeatCount);

        // Запускаем активность для обработки будильника
        Intent alarmIntent = new Intent(context, AlarmTriggerActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        alarmIntent.putExtra("repeatCount", repeatCount);
        context.startActivity(alarmIntent);
    }
}