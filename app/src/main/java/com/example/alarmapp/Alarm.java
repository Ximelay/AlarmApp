package com.example.alarmapp;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private boolean active;
    private int repeatCount; // Новое поле для повторений

    public Alarm(int hour, int minute, boolean active, int repeatCount) {
        this.hour = hour;
        this.minute = minute;
        this.active = active;
        this.repeatCount = repeatCount;
    }

    public Alarm(int id, int hour, int minute, boolean active, int repeatCount) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.active = active;
        this.repeatCount = repeatCount;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
}