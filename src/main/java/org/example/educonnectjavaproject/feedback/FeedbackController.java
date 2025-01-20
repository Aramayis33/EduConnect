package org.example.educonnectjavaproject.feedback;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private HttpSession session;

    @GetMapping("/feedback")
    public String feedback(Model model) {
        return "feedback";
    }

    @PostMapping("/addFeedback")
    public String addFeedback(@RequestParam("title") String title, Model model) {

        int studentId = (int) session.getAttribute("studentId");
        LocalDate inputDate = LocalDate.now();
        Feedback feedback = new Feedback(studentId, java.sql.Date.valueOf(inputDate), title);
        feedbackRepository.save(feedback);


        return "studentPage";


    }
}
