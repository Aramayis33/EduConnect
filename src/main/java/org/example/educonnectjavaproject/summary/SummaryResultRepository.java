package org.example.educonnectjavaproject.summary;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.security.auth.Subject;
import java.util.List;

public interface SummaryResultRepository extends JpaRepository<SummaryResult, Integer> {

    SummaryResult findSummaryResultByStudentAndSubject(Student student, Subjects subject);

    List<SummaryResult> findSummaryResultByStudent(Student student);

    @Query("SELECT AVG(s.afterAll) FROM SummaryResult s WHERE s.student = :student")
    Double findAverageByStudent(@Param("student") Student student);

    @Query("SELECT AVG(s.afterAll) FROM SummaryResult s where s.subject=:subject")
    Double findAveageGradeBySubject(Subjects subject);

    List<SummaryResult> findSummaryResultsBySubjectAndGroup(Subjects subject, GroupInfo group);

    default int excellentCount(List<Student> students) {
        int excellentCount = 0;
        for (int i = 0; i <students.size() ; i++) {
         List<SummaryResult> summaryResult=findSummaryResultByStudent(students.get(i));
         if(summaryResult.size()>0){
             Double avg=findAverageByStudent(students.get(i));
             if(Math.round(avg)>=9){
                 excellentCount++;
             }

         }
        }
        return excellentCount;
    }

    default int intermediateCount(List<Student> students) {
        int intermediateCount = 0;
        for (int i = 0; i <students.size() ; i++) {
            List<SummaryResult> summaryResult=findSummaryResultByStudent(students.get(i));
            if(summaryResult.size()>0){
                Double avg=findAverageByStudent(students.get(i));
                if(Math.round(avg)>4&&Math.round(avg)<9){
                    intermediateCount++;
                }

            }
        }
        return intermediateCount;
    }

    default int sufficientCount(List<Student> students) {
        int sufficientCount = 0;
        for (int i = 0; i <students.size() ; i++) {
            List<SummaryResult> summaryResult=findSummaryResultByStudent(students.get(i));
            if(summaryResult.size()>0){
                Double avg=findAverageByStudent(students.get(i));
                if(Math.round(avg)==4){
                    sufficientCount++;
                }

            }
        }
        return sufficientCount;
    }


    default int inSufficientCount(List<Student> students) {
        int inSufficientCount = 0;
        for (int i = 0; i <students.size() ; i++) {
            List<SummaryResult> summaryResult=findSummaryResultByStudent(students.get(i));
            if(summaryResult.size()>0){
                Double avg=findAverageByStudent(students.get(i));
                if(Math.round(avg)<4){
                    inSufficientCount++;
                }

            }
        }
        return inSufficientCount;
    }


}
