package org.example.educonnectjavaproject.student;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.exercises.ExerciseRepository;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationReading;
import org.example.educonnectjavaproject.notifications.StudentNotificationReadingRepository;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.ratings.RatingRepository;
import org.example.educonnectjavaproject.security.LogInHistory;
import org.example.educonnectjavaproject.security.LogInHistoryRepository;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.SubjectsRepository;
import org.example.educonnectjavaproject.summary.SummaryResult;
import org.example.educonnectjavaproject.summary.SummaryResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private HttpSession session;
@Autowired
StudentNotificationRepository studentNotificationRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private StudentNotificationReadingRepository studentNotificationReadingRepository;

    @Autowired
    private SubjectsRepository subjectsRepository;

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private SummaryResultRepository summaryResultRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LogInHistoryRepository logInHistoryRepository;


    @GetMapping("/logout-all")
    public String logOut(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // Մաքրել session-ը
//        if (session != null) {
//            session.removeAttribute("student");
//            session.removeAttribute("teacher");
//            session.removeAttribute("admin");
//            session.invalidate();
//        }
//        assert session != null;
//        session.invalidate();
//        Cookie cookieToDelete = new Cookie("userId", null);
//        cookieToDelete.setMaxAge(0);
//        cookieToDelete.setPath("/");
//        response.addCookie(cookieToDelete);

        Cookie[]cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue(null);
                response.addCookie(cookie);
            }
        }
        session.invalidate();
        System.out.println("aral avvv arraaaa");

        return "redirect:/";
    }
//    @GetMapping("/logout")
//    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//
//        System.out.println("logout");
//        return "studentSchedule";
//    }


    @GetMapping("/student")
    public String studentMain(Model model) {
        if(session.getAttribute("student") == null) {
            return "redirect:/";
        }
        Student student = (Student) session.getAttribute("student");
        List<Exercise> exercises = exerciseRepository.findExercisesByStudent(student);
        List<HomeWork>activeHomeworks= homeWorkRepository.findActiveHomeWorksByGroupNumberAndExercises(student.getGroupInfo().getGroupNumber(), exercises);
        session.setAttribute("activeHomeworks",activeHomeworks);
        session.setAttribute("expiredHomeworks", homeWorkRepository.findExpiredHomeWorksByGroupNumberAndExercises(student.getGroupInfo().getGroupNumber(), exercises));
        List<StudentNotification>studentNotification=studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student,student.getGroupInfo(),studentNotificationReadingRepository);
        model.addAttribute("studentNotification", studentNotification);
        return "studentPage";

    }




    @GetMapping("studentPage")
    public String studentPage(Model model, HttpServletRequest request) {
        Student student= (Student) session.getAttribute("student");

        List<StudentNotification> studentNotification = studentNotificationRepository.findStudentNotificationsByStudentOrGroup(student, student.getGroupInfo());
        session.setAttribute("studentNotification", studentNotification);
        session.setAttribute("student", student);
        model.addAttribute("studentImage", student.getImage());
        session.setAttribute("studentId", student.getId());
        session.setAttribute("ratings", ratingRepository.findRatingsByStudent(student));
        List<Exercise> exercises = exerciseRepository.findExercisesByStudent(student);
        List<Subjects> subjects = subjectsRepository.findAll();
        session.setAttribute("subjectsForSearch", subjects);
        List<HomeWork> activeHomeworks = homeWorkRepository.findActiveHomeWorksByGroupNumberAndExercises(student.getGroupInfo().getGroupNumber(), exercises);
        session.setAttribute("activeHomeworks", activeHomeworks);
        session.setAttribute("expiredHomeworks", homeWorkRepository.findExpiredHomeWorksByGroupNumberAndExercises(student.getGroupInfo().getGroupNumber(), exercises));

        List<SummaryResult> summaries = summaryResultRepository.findSummaryResultByStudent(student);
        double avg = summaryResultRepository.findAverageByStudent(student) != null ? summaryResultRepository.findAverageByStudent(student) : 0;
        String status = "";
        if (avg == 0) {
            status = "Արդյունք չկա";
        } else if (avg < 4) {
            status = "Անբավարար";
        } else if (avg == 4) {
            status = "Բավարար";
        } else if (avg < 9) {
            status = "Հարվածային";
        } else {
            status = "Գերազանցիկ";
        }

        session.setAttribute("summary", summaries);
        session.setAttribute("status", status);

        List<Student> groupStudents = studentRepository.findStudentsByGroupInfo(student.getGroupInfo());
        session.setAttribute("groupStudents", groupStudents);
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());

        LogInHistory logInHistory=new LogInHistory(null,student,ipAddress,userAgent,loginTime);
        logInHistoryRepository.save(logInHistory);

        return "redirect:/student";
    }










    @PostMapping("/student/read-notification")
    public ResponseEntity<?> markNotificationsAsRead(@RequestBody List<Integer> notificationIds,Model model) {

        Student student=(Student) session.getAttribute("student");

      for(Integer notificationId : notificationIds) {

          StudentNotification studentNotification=studentNotificationRepository.findStudentNotificationById(notificationId);
          if(studentNotification!=null) {
              StudentNotificationReading studentNotificationRead=studentNotificationReadingRepository.findByStudentAndStudentNotification(student,studentNotification);
              if(studentNotificationRead==null) {
                  StudentNotificationReading studentNotificationReading=new StudentNotificationReading(student,studentNotification);
                  studentNotificationReadingRepository.save(studentNotificationReading);
              }

          }
      }
      return ResponseEntity.ok().build();

    }


    @PostMapping("/student")
        public String studentMainPage(Model model){
        return "studentPage";
    }

    @PostMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

    @GetMapping("student/settings")
    public String settings(Model model) {
        Student student = (Student) session.getAttribute("student");
        if(student == null) {
            return "redirect:/";
        }
        List<StudentNotification>studentNotifications=studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student,student.getGroupInfo(),studentNotificationReadingRepository);
        model.addAttribute("studentNotification", studentNotifications);
        return "studentsettings";
    }
    @PostMapping("student/settings")
    public String settings(Model model,String a) {
        Student student = (Student) session.getAttribute("student");
        if(student == null) {
            return "redirect:/";
        }
        List<StudentNotification>studentNotifications=studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student,student.getGroupInfo(),studentNotificationReadingRepository);
        model.addAttribute("studentNotification", studentNotifications);
        return "studentsettings";
    }

    @GetMapping("student/notifications")
    public String notifications(Model model) {
        Student student = (Student) session.getAttribute("student");
        if(student == null) {
            return "redirect:/";
        }
        List<StudentNotification>studentNotifications=studentNotificationRepository.findStudentNotificationsByStudentOrGroup(student,student.getGroupInfo());
      studentNotifications=studentNotificationRepository.sortStudentNotificationsByDate(studentNotifications);
        model.addAttribute("studentNotifications", studentNotifications);
        return "studentnotifications";
    }






}
