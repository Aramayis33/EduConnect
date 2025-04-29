package org.example.educonnectjavaproject.summary;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.springframework.data.repository.cdi.Eager;

import javax.security.auth.Subject;

@Entity
public class SummaryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subjects subject;
    @ManyToOne
    @JoinColumn(name="group_id")
    private GroupInfo group;

    private double firstSemester;
    private double secondSemester;
    private double afterAll;


    public SummaryResult() {
    }

    public SummaryResult(Student student, Subjects subject, GroupInfo group, double firstSemester, double secondSemester, double afterAll) {
        this.student = student;
        this.subject = subject;
        this.group = group;
        this.firstSemester = firstSemester;
        this.secondSemester = secondSemester;
        this.afterAll = afterAll;
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

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    public double getFirstSemester() {
        return firstSemester;
    }

    public void setFirstSemester(double firstSemester) {
        this.firstSemester = firstSemester;
    }

    public double getSecondSemester() {
        return secondSemester;
    }

    public void setSecondSemester(double secondSemester) {
        this.secondSemester = secondSemester;
    }

    public double getAfterAll() {
        return afterAll;
    }

    public void setAfterAll(double afterAll) {
        this.afterAll = afterAll;
    }
}
