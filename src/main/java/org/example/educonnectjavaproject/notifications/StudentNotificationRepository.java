package org.example.educonnectjavaproject.notifications;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Integer> {


    List<StudentNotification> findStudentNotificationsByStudentOrGroup(Student student, GroupInfo group);

    StudentNotification findStudentNotificationById(int id);

    default List<StudentNotification>findStudentNotificationsByStudentOrGroupAndIsRead(Student student, GroupInfo groupInfo,StudentNotificationReadingRepository studentNotificationReadingRepository){
        List<StudentNotification>allNotifications=findStudentNotificationsByStudentOrGroup(student, groupInfo);
       List<StudentNotification>returnList=new ArrayList<StudentNotification>();

        for(StudentNotification notification:allNotifications){
            StudentNotificationReading notifRead=studentNotificationReadingRepository.findByStudentAndStudentNotification(student, notification);
            if(notifRead==null){
                returnList.add(notification);
            }
        }
        for (int i = 0; i <returnList.size() ; i++) {
            for (int j = 0; j < returnList.size()-1; j++) {
                if(returnList.get(j).getDate().compareTo(returnList.get(j+1).getDate())<0){
                    StudentNotification studentNotification=returnList.get(j);
                    returnList.set(j,returnList.get(j+1));
                    returnList.set(j+1,studentNotification);
                }
            }
        }
        return returnList;
    }

    default List<StudentNotification>sortStudentNotificationsByDate(List<StudentNotification>returnList){
        for (int i = 0; i <returnList.size() ; i++) {
            for (int j = 0; j < returnList.size()-1; j++) {
                if(returnList.get(j).getDate().compareTo(returnList.get(j+1).getDate())<0){
                    StudentNotification studentNotification=returnList.get(j);
                    returnList.set(j,returnList.get(j+1));
                    returnList.set(j+1,studentNotification);
                }
            }
        }
        return returnList;
    }
}
