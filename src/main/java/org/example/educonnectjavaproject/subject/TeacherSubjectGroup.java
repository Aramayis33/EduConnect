package org.example.educonnectjavaproject.subject;

import jakarta.persistence.*;

import javax.security.auth.Subject;
import java.beans.JavaBean;

@Entity
public class TeacherSubjectGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subjects subject;

    @ManyToOne
    @JoinColumn(name="subject_group_id")
    private SubjectGroup subjectGroup;

    public TeacherSubjectGroup() {
    }

    public TeacherSubjectGroup(SubjectGroup subjectGroup, Subjects subject) {
        this.subjectGroup = subjectGroup;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public SubjectGroup getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(SubjectGroup subjectGroup) {
        this.subjectGroup = subjectGroup;
    }
}
