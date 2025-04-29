package org.example.educonnectjavaproject.homework;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.filegroup.FileGroup;
import org.example.educonnectjavaproject.subject.Subjects;

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
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subjects subject;
    private int requiresResponse;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "file_group_id")
private FileGroup fileGroup;


    @Column(length = 1000)
    private String description;

    public HomeWork() {
    }

    public HomeWork(int groupNumber, String title, Date inputDate,Date expirationDate, Subjects subject,int requiresResponse,Teacher teacher,FileGroup fileGroup,String description) {
        this.groupNumber = groupNumber;
        this.title = title;
        this.inputDate = inputDate;
        this.expirationDate = expirationDate;
        this.subject = subject;
        this.requiresResponse = requiresResponse;
        this.teacher = teacher;
        this.fileGroup = fileGroup;
        this.description = description;
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


    public int getRequiresResponse() {
        return requiresResponse;
    }

    public void setRequiresResponse(int requiresResponse) {
        this.requiresResponse = requiresResponse;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public FileGroup getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(FileGroup fileGroup) {
        this.fileGroup = fileGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
