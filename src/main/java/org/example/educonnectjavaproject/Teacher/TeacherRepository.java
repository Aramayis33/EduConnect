package org.example.educonnectjavaproject.Teacher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Teacher findTeacherByUsername(String username);
    Teacher findTeacherById(int id);
}
