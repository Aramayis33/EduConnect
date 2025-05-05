package org.example.educonnectjavaproject.controller;



import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.notifications.TeacherNotificationRepository;
import org.example.educonnectjavaproject.schedule.Schedule;
import org.example.educonnectjavaproject.schedule.ScheduleService;
import org.example.educonnectjavaproject.schedule.ScheduleWithDate;
import org.example.educonnectjavaproject.security.Admin;
import org.example.educonnectjavaproject.security.LogInHistory;
import org.example.educonnectjavaproject.security.LogInHistoryRepository;
import org.example.educonnectjavaproject.subject.TeacherSubjectGroupRepository;
import org.example.educonnectjavaproject.summary.SummaryResult;
import org.example.educonnectjavaproject.summary.SummaryResultRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.teacher.TeacherRepository;
import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.exercises.ExerciseRepository;
import org.example.educonnectjavaproject.feedback.FeedbackRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.ratings.RatingRepository;
import org.example.educonnectjavaproject.schedule.ScheduleRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.SubjectsRepository;
import org.example.educonnectjavaproject.summary.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AppController {

    @Autowired
    private TeacherRepository teacherRepository;


    @Autowired
    private StudentRepository studentRepository;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/")
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        String userId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (userId != null) {
            if (userId.startsWith("student_")) {
                String studentId = userId.substring("student_".length());
                Student student = studentRepository.findById(Integer.parseInt(studentId)).orElse(null);
                if (student != null && student.getIsActivated() == 1 && student.getIsBlocked() == 0) {
                    session.setAttribute("student", student);
                    return "redirect:/studentPage";
                }
            } else {
                Teacher teacher = teacherRepository.findById(Integer.parseInt(userId)).orElse(null);
                if (teacher != null && teacher.getIsActivated() == 1 && teacher.getIsBlocked() == 0 && teacher.getDeletedAt() == null) {
                    session.setAttribute("teacher", teacher);
                    return "redirect:/teacherPage";
                }
            }
        }

        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Student student = (Student) session.getAttribute("student");
        Admin admin = (Admin) session.getAttribute("admin");
        if (teacher != null) {
            return "redirect:/teacherPage";
        } else if (student != null) {
            return "redirect:/studentPage";
        } else if (admin != null) {
            return "redirect:/admin";
        }
        return "index";
    }

    @PostMapping("/home")
    public String logIn(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "remember-me", required = false) String rememberMe,
                        HttpSession session,
                        HttpServletResponse response,
                        HttpServletRequest request,
                        Model model) {
        session.invalidate();
        session = request.getSession(true);

        Teacher teacher = teacherRepository.findTeacherByEmailAndIsActivatedAndIsBlockedAndDeletedAtIsNull(username, 1, 0);
        Student student = studentRepository.findStudentByEmailAndIsActivatedAndIsBlocked(username, 1, 0);

        if (teacher != null && encoder.matches(password, teacher.getPassword())) {
            if("on".equals(rememberMe)) {


                Cookie cookie = new Cookie("userId", teacher.getId() + "");
                cookie.setMaxAge(30 * 24 * 60 * 60); //30
                cookie.setPath("/");
                cookie.setHttpOnly(false);
                cookie.setSecure(false );
                response.addCookie(cookie);
            }
            session.setAttribute("teacher", teacher);
            return "redirect:/teacherPage";
        } else if (student != null && encoder.matches(password, student.getPassword())) {
            if("on".equals(rememberMe)) {
                Cookie cookie = new Cookie("userId", "student_" + student.getId());
                cookie.setMaxAge(30 * 24 * 60 * 60);
                cookie.setPath("/");
                cookie.setHttpOnly(false);
                cookie.setSecure(false);
                response.addCookie(cookie);
            }
            session.setAttribute("student", student);
            return "redirect:/studentPage";
        } else {
            return "redirect:/?loginError=true";
        }
    }


}


