package org.example.educonnectjavaproject.notifications;

import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherNotificationReadingRepository extends JpaRepository<TeacherNotificationReading, Integer> {

    TeacherNotificationReading findTeacherNotificationReadingByTeacherAndNotification(Teacher teacher, TeacherNotification notification);
}
