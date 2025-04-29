package org.example.educonnectjavaproject.exercises;

import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findExercisesByStudent(Student student);

    @Query("Select e from Exercise e LEFT JOIN FETCH e.homeWork h where h.teacher= :teacher and e.isChecked= :isChecked")
    List<Exercise>findExercisesByTeacherAndIsChecked(Teacher teacher, int isChecked);

    List<Exercise>findExercisesByHomeWork(HomeWork homeWork);

    Exercise findExerciseById(int id);

}
