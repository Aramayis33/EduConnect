package org.example.educonnectjavaproject.schedule;

import java.sql.Date;

public class ScheduleWithDate {
    private Schedule schedule;
    private Date date;

    public ScheduleWithDate() {
    }

    public ScheduleWithDate(Schedule schedule, Date date) {
        this.schedule = schedule;
        this.date = date;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
