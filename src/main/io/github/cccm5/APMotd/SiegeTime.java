package io.github.cccm5.APMotd;

public class SiegeTime implements Comparable<SiegeTime>{
    private int minute, hour, day;

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

    public SiegeTime getInterval(SiegeTime time){
        return minutesToSiegeTime(Math.abs(this.toMinutes()-time.toMinutes()));
    }

    private int toMinutes() {
        return 1440*day + 60 * hour + minute;
    }

    @Override
    public int compareTo(SiegeTime o) {
        return (day*1440 + hour * 60 + minute) - (o.getDay() * 1440 + o.getHour()*60 + o.getMinute());
    }

    private static SiegeTime minutesToSiegeTime(int minutes){
        return new SiegeTime(minutes/1440, minutes%1440/60, minutes%1440%60);
    }
}
