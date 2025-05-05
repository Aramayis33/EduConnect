package org.example.educonnectjavaproject.ratings;

import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.security.auth.Subject;
import java.sql.Date;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating,Integer> {
    List<Rating> findByStudent(Student student);
    @Query("SELECT r FROM Rating r JOIN r.student u WHERE u = :student")
    List<Rating> findRatingsByStudent(@Param("student") Student student);
    @Query("SELECT DISTINCT s FROM Subjects s JOIN Rating r ON s.id = r.subject.id WHERE r.student = :student")
    List<Subjects> findSubjectsByStudent(@Param("student") Student student);
    @Query("Select r from Rating r where r.student= :student and r.semester= :semester and r.subject= :subject and r.rating !=0")
    List<Rating> findRatingsBySemesterAndStudentAndSubject(Integer semester, Student student, Subjects subject);
List<Rating> findRatingsByTeacherAndSemester(Teacher teacher, int semester);
Rating findRatingByTeacherAndStudentAndDate(Teacher teacher, Student student, Date date);
    @Query("SELECT ROUND(AVG(r.rating)) FROM Rating r " +
            "WHERE r.student = :student " +
            "AND r.subject = :subject " +
            "AND EXTRACT(MONTH FROM r.date) BETWEEN 9 AND 12 " +
            "AND r.rating > 0")
    Double findFirstSemesterAverageRatingByStudentAndSubject(
            @Param("student") Student student,
            @Param("subject") Subjects subject
    );

    @Query("SELECT ROUND(AVG(r.rating)) FROM Rating r " +
            "WHERE r.student = :student " +
            "AND r.subject = :subject " +
            "AND EXTRACT(MONTH FROM r.date) BETWEEN 1 AND 6 " +
            "AND r.rating > 0 ")
    Double findSecondSemesterAverageRatingByStudentAndSubject(
            @Param("student") Student student,
            @Param("subject") Subjects subject
    );
    List<Rating>findRatingsByStudentAndRating(Student student, int rating);

}