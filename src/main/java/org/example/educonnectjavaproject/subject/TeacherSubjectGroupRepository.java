package org.example.educonnectjavaproject.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.security.auth.Subject;
import java.util.List;

public interface TeacherSubjectGroupRepository extends JpaRepository<TeacherSubjectGroup, Integer> {

    @Query("SELECT tsg.subject FROM TeacherSubjectGroup tsg WHERE tsg.subjectGroup = :subjectGroup")
    List<Subjects> findSubjectsBySubjectGroup(SubjectGroup subjectGroup);

    void deleteBySubjectGroupAndSubject(SubjectGroup subjectGroup, Subjects subject);
    TeacherSubjectGroup findSubjectBySubjectGroupAndSubject(SubjectGroup subjectGroup, Subjects subject);


    List<TeacherSubjectGroup> findTeacherSubjectGroupsBySubject(Subjects subject);
}