package com.example.alarmapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private Context context;
    private List<Alarm> alarmList;
    private AlarmDatabase alarmDatabase; // Для работы с базой данных

    public AlarmAdapter(Context context, List<Alarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
        this.alarmDatabase = new AlarmDatabase(context); // Инициализация базы данных
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);

        // Установка времени будильника
        holder.alarmTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        // Установка состояния переключателя
        holder.activeSwitch.setChecked(alarm.isActive());

        // Обработка изменения состояния переключателя
        holder.activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setActive(isChecked);
            alarmDatabase.updateAlarm(alarm); // Обновляем состояние в базе данных
        });

        // Обработка кнопки удаления
        holder.deleteButton.setOnClickListener(v -> {
            alarmDatabase.deleteAlarm(alarm.getId()); // Удаляем будильник из базы данных
            alarmList.remove(position); // Удаляем из списка
            notifyItemRemoved(position); // Уведомляем адаптер
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime;
        Switch activeSwitch;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarmTime);
            activeSwitch = itemView.findViewById(R.id.activeSwitch);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}