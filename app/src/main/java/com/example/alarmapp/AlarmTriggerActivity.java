package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AlarmTriggerActivity extends AppCompatActivity {
    private int repeatCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_trigger);

        TextView alarmMessage = findViewById(R.id.alarmMessage);
        Button snoozeButton = findViewById(R.id.snoozeButton);
        Button stopButton = findViewById(R.id.stopButton);

        repeatCount = getIntent().getIntExtra("repeatCount", 1); // Получаем количество повторений

        alarmMessage.setText("Будильник сработал!");

        snoozeButton.setOnClickListener(v -> snoozeAlarm());
        stopButton.setOnClickListener(v -> stopAlarm());
    }

    private void snoozeAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
            return;
        }

        if (repeatCount > 0) {
            int requestCode = (int) System.currentTimeMillis();

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("repeatCount", repeatCount - 1);
            intent.putExtra("alarmId", getIntent().getIntExtra("alarmId", -1));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Calendar snoozeTime = Calendar.getInstance();
            snoozeTime.add(Calendar.MINUTE, 5);

            int alarmId = getIntent().getIntExtra("alarmId", -1);
            if (alarmId != -1) {
                AlarmDatabase db = new AlarmDatabase(this);
                Alarm alarm = db.getAlarmById(alarmId);
                if (alarm != null) {
                    alarm.setHour(snoozeTime.get(Calendar.HOUR_OF_DAY));
                    alarm.setMinute(snoozeTime.get(Calendar.MINUTE));
                    db.updateAlarm(alarm);
                }
            }

            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Будильник отложен на 5 минут", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(this, "Не удалось установить точный будильник: требуется разрешение", Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }

    private void stopAlarm() {
        int alarmId = getIntent().getIntExtra("alarmId", -1);
        if (alarmId != -1) {
            AlarmDatabase db = new AlarmDatabase(this);
            Alarm alarm = db.getAlarmById(alarmId);
            if (alarm != null) {
                alarm.setActive(false);
                db.updateAlarm(alarm);
            }
        }

        Toast.makeText(this, "Будильник выключен", Toast.LENGTH_SHORT).show();
        finish();
    }
}