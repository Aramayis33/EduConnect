package org.example.educonnectjavaproject.homework;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.sql.Date;

@Entity
@AllArgsConstructor
public class HomeWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int groupNumber;

    private String title;
    private Date inputDate;
    @Column(nullable=false,length=50)
    private String subject;

    public HomeWork() {
    }

    public HomeWork(int groupNumber, String title, Date inputDate, String subject) {
        this.groupNumber = groupNumber;
        this.title = title;
        this.inputDate = inputDate;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
