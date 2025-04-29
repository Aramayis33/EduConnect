package org.example.educonnectjavaproject.notifications;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.teacher.Teacher;

import java.util.List;

@Entity
public class TeacherNotificationReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name="notification_id")
    private TeacherNotification notification;

    public TeacherNotificationReading() {
    }

    public TeacherNotificationReading(Teacher teacher, TeacherNotification notification) {
        this.teacher = teacher;
        this.notification = notification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public TeacherNotification getNotification() {
        return notification;
    }

    public void setNotification(TeacherNotification notification) {
        this.notification = notification;
    }

}

