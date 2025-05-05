package org.example.educonnectjavaproject.schedule;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findSchedulesByGroup(GroupInfo groupInfo);
    List<Schedule> findSchedulesByGroupAndSemester(GroupInfo groupInfo, int semester);
    List<Schedule> findSchedulesByGroupAndSemesterAndSemesterPart(GroupInfo group, int semester, String semesterPart);
    @Query("SELECT s FROM Schedule s WHERE s.group = :group AND s.semester = :semester " +
            "AND (s.semesterPart = :semesterPart OR s.semesterPart = 'all')")
    List<Schedule> findSchedulesByGroupAndSemesterAndSemesterPartOrAll(
            @Param("group") GroupInfo group,
            @Param("semester") int semester,
            @Param("semesterPart") String semesterPart
    );
    @Query("Select s From Schedule s Where s.teacher=:teacher And s.semester=:semester and s.weekday=:weekday and s.classHour=:classHour " +
            "And (s.semesterPart = :semesterPart OR s.semesterPart = 'all') ")
    Schedule findTeacherDayClass(@Param("teacher")Teacher teacher,
              @Param("semester")int semester, @Param("weekday")int weekday,
              @Param("classHour")int classHour,@Param("semesterPart")String semesterPart);

    @Query("Select s From Schedule s Where s.classroom=:classroom And s.semester=:semester and s.weekday=:weekday and s.classHour=:classHour " +
            "And (s.semesterPart = :semesterPart OR s.semesterPart = 'all') ")
    Schedule findLaboratoryUsage(@Param("semester")int semester,@Param("semesterPart")String semesterPart, @Param("weekday")int weekday,@Param("classHour")int classHour,
                                 @Param("classroom")String classroom);


    @Query("SELECT s FROM Schedule s WHERE s.teacher = :teacher AND s.semester = :semester " +
            "AND (s.semesterPart = :semesterPart OR s.semesterPart = 'all')")
    List<Schedule> findSchedulesByTeacherAndSemesterAndSemesterPartOrAll(@Param("teacher") Teacher teacher, @Param("semester") int semester,
            @Param("semesterPart") String semesterPart
    );
    @Query("SELECT DISTINCT s.group FROM Schedule s WHERE s.teacher = :teacher")
    List<GroupInfo> findTeacherGroupsFromSchedule(Teacher teacher);
    @Query("SELECT s FROM Schedule s WHERE s.teacher = :teacher AND s.id IN " +
            "(SELECT MIN(s2.id) FROM Schedule s2 WHERE s2.teacher = :teacher GROUP BY s2.group)")
    List<Schedule> findTeacherScheduleGroups(@Param("teacher") Teacher teacher);
    List<Schedule> findSchedulesByTeacherAndGroupAndSemester(Teacher teacher, GroupInfo group, int semester);

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.teacher = :teacher " +
            "AND s.semester = :semester " +
            "And s.subject = :subject "+
            "AND (s.semesterPart = 'all' OR s.semesterPart = :semesterPart)")
    List<Schedule> findSchedulesByTeacherAndSemesterAndSemesterPartAndSubject(
            @Param("teacher") Teacher teacher,
            @Param("semester") int semester,
            @Param("semesterPart") String semesterPart,
            @Param("subject") Subjects subject);


    @Query("SELECT s FROM Schedule s Where s.teacher= :teacher AND s.group= :group And s.semester= :semester And " +
            "(s.semesterPart = :semesterPart OR s.semesterPart = 'all') And s.subject= :subject ")
    List<Schedule> findSchedulesByTeacherAndGroupAndSemesterAndSemesterPartAndSubject(Teacher teacher, GroupInfo group, int semester, String semesterPart, Subjects subject);

    List<Schedule> findSchedulesByTeacherAndSubject(Teacher teacher, Subjects subject);

    void deleteSchedulesByGroup(GroupInfo group);

    List<Schedule>findSchedulesBySubject(Subjects subject);

    List<Schedule>findSchedulesBySemesterAndSemesterPartAndGroup(int semester, String semesterPart, GroupInfo group);

    Schedule findScheduleBySemesterAndSemesterPartAndWeekdayAndClassHourAndGroup(int semester, String semesterPart, int weekday, int classHour, GroupInfo group);

    Schedule findScheduleById(int id);
}
