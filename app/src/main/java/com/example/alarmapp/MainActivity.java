package com.example.alarmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AlarmDatabase alarmDatabase;
    private List<Alarm> alarmList;
    private AlarmAdapter alarmAdapter;

    private static final int ADD_ALARM_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        Button addAlarmButton = findViewById(R.id.addAlarmButton);

        alarmDatabase = new AlarmDatabase(this);
        alarmList = alarmDatabase.getAllAlarms();
        alarmAdapter = new AlarmAdapter(this, alarmList);

        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmRecyclerView.setAdapter(alarmAdapter);

        addAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
            startActivityForResult(intent, ADD_ALARM_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ALARM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Заново загружаем список будильников из базы данных
            alarmList.clear();
            alarmList.addAll(alarmDatabase.getAllAlarms());
            alarmAdapter.notifyDataSetChanged();
        }
    }
}