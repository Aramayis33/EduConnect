package org.example.educonnectjavaproject.notifications;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
public class StudentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name="group_id")
    private GroupInfo group;

    private Timestamp date;

    private String messageTitle;

    private String messageText;

    private String messageType;
    public int isRead;

    public StudentNotification() {
    }

    public StudentNotification(Student student, GroupInfo group, Timestamp date, String messageTitle, String messageText,String messageType) {
        this.student = student;
        this.group = group;
        this.date = date;
        this.messageTitle = messageTitle;
        this.messageText = messageText;
        this.messageType = messageType;
        this.isRead = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
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
