package org.example.educonnectjavaproject.feedback;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/feedback")
    public String feedback(Model model) {
        return "feedback";
    }



//    @PostMapping("/addFeedback")
//    public String addFeedback(@RequestParam("title") String title, Model model) {
//
//        int studentId = (int) session.getAttribute("studentId");
//        LocalDate inputDate = LocalDate.now();
//        Student student=studentRepository.findStudentById(studentId);
//        Feedback feedback = new Feedback(student, java.sql.Date.valueOf(inputDate), title);
//        feedbackRepository.save(feedback);
//
//
//        return "studentPage";
//
//
//    }

    @PostMapping("/feedback")
    public String sendFeedback(@RequestParam("message")String message, Model model) {
        Student student = (Student) session.getAttribute("student");
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(student==null&&teacher==null) {
            return "redirect:/";
        }
        if(message==null||message.isEmpty()||message==""||message.length()<5) {
            return "redirect:/feedback";
        }
        if(teacher!=null){
            Feedback feedback = new Feedback(null,teacher,new Timestamp(System.currentTimeMillis()),message);
            feedbackRepository.save(feedback);
        }
        else if(student!=null){
            Feedback feedback = new Feedback(student,null,new Timestamp(System.currentTimeMillis()),message);
            feedbackRepository.save(feedback);
        }
        return "redirect:/";


    }
}
