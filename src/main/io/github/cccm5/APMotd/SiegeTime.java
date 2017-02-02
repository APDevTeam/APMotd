package io.github.cccm5.APMotd;

import java.util.Calendar;
import java.util.TimeZone;

public class SiegeTime implements Comparable<SiegeTime>{
    private int minute, hour, day;

    //A day of 1 is monday, 7 is sunday
    public SiegeTime(int minute, int hour, int day){
        if(minute > 60 || minute < 0)
            throw new IllegalArgumentException("Minute must be withing 1-60. Input: " + minute);
        if(hour > 24 || hour < 0)
            throw new IllegalArgumentException("Hour must be withing 0-24. Input: " + hour);
        if(day > 7 || day < 1)
            throw new IllegalArgumentException("day must be withing 1-7. Input: " + day);
        this.minute = minute;
        this.hour = hour;
        this.day = day;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    private int toMinutes() {
        return 1440*getDay() + 60 * hour + minute;
    }

    @Override
    public int compareTo(SiegeTime o) {
        return siegeTimetoMinutes(this) - siegeTimetoMinutes(o);
        //return (day*1440 + hour * 60 + minute) - (o.getDay() * 1440 + o.getHour()*60 + o.getMinute());
    }

    public static SiegeTime minutesToSiegeTime(int minutes){
        return new SiegeTime(minutes%60,minutes/60%24,minutes/24/60);
        //return new SiegeTime(minutes%1440%60, minutes%1440/60, minutes/1440);
    }

    public static int siegeTimetoMinutes(SiegeTime t){
        return (t.getDay()-1) * 1440 + t.getHour()*60 + t.getMinute();

    }
}
