package org.example.educonnectjavaproject.teacher;

import org.example.educonnectjavaproject.subject.Subjects;

import java.util.List;

public class TeacherWithSubjects {
    private Teacher teacher;
    private List<Subjects> subjectsList;

    public TeacherWithSubjects(Teacher teacher, List<Subjects> subjectsList) {
        this.teacher = teacher;
        this.subjectsList = subjectsList;
    }

    public TeacherWithSubjects() {
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Subjects> getSubjectsList() {
        return subjectsList;
    }

    public void setSubjectsList(List<Subjects> subjectsList) {
        this.subjectsList = subjectsList;
    }
}
