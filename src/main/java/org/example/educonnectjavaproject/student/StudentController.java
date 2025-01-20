package org.example.educonnectjavaproject.student;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentController {

    @Autowired
    private HttpSession session;
    @GetMapping("/logout")
    public String logOut(Model model) {
        session.invalidate();
        return "index";
    }

    @GetMapping("/student")
    public String studentMain(Model model) {
        return "studentPage";

    }
    @PostMapping("/student")
        public String studentMainPage(Model model){
        return "studentPage";
    }

    @PostMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

}
