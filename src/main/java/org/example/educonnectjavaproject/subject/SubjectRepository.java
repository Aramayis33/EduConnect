package org.example.educonnectjavaproject.subject;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    Subject findByStudentId(int studentId);
    void deleteByStudentId(int studentId);
    @Query("select s from Subject s join s.student u")
    List<Subject> findAllSubjectsWithStudents();

    @Query("select s from Subject s join s.student st where st.groupInfo=:group")
    List<Subject>findSubjectAndStudentByGroup(GroupInfo group);

}
