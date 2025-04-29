package org.example.educonnectjavaproject.feedback;

import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    List<Feedback> findFeedbacksByStudentId(int id);

    List<Feedback> findFeedbacksByTeacher(Teacher teacher);
}
