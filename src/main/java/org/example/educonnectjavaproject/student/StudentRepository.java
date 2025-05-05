package org.example.educonnectjavaproject.student;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Integer> {

    Student findStudentByEmail(String username);



    Student findStudentById(int id);

    List<Student> findStudentsByGroupInfo(GroupInfo groupInfo);
    void deleteByEmail(String email);

    Student findStudentByEmailAndIsActivatedAndIsBlocked(String email, int isActivated, int isBlocked);
Student findStudentByEmailAndIsActivated(String email, int isActivated);
    //void updateStudent(Student student);
}
