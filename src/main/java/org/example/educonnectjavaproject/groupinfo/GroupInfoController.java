//package org.example.educonnectjavaproject.groupinfo;
//
//import jakarta.servlet.http.HttpSession;
//import org.example.educonnectjavaproject.subject.Subject;
//import org.example.educonnectjavaproject.subject.SubjectRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
//@Controller
//public class GroupInfoController {
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    @Autowired
//    private HttpSession session;
//
//    @GetMapping("/teacher/group")
//    public String groups(@RequestParam("filterGroup") String groupNumber, Model model) {
//        GroupInfo groupInfo = groupRepository.findGroupByGroupNumber(Integer.parseInt(groupNumber));
//        List<Subject> filterStudents = subjectRepository.findSubjectAndStudentByGroup(groupInfo);
//        session.setAttribute("subjects", filterStudents);
//        return "teacherPage";
//
//    }
//}
