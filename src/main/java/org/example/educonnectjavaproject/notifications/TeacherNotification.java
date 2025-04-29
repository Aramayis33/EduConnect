package org.example.educonnectjavaproject.notifications;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class TeacherNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subjects subjects;

    private Timestamp date;

    private String messageTitle;

    private String messageText;

    private String messageType;

    private int isRead;



    public TeacherNotification() {
    }

    public TeacherNotification(Teacher teacher, Subjects subjects, Timestamp date, String messageTitle, String messageText,String messageType) {
        this.teacher = teacher;
        this.subjects = subjects;
        this.date = date;
        this.messageTitle = messageTitle;
        this.messageText = messageText;
        this.messageType = messageType;
        this.isRead = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subjects getSubjects() {
        return subjects;
    }

    public void setSubjects(Subjects subjects) {
        this.subjects = subjects;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }


}
