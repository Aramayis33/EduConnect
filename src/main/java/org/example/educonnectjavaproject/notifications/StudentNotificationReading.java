package org.example.educonnectjavaproject.notifications;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.teacher.Teacher;

@Entity
public class StudentNotificationReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name="notification_id")
    private StudentNotification studentNotification;

    public StudentNotificationReading() {
    }

    public StudentNotificationReading(Student student, StudentNotification studentNotification) {
        this.student = student;
        this.studentNotification = studentNotification;
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

    public StudentNotification getStudentNotification() {
        return studentNotification;
    }

    public void setStudentNotification(StudentNotification studentNotification) {
        this.studentNotification = studentNotification;
    }
}
