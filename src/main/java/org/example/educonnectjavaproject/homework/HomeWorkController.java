package org.example.educonnectjavaproject.homework;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class HomeWorkController {


    @Autowired
    private HttpSession session;
    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @GetMapping("/teacher/new/homework")
    public String homework(Model model) {
        model.addAttribute("subject", session.getAttribute("subject"));

        return "addHomeWork";
    }

    @PostMapping("addHomework")
    public String homeWorkPage(@RequestParam("groupNumber") String groupNumber, @RequestParam("title") String title, Model model) {

        int group = Integer.parseInt(groupNumber);
        String subjectName = (String) session.getAttribute("subject");
        LocalDate inputDate = LocalDate.now();
        HomeWork homeWork = new HomeWork(group, title, java.sql.Date.valueOf(inputDate), subjectName);
        homeWorkRepository.save(homeWork);

        return "redirect:/teacher";

    }
}
