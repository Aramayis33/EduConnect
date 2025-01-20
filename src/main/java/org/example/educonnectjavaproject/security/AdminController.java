package org.example.educonnectjavaproject.security;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.example.educonnectjavaproject.subject.Subject;
import org.example.educonnectjavaproject.subject.SubjectRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.Teacher.Teacher;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.Teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private HttpSession session;

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private SubjectRepository subjectRepository;

    private final String UPLOAD_DIR="src/main/resources/static/images/";

    @GetMapping("/admin")
    public String adminLogin(Model model) {

        model.addAttribute("admin", new Admin());
        return "adminlogin";
    }
    @PostMapping("/admin/main")
    public String adminMain(Model model, Admin admin) {
        List<Admin>admins = userService.findUserByLogin(admin.getUsername(),admin.getPassword());
        if (!admins.isEmpty() ) {
            session.setAttribute("admin", admin);
            return "redirect:/admin/main";

        } else {

            return "redirect:/admin?loginError=true";


        }

    }
    @GetMapping("/admin/main")
    public String adminPage(Model model) {

        List<Student> allStudents = studentRepository.findAll();
        session.setAttribute("student", allStudents);
        List<Teacher> teacherList = teacherRepository.findAll();
        session.setAttribute("teacher", teacherList);
        model.addAttribute("students", allStudents);
        model.addAttribute("teachers", teacherList);
        List<GroupInfo>groups = groupRepository.findAll();
        model.addAttribute("allGroups", groups);
        session.setAttribute("allGroups", groups);

        return "adminPage";
    }

    @PostMapping("/admin/delete/student")
    @Transactional
    public String deleteStudent(@RequestParam("studentId") String id, Model model) {
        int studentId = Integer.parseInt(id);
        subjectRepository.deleteByStudentId(studentId);
        studentRepository.deleteById(studentId);
        return "redirect:/admin/main";


    }


    @PostMapping("/addStudentSubmit")
    public String addStudentSubmit(
            Student student, Model model, @RequestParam("photo") MultipartFile file,@RequestParam("image") String oldImage) {
        String fileName= StringUtils.cleanPath(file.getOriginalFilename());
        if(fileName!=null && !fileName.equals("")) {
            student.setImage(fileName);
        }else{
       //     student.setImage("default.png");
            student.setImage(oldImage);

        }
        Subject sub = subjectRepository.findByStudentId(student.getId());
        if(sub==null) {
            Subject subject=new Subject(0,0,0,student);
            subjectRepository.save(subject);

        }
        if(student.getPassword()!=null&&student.getPassword().isEmpty()) {
            student.setPassword(encoder.encode(student.getPassword()));
        }
        studentRepository.save(student);

        try{

            Path path= Paths.get(UPLOAD_DIR+fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        }
        catch(IOException e){

            e.printStackTrace();
        }



        return "redirect:/admin/main";
    }
    @PostMapping("/admin/edit/student")
    public String updateStudent(@RequestParam("stId") String id,Model model) {
        int studentId = Integer.parseInt(id);
        Student student = studentRepository.findStudentById(studentId);
        model.addAttribute("studentUpdate", student);
        session.setAttribute("studentOldImage", student.getImage());
        model.addAttribute("student", student);
        List<GroupInfo>groups = groupRepository.findAll();
        model.addAttribute("allGroups", groups);
        return "updateStudent";
    }
    @PostMapping("/admin/new/student")
    public String addStudentPage(Model model) {
        model.addAttribute("groups",groupRepository.findAll());
        model.addAttribute("student",new Student());
        return "addStudent";
    }




    @PostMapping("/admin/delete/teacher")
    public String deleteTeacher(@RequestParam("teacherId") String id, Model model) {
        int teacherId = Integer.parseInt(id);
        teacherRepository.deleteById(teacherId);

        return "redirect:/admin/main";
    }



    @PostMapping("/admin/new/teacher")
    public String addTeacherPage() {
        return "addTeacher";
    }


    @PostMapping("/addTeacherSubmit")
    public String addTeacherSubmit( @RequestParam("teName") String name,
                                    @RequestParam("teSurname") String surname,
                                    @RequestParam("teUsername")String username,
                                    @RequestParam("teSubject")String subject,
                                    @RequestParam("tePassword")String password,  @RequestParam("oldPassword")String oldPassword,
                                    @RequestParam("teId") int teId, Model model) {

        String hashedPassword;
        if(!oldPassword.equals("0")){
            hashedPassword=oldPassword;
        }
        else{
            hashedPassword =encoder.encode(password);
        }
        Teacher teacher=new Teacher(name,surname,username,subject,hashedPassword);
        if(teId!=0){
            teacherRepository.deleteById(teId);
        }
        teacherRepository.save(teacher);

        return "redirect:/admin/main";
    }

//    @GetMapping("/admin")
//    public String admin(Model model) {
//        List<Student> allStudents = studentRepository.findAll();
//        session.setAttribute("student", allStudents);
//        List<Teacher> teacherList = teacherRepository.findAll();
//        session.setAttribute("teacher", teacherList);
//        List<GroupInfo>groupInfos=groupRepository.findAll();
//        session.setAttribute("allGroups", groupInfos);
//        return "adminPage";
//    }



    @PostMapping("/admin/edit/teacher")
    public String updateTeacher(@RequestParam("teId") String id,Model model) {
        int teacherId = Integer.parseInt(id);
        Teacher teacher=teacherRepository.findTeacherById(teacherId);
        model.addAttribute("teacherUpdate", teacher);
        return "updateTeacher";

    }




    @GetMapping("admin/new/group")
    public String newGroup(Model model) {
        model.addAttribute("groupInfo",new GroupInfo());
        return "groupform";
    }

    @PostMapping("/admin/save/group")
    public String save(GroupInfo groupInfo) {
        groupRepository.save(groupInfo);
        return "redirect:/admin";
    }



}
