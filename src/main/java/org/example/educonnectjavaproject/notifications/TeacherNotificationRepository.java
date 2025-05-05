package org.example.educonnectjavaproject.notifications;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface TeacherNotificationRepository extends JpaRepository<TeacherNotification, Integer> {


//    List<StudentNotification> findStudentNotificationsByStudentOrGroup(Student student, GroupInfo group);
//
//    StudentNotification findStudentNotificationById(int id);
//
//    default List<StudentNotification>findStudentNotificationsByStudentOrGroupAndIsRead(Student student, GroupInfo groupInfo,StudentNotificationReadingRepository studentNotificationReadingRepository){
//        List<StudentNotification>allNotifications=findStudentNotificationsByStudentOrGroup(student, groupInfo);
//        List<StudentNotification>returnList=new ArrayList<StudentNotification>();
//
//        for(StudentNotification notification:allNotifications){
//            StudentNotificationReading notifRead=studentNotificationReadingRepository.findByStudentAndStudentNotification(student, notification);
//            if(notifRead==null){
//                returnList.add(notification);
//            }
//        }
//        return returnList;
//    }

    List<TeacherNotification>findTeacherNotificationsBySubjects(Subjects subjects);
    List<TeacherNotification>findTeacherNotificationsByTeacher(Teacher teacher);

    default List<TeacherNotification>findTeacherNotificationsByTeacherOrSubjects(Teacher teacher, Subjects subjects) {
        List<TeacherNotification>all=findTeacherNotificationsBySubjects(subjects);
        List<TeacherNotification>teacherNotifications=findTeacherNotificationsByTeacher(teacher);
        all.addAll(teacherNotifications);

        return all;
    }

    default List<TeacherNotification>findTeacherNotificationsByListAndIsRead(List<TeacherNotification>all,Teacher teacher,TeacherNotificationReadingRepository teacherNotificationReadingRepository) {
       List<TeacherNotification>returnList=new ArrayList<>();
        for(TeacherNotification teacherNotification:all){
            TeacherNotificationReading teacherNotificationReading=teacherNotificationReadingRepository.findTeacherNotificationReadingByTeacherAndNotification(teacher,teacherNotification);
            if(teacherNotificationReading==null){
                returnList.add(teacherNotification);
            }
        }
        return returnList;

    }

    default List<TeacherNotification>sortTeacherNotificationsByDate(List<TeacherNotification>notifications){
        for (int i = 0; i <notifications.size() ; i++) {
            for (int j = 0; j <notifications.size()-1 ; j++) {
                if(notifications.get(j).getDate().compareTo(notifications.get(j+1).getDate())<0){
                    TeacherNotification teacherNotification=notifications.get(j);
                    notifications.set(j,notifications.get(j+1));
                    notifications.set(j+1,teacherNotification);
                }
            }
        }
        return notifications;
    }


    TeacherNotification findTeacherNotificationById(Integer id);
}
