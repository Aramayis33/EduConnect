package org.example.educonnectjavaproject.controller;


//import org.example.educonnectjavaproject.model.interfaces.GroupRepository;
import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.Teacher.Teacher;
import org.example.educonnectjavaproject.Teacher.TeacherRepository;
import org.example.educonnectjavaproject.feedback.Feedback;
import org.example.educonnectjavaproject.feedback.FeedbackRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpSession session;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;
    @Autowired
    FeedbackRepository feedbackRepository;


    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/home")
    public String logIn(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {

        Teacher teacher = teacherRepository.findTeacherByUsername(username);
        Student student = studentRepository.findStudentByEmail(username);
        if (teacher != null && encoder.matches(password, teacher.getPassword())) {

            List<GroupInfo>groupInfos=groupRepository.findAll();
            session.setAttribute("allGroups", groupInfos);

            session.setAttribute("teacher", teacher);
            session.setAttribute("subject", teacher.getSubject());
        //    Subject subject = subjectRepository.findByStudentId(student.getId());
            //  model.addAttribute("subject", subject);
            session.setAttribute("subject", teacher.getSubject());
          //  List<HomeWork> homeWorkList = homeWorkRepository.findHomeWorkByGroupNumber(student.getGroupInfo().getGroupNumber());
          List<HomeWork>homeWorkList=homeWorkRepository.findAll();
            //   model.addAttribute("homeWorks", homeWorkList);
            session.setAttribute("homeWorks", homeWorkList);


            return "redirect:/teacher";
        } else if (student != null && encoder.matches(password, student.getPassword())) {
            session.setAttribute("student", student);
            model.addAttribute("studentImage", student.getImage());
          //  session.setAttribute("studentImage", student.getImage());
            session.setAttribute("studentId", student.getId());
            session.setAttribute("subject", subjectRepository.findByStudentId(student.getId()));
            session.setAttribute("homeWorks", homeWorkRepository.findHomeWorkByGroupNumber(student.getGroupInfo().getGroupNumber()));
            return "redirect:/student";

        } else {
            return "redirect:/?loginError=true";


        }

    }


}

