package org.example.educonnectjavaproject.Teacher;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.subject.Subject;
import org.example.educonnectjavaproject.feedback.FeedbackRepository;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.subject.SubjectRepository;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpSession session;

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;
    @Autowired
    FeedbackRepository feedbackRepository;

    @GetMapping("/teacher")
    public String teacher(Model model) {
//        List<GroupInfo> groups = groupRepository.findAll();
//        session.setAttribute("allGroups", groups);
//
//        GroupInfo first = groupRepository.findFirstByOrderByGroupNumber();
//        List<Student> studentList = studentRepository.findStudentsByGroupInfo(first);
//        //   List<Student> studentList = studentRepository.findAllStudentsWithSubjects();
//        session.setAttribute("students", studentList);
//        //    session.setAttribute("subjects", subjectList);
        List<Subject>studentSubjectList=subjectRepository.findAllSubjectsWithStudents();
        session.setAttribute("subjects", studentSubjectList);
        model.addAttribute("subject", new Subject());
        return "teacherPage";

    }


    @PostMapping("/main")
    public String goBackTeacher(Model model) {

        return "redirect:/teacher";
    }

    @PostMapping("/update")
    public String update(@RequestParam("studentId") int studentId,
                         @RequestParam("mathValue") String mathValue,
                         @RequestParam("englishValue") String englishValue,
                         @RequestParam("javaValue") String javaValue,
                         Model model) {

          Subject subject=subjectRepository.findByStudentId(studentId);
       // Student student = studentRepository.findStudentById(studentId);

        String subjectName = (String) session.getAttribute("subject");
        //  System.out.println(subject.getMath()+" "+subject.getEnglish()+" "+subject.getJava());

        int mathGrade = Integer.parseInt(mathValue);
        subject.setMath(mathGrade);
        int englishGrade = Integer.parseInt(englishValue);
        subject.setEnglish(englishGrade);
        int javaGrade = Integer.parseInt(javaValue);
        subject.setJava(javaGrade);

        subjectRepository.save(subject);

        return "redirect:/teacher";

    }


}
