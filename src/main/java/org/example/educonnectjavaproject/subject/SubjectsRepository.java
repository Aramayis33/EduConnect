package org.example.educonnectjavaproject.subject;

import org.example.educonnectjavaproject.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SubjectsRepository extends JpaRepository<Subjects, Integer> {
    Subjects findSubjectByName(String name);

//    Subjects findByStudentId(int studentId);
//    void deleteByStudentId(int studentId);
//    @Query("select s from Subject s join s.student u")
//    List<Subject> findAllSubjectsWithStudents();
//
//    @Query("select s from Subject s join s.student st where st.groupInfo=:group")
//    List<Subject>findSubjectAndStudentByGroup(GroupInfo group);

//
//    @Query("Select s from Subjects s Join fetch sg. s where tsg.subjectGroup= :subjectGroup")
//    List<Subjects> findSubjectsBySubjectGroup(SubjectGroup subjectGroup);

    Subjects findSubjectById(int id);

    default Map<String, Integer> getSubjectOccupation(List<Schedule> schedules) {
        Map<String , Integer> subjectMap = new HashMap<>();
        List<Subjects> subjectList = findAll();

        for (Subjects subject : subjectList) {
            int subCount = 0;
            for (Schedule schedule : schedules) {
                if (subject.equals(schedule.getSubject())) {
                    subCount++;
                }
            }
            subjectMap.put(subject.getName(), subCount);
        }
        return subjectMap;
    }

}
