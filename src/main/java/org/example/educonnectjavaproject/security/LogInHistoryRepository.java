package org.example.educonnectjavaproject.security;

import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogInHistoryRepository extends JpaRepository<LogInHistory, Integer> {

    List<LogInHistory> findLogInHistoriesByStudent(Student student);

    List<LogInHistory> findLogInHistoriesByTeacher(Teacher teacher);
}
