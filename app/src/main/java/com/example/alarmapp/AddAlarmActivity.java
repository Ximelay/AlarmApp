package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {
    private int hour, minute;
    private int repeatCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        EditText timeEditText = findViewById(R.id.timeEditText);
        NumberPicker repeatPicker = findViewById(R.id.repeatPicker);
        Button saveButton = findViewById(R.id.saveButton);

        // Проверяем и запрашиваем разрешение на точные будильники
        checkAndRequestAlarmPermission();

        // Настройка NumberPicker
        repeatPicker.setMinValue(1);
        repeatPicker.setMaxValue(10);
        repeatPicker.setValue(1);
        repeatPicker.setOnValueChangedListener((picker, oldVal, newVal) -> repeatCount = newVal);

        timeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int selectedHour, int selectedMinute) -> {
                hour = selectedHour;
                minute = selectedMinute;
                timeEditText.setText(String.format("%02d:%02d", hour, minute));
            }, hour, minute, true);
            timePickerDialog.show();
        });

        saveButton.setOnClickListener(v -> {
            Alarm alarm = new Alarm(hour, minute, true, repeatCount);
            AlarmDatabase db = new AlarmDatabase(this);
            db.addAlarm(alarm);

            // Настраиваем будильник
            scheduleAlarm(alarm);

            setResult(RESULT_OK);
            finish();
        });
    }

    private void scheduleAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.putExtra("repeatCount", alarm.getRepeatCount());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // Устанавливаем точный будильник
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void checkAndRequestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {
                // Запрашиваем разрешение через настройки
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // Открываем настройки для включения разрешения
            }
        }
    }

    private void stopAlarm() {
        int alarmId = getIntent().getIntExtra("alarmId", -1); // Получаем ID будильника
        if (alarmId != -1) {
            // Обновляем состояние будильника в базе данных
            AlarmDatabase db = new AlarmDatabase(this);
            Alarm alarm = db.getAlarmById(alarmId); // Создайте метод getAlarmById
            if (alarm != null) {
                alarm.setActive(false); // Делаем будильник неактивным
                db.updateAlarm(alarm); // Обновляем базу данных
            }
        }

        // Сообщаем пользователю
        Toast.makeText(this, "Будильник выключен", Toast.LENGTH_SHORT).show();

        finish(); // Закрываем активность
    }
}