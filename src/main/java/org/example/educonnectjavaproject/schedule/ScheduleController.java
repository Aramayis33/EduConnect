package org.example.educonnectjavaproject.schedule;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.notifications.*;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.ratings.RatingRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.subject.SubjectGroupRepository;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.TeacherSubjectGroupRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HttpSession session;
    @Autowired
    private GroupRepository groupRepository;

    ScheduleService scheduleService = new ScheduleService();
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private StudentNotificationRepository studentNotificationRepository;

    @Autowired
    private StudentNotificationReadingRepository studentNotificationReadingRepository;

    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;
    @Autowired
    private SubjectGroupRepository subjectGroupRepository;
    @Autowired
    private TeacherSubjectGroupRepository teacherSubjectGroupRepository;

    @Autowired
    private TeacherNotificationReadingRepository teacherNotificationReadingRepository;

    @GetMapping("/student/schedule")
    public String schedule(
            @RequestParam(name = "semester", defaultValue = "1") String semester,
            @RequestParam(name = "semesterPart", defaultValue = "first") String semesterPart,
            Model model) {

        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/";
        }

        int semesterNum;
        try {
            semesterNum = Integer.parseInt(semester);
            if (semesterNum != 1 && semesterNum != 2) {
                semesterNum = 1;
            }
        } catch (NumberFormatException e) {
            semesterNum = 1;
        }

        String validSemesterPart = (semesterPart.equals("second")) ? "second" : "first";

        List<Schedule> schedules = scheduleRepository
                .findSchedulesByGroupAndSemesterAndSemesterPartOrAll(
                        student.getGroupInfo(),
                        semesterNum,
                        validSemesterPart
                );

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedSemester", semesterNum);
        model.addAttribute("selectedSemesterPart", validSemesterPart);
        model.addAttribute("student", student);

        List<StudentNotification>studentNotifications=
                studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student,student.getGroupInfo(),studentNotificationReadingRepository);
        model.addAttribute("studentNotifications", studentNotifications);

        return "studentSchedule";
    }

    @GetMapping("/teacher/schedule")
    public String teacherSchedule(
            @RequestParam(name = "semester", defaultValue = "1") String semester,
            @RequestParam(name = "semesterPart", defaultValue = "first") String semesterPart,
            Model model) {

        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }

        int semesterNum;
        try {
            semesterNum = Integer.parseInt(semester);
            if (semesterNum != 1 && semesterNum != 2) {
                semesterNum = 1;
            }
        } catch (NumberFormatException e) {
            semesterNum = 1;
        }

        String validSemesterPart = (semesterPart.equals("second")) ? "second" : "first";

        List<Schedule> schedules = scheduleRepository.findSchedulesByTeacherAndSemesterAndSemesterPartOrAll(teacher, semesterNum, validSemesterPart);

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedSemester", semesterNum);
        model.addAttribute("selectedSemesterPart", validSemesterPart);


        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);

        model.addAttribute("teacherNotifications", notRead);


        return "teacherSchedule";
    }



}