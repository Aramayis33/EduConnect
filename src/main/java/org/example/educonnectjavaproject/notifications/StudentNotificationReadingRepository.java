package org.example.educonnectjavaproject.notifications;

import org.example.educonnectjavaproject.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentNotificationReadingRepository extends JpaRepository<StudentNotificationReading, Integer> {

    StudentNotificationReading findByStudentAndStudentNotification(Student student, StudentNotification studentNotification);
}
