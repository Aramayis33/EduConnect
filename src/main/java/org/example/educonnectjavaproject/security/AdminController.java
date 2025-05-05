package org.example.educonnectjavaproject.security;

import com.fasterxml.jackson.core.ObjectCodec;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.example.educonnectjavaproject.feedback.Feedback;
import org.example.educonnectjavaproject.feedback.FeedbackRepository;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.library.Library;
import org.example.educonnectjavaproject.library.LibraryRepository;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.notifications.TeacherNotification;
import org.example.educonnectjavaproject.notifications.TeacherNotificationRepository;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.ratings.RatingRepository;
import org.example.educonnectjavaproject.schedule.Schedule;
import org.example.educonnectjavaproject.schedule.ScheduleController;
import org.example.educonnectjavaproject.schedule.ScheduleRepository;
import org.example.educonnectjavaproject.schedule.ScheduleService;
import org.example.educonnectjavaproject.subject.*;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.summary.SummaryResult;
import org.example.educonnectjavaproject.summary.SummaryResultRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.teacher.TeacherController;
import org.example.educonnectjavaproject.teacher.TeacherRepository;
import org.example.educonnectjavaproject.teacher.TeacherWithSubjects;
import org.example.educonnectjavaproject.videolessons.VideoLesson;
import org.example.educonnectjavaproject.videolessons.VideoLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@Controller
public class AdminController {
    //
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
    //
    @Autowired
    private UserService userService;

    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private VideoLessonRepository videoLessonRepository;

    @Autowired
    private SummaryResultRepository resultRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private LogInHistoryRepository logInHistoryRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private SummaryResultRepository summaryResultRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TeacherSubjectGroupRepository teacherSubjectGroupRepository;

    @Autowired
    private SubjectGroupRepository subjectGroupRepository;

    private ScheduleService scheduleService = new ScheduleService();

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StudentNotificationRepository studentNotificationRepository;
    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;


    private EmailSenderService emailSenderService = new EmailSenderService();

    private PasswordGenerationService passwordGenerationService = new PasswordGenerationService();

    //
//    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    private final String UPLOAD_DIR="src/main/resources/static/images/";
//
    @GetMapping("/admin")
    public String adminLogin(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return "redirect:/admin/home";
        }
        Student student=(Student) session.getAttribute("student");
        Teacher teacher=(Teacher) session.getAttribute("teacher");
        if(teacher!=null||student!=null) {
            return "redirect:/";
        }

        model.addAttribute("admin", new Admin());
        return "adminlogin";
    }

    @PostMapping("/admin/home")
    public String adminMain(Model model, Admin admin) {

        Admin admin1 = userService.findUserByUsername(admin.getUsername());
        if (admin1 != null) {
            if (encoder.matches(admin.getPassword(), admin1.getPassword())) {
                session.setAttribute("admin", admin1);
                return "redirect:/admin/home";
            }

        }
        return "redirect:/admin?loginError=true";

    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }

        List<Teacher> teachers = teacherRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<GroupInfo> groupInfos = groupRepository.findAll();
        List<Library> libraries = libraryRepository.findAll();
        List<VideoLesson> videoLessons = videoLessonRepository.findAll();
        List<Admin> admins = adminRepository.findAll();
        List<Subjects> subjects = subjectsRepository.findAll();
        List<Schedule> schedules = scheduleRepository.findAll();
        Map<String, Integer> subjectOccupationMap = subjectsRepository.getSubjectOccupation(schedules); // Փոխված տիպ
        session.setAttribute("subjectOccupationMap", subjectOccupationMap);
        session.setAttribute("teacherCount", teachers.size());
        session.setAttribute("studentCount", students.size());
        session.setAttribute("groupCount", groupInfos.size());
        session.setAttribute("librariesCount", libraries.size());
        session.setAttribute("videoLessonCount", videoLessons.size());
        session.setAttribute("adminCount", admins.size());
        session.setAttribute("subjectCount", subjects.size());
        model.addAttribute("activePage", "home");
        int excelentCount = resultRepository.excellentCount(students);
        int intermediateCount = resultRepository.intermediateCount(students);
        int sufficientCount = resultRepository.sufficientCount(students);
        int inSufficientCount = resultRepository.inSufficientCount(students);
        session.setAttribute("excelentCount", excelentCount);
        session.setAttribute("intermediateCount", intermediateCount);
        session.setAttribute("sufficientCount", sufficientCount);
        session.setAttribute("inSufficientCount", inSufficientCount);

        Map<Integer, Integer> currentHomeWorkCount = homeWorkRepository.getHomeWorkCount(groupInfos, "current");
        Map<Integer, Integer> allTimeHomeWorkCount = homeWorkRepository.getHomeWorkCount(groupInfos, "all");
        session.setAttribute("currentHomeWorkCount", currentHomeWorkCount);
        session.setAttribute("allTimeHomeWorkCount", allTimeHomeWorkCount);

        Map<String, Integer> teacherClassHours = teacherRepository.getTeacherClassHourCount(schedules);
        session.setAttribute("teacherClassHours", teacherClassHours);

        session.setAttribute("admin", admin);
        return "adminPage";
    }

    @GetMapping("/admin/students")
    public String students(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        model.addAttribute("activePage", "students");
        List<Student> students = studentRepository.findAll();
        List<GroupInfo> groupInfos = groupRepository.findAll();
        session.setAttribute("allGroups", groupInfos);
        session.setAttribute("allStudents", students);
        return "adminStudents";
    }

    @PostMapping("/toggle-student-activation")
    @ResponseBody
    public Map<String, Object> toggleStudentActivation(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("admin");
            if (admin == null) {
                response.put("success", false);
                response.put("message", "Դուք մուտք չեք գործել");
                return response;
            }

            Integer id = Integer.parseInt(payload.get("id").toString());
            int isBlocked = Integer.parseInt(payload.get("isActivated").toString());

            Student student = studentRepository.findById(id).orElse(null);
            if (student == null) {
                response.put("success", false);
                response.put("message", "Ուսանողը չի գտնվել");
                return response;
            }

            student.setIsBlocked(isBlocked);
            studentRepository.save(student);

            response.put("success", true);
            response.put("message", "Կարգավիճակը հաջողությամբ փոխվել է");
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Սխալ ID կամ կարգավիճակի արժեք");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Սխալ է տեղի ունեցել: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/admin/students/new")
    @ResponseBody
    public Map<String, Object> newStudent(@RequestParam("name") String name, @RequestParam("surname") String sname,
                                          @RequestParam("email") String email, @RequestParam("birthDay") Date birthDate,
                                          @RequestParam("fee") int fee, @RequestParam("groupId") int groupId) {

        Map<String, Object> response = new HashMap<>();
        Student student = studentRepository.findStudentByEmail(email);
        Teacher teacher = teacherRepository.findTeacherByEmail(email);
        if (student != null || teacher != null) {
            response.put("success", false);
            response.put("message", "Էլ փոստն արդեն օգտագործված է");
            return response;
        }

        if (name == null || sname == null || email == null || birthDate == null) {
            response.put("success", false);
            response.put("message", "Կան չլրացված դաշտեր");
            return response;
        }
        GroupInfo groupInfo = groupRepository.findGroupById(groupId);
        if (groupInfo == null) {
            response.put("success", false);
            response.put("message", "Խումբը չգտնվեց");
            return response;
        }
        Student student1 = new Student(name, sname, birthDate, fee, email, null, groupInfo, "default.jpg",
                0, 0);
        studentRepository.save(student1);
        response.put("success", true);
        response.put("message", "Ուսանողը հաջողությամբ ստեղծվեց");
        return response;

    }

    @PostMapping("/admin/students/update")
    @ResponseBody
    public Map<String, Object> updateStudent(@RequestParam("name") String name, @RequestParam("surname") String sname,
                                             @RequestParam("email") String email, @RequestParam("birthDay") Date birthDate,
                                             @RequestParam("fee") int fee, @RequestParam("groupId") int groupId, @RequestParam("id") int id) {

        Map<String, Object> response = new HashMap<>();
        Student student1 = studentRepository.findStudentById(id);
        if (student1 == null) {
            response.put("success", false);
            response.put("message", "student not found");
        }

        Student student = studentRepository.findStudentByEmail(email);
        if (student != null && !student1.getEmail().equals(email)) {
            response.put("success", false);
            response.put("message", "Էլ փոստն արդեն օգտագործված է");
            return response;
        }

        if (name == null || sname == null || email == null || birthDate == null) {
            response.put("success", false);
            response.put("message", "Կան չլրացված դաշտեր");
            return response;
        }
        GroupInfo groupInfo = groupRepository.findGroupById(groupId);
        if (groupInfo == null) {
            response.put("success", false);
            response.put("message", "Խումբը չգտնվեց");
            return response;
        }
        student1.setName(name);
        student1.setSurname(sname);
        student1.setEmail(email);
        student1.setBirthDay(birthDate);
        student1.setFee(fee);
        student1.setGroupInfo(groupInfo);
        studentRepository.save(student1);
        response.put("success", true);
        response.put("message", "Ուսանողի տվյալները հաջողությամբ թարմացվեցին");
        return response;

    }


    @GetMapping("/admin/students/{id}")
    public String students(@PathVariable Integer id, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        Student student = studentRepository.findStudentById(id);
        if (student == null) {
            return "redirect:/admin/students";
        }
        List<LogInHistory> logInHistories = logInHistoryRepository.findLogInHistoriesByStudent(student);
        List<SummaryResult> summaryResults = summaryResultRepository.findSummaryResultByStudent(student);
        List<Feedback> feedbacks = feedbackRepository.findFeedbacksByStudentId(student.getId());
        int absentCount = ratingRepository.findRatingsByStudentAndRating(student, 0).size();

        session.setAttribute("logInHistories", logInHistories);
        session.setAttribute("summaryResults", summaryResults);
        session.setAttribute("feedbacks", feedbacks);
        session.setAttribute("absentCount", absentCount);
        model.addAttribute("activePage", "students");
        session.setAttribute("viewStudent", student);

        return "updateStudent";
    }

    @PostMapping("/admin/students/delete")
    public String deleteStudent(@RequestParam("id") int id, Model model) {
        Student student = studentRepository.findStudentById(id);
        if (student == null) {
            return "redirect:/admin/students";
        }
        studentRepository.delete(student);
        return "redirect:/admin/students";
    }


    //ՈՒՍՈՒԻՉՆԵՐԻ ՄԵԹՈԴՆԵՐ


    @GetMapping("/admin/teachers")
    public String teachers(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }

        List<Teacher> teachers = teacherRepository.findAllByDeletedAtIsNull();
        List<Subjects> allSubjects = subjectsRepository.findAll(); // Բոլոր առարկաները
        List<TeacherWithSubjects> teacherWithSubjects = new ArrayList<>();

        for (Teacher teacher : teachers) {
            List<Subjects> subjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
            teacherWithSubjects.add(new TeacherWithSubjects(teacher, subjects));
        }

        model.addAttribute("activePage", "teachers");
        model.addAttribute("teacherWithSubjects", teacherWithSubjects);
        model.addAttribute("allSubjects", allSubjects); // Փոխանցել առարկաները

        return "adminTeachers";
    }

    @PostMapping("/admin/teachers/new")
    @ResponseBody
    public Map<String, Object> newTeacher(@RequestParam("name") String name, @RequestParam("surname") String sname,
                                          @RequestParam("email") String email,
                                          @RequestParam("subjectIds") String subjectIds) {

        Map<String, Object> response = new HashMap<>();
        Teacher teacher = teacherRepository.findTeacherByEmail(email);
        Student student = studentRepository.findStudentByEmail(email);
        if (teacher != null || student != null) {
            response.put("success", false);
            response.put("message", "Էլ փոստն արդեն օգտագործված է");
            return response;
        }

        if (name == null || sname == null || email == null || subjectIds == null) {
            response.put("success", false);
            response.put("message", "Կան չլրացված դաշտեր");
            return response;
        }

        String[] arr = subjectIds.split(",");
        SubjectGroup subjectGroup = new SubjectGroup();
        subjectGroupRepository.save(subjectGroup);
        Teacher teacher1 = new Teacher(name, sname, email, subjectGroup, "", 0, "default.jpg", 0, null);
        teacherRepository.save(teacher1);
        for (int i = 0; i < arr.length; i++) {
            int subId = Integer.parseInt(arr[i]);
            Subjects subjects = subjectsRepository.findSubjectById(subId);
            teacherSubjectGroupRepository.save(new TeacherSubjectGroup(subjectGroup, subjects));
        }
        response.put("success", true);
        response.put("message", "Ուսուցիչը հաջողությամբ ստեղծվեց");
        return response;

    }

    @PostMapping("/toggle-teacher-activation")
    @ResponseBody
    public Map<String, Object> toggleTeacherActivation(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("admin");
            if (admin == null) {
                response.put("success", false);
                response.put("message", "Դուք մուտք չեք գործել");
                return response;
            }

            Integer id = Integer.parseInt(payload.get("id").toString());
            int isBlocked = Integer.parseInt(payload.get("isBlocked").toString());

            Teacher teacher = teacherRepository.findById(id).orElse(null);
            if (teacher == null) {
                response.put("success", false);
                response.put("message", "Ուսուցիչը չի գտնվել");
                return response;
            }

            teacher.setIsBlocked(isBlocked);
            teacherRepository.save(teacher);

            response.put("success", true);
            response.put("message", "Կարգավիճակը հաջողությամբ փոխվել է");
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Սխալ ID կամ կարգավիճակի արժեք");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Սխալ է տեղի ունեցել: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/admin/teachers/{id}")
    public String teachers(@PathVariable Integer id, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        Teacher teacher = teacherRepository.findTeacherById(id);
        if (teacher == null) {
            return "redirect:/admin/teachers";
        }
        List<LogInHistory> logInHistories = logInHistoryRepository.findLogInHistoriesByTeacher(teacher);
//        List<SummaryResult>summaryResults=summaryResultRepository.findSummaryResultByStudent(student);
        List<Feedback> feedbacks = feedbackRepository.findFeedbacksByTeacher(teacher);
//        int absentCount=ratingRepository.findRatingsByStudentAndRating(student,0).size();

        model.addAttribute("viewTeacher", teacher);
        model.addAttribute("logInHistories", logInHistories);
//        session.setAttribute("summaryResults", summaryResults);
        model.addAttribute("feedbacks", feedbacks);
//        session.setAttribute("absentCount", absentCount);
        model.addAttribute("activePage", "students");
//        session.setAttribute("viewStudent", student);

        List<Subjects> teacherSubjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        model.addAttribute("teacherSubjects", teacherSubjects);

        Map<String, Integer> subjectHours = new HashMap<>();
        for (int i = 0; i < teacherSubjects.size(); i++) {

            List<Schedule> schedules = scheduleRepository.findSchedulesByTeacherAndSubject(teacher, teacherSubjects.get(i));
            subjectHours.put(teacherSubjects.get(i).getName(), schedules.size());
        }
        model.addAttribute("subjectHours", subjectHours);

        List<Schedule> teacherGroups = scheduleRepository.findTeacherScheduleGroups(teacher);
        model.addAttribute("teacherGroups", teacherGroups);

        List<Subjects> allSubjects = subjectsRepository.findAll();
        model.addAttribute("allSubjects", allSubjects);

        return "updateTeacher";
    }


    @PostMapping("/admin/teachers/delete")
    public String deleteTeacher(@RequestParam("id") int id, Model model) {
        Teacher teacher = teacherRepository.findTeacherById(id);
        if (teacher == null) {
            return "redirect:/admin/teachers";
        }
        teacher.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        teacherRepository.save(teacher);
        return "redirect:/admin/students";
    }

    @PostMapping("/admin/teachers/{teacherId}/delete-subject/{subjectId}")
    @ResponseBody
    @Transactional
    public Map<String, Object> deleteTeacherSubject(@PathVariable Integer subjectId, @PathVariable Integer teacherId, Model model) {
        Map<String, Object> response = new HashMap<>();

        Teacher teacher = teacherRepository.findTeacherById(teacherId);
        if (teacher == null) {
            response.put("success", false);
            response.put("message", "Առկա է սխալմունք");
            return response;
        }
        List<Subjects> teacherSubjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        System.out.println(teacherSubjects.size() + " esa chapy axper");
        if (teacherSubjects.size() <= 1) {
            response.put("success", false);
            response.put("message", "Դասախոսը պիտի ունենա կցված առնվազն 1 առարկա");
            return response;
        }

        Subjects subject = subjectsRepository.findSubjectById(subjectId);
        if (subject == null) {
            response.put("success", false);
            response.put("message", "Առարկան չի գտնվել");
            return response;
        }

        try {
            teacherSubjectGroupRepository.deleteBySubjectGroupAndSubject(teacher.getSubjectGroup(), subject);
            response.put("success", true);
            response.put("message", "Առարկան հաջողությամբ ջնջվել է");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Սխալ ջնջման ընթացքում: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/admin/teachers/update")
    public String updateTeacher(@RequestParam("id") String id, @RequestParam("name") String name,
                                @RequestParam("surname") String surname, @RequestParam("email") String email,
                                @RequestParam("newSubjectId") String newSubId) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        int teacherId;
        int subjectId;
        try {
            teacherId = Integer.parseInt(id);
            subjectId = Integer.parseInt(newSubId);
        } catch (NumberFormatException e) {
            return "redirect:/admin/teachers/" + id;
        }
        Teacher teacher = teacherRepository.findTeacherById(teacherId);
        if (teacher == null) {
            return "redirect:/admin/teachers";
        }
        if ((Object) subjectId != null) {
            Subjects subjects = subjectsRepository.findSubjectById(subjectId);
            if (subjects != null) {
                TeacherSubjectGroup existingSubject = teacherSubjectGroupRepository.findSubjectBySubjectGroupAndSubject(teacher.getSubjectGroup(), subjects);
                if (existingSubject != null) {
                    if (!subjects.equals(existingSubject.getSubject())) {
                        teacherSubjectGroupRepository.save(new TeacherSubjectGroup(teacher.getSubjectGroup(), subjects));
                    }
                } else {
                    teacherSubjectGroupRepository.save(new TeacherSubjectGroup(teacher.getSubjectGroup(), subjects));

                }
            }
        }
        teacher.setName(name);
        teacher.setSurname(surname);
        teacher.setEmail(email);
        teacherRepository.save(teacher);
        return "redirect:/admin/teachers/" + teacher.getId();
    }


    //ԽՄԲԵՐԻ ՄԵԹՈԴՆԵՐ


    @GetMapping("admin/groups")
    public String groups(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        List<GroupInfo> groupInfos = groupRepository.findAll();
        Map<Integer, int[]> groupInfoMap = new HashMap<>();
        for (int i = 0; i < groupInfos.size(); i++) {
            int studentCount = studentRepository.findStudentsByGroupInfo(groupInfos.get(i)).size();
            int classHours = scheduleRepository.findSchedulesByGroup(groupInfos.get(i)).size();
            groupInfoMap.put(groupInfos.get(i).getGroupNumber(), new int[]{studentCount, classHours});
        }
        model.addAttribute("groupInfoMap", groupInfoMap);
        model.addAttribute("activePage", "groups");
        return "adminGroupPage";

    }


    @PostMapping("/admin/groups/new")
    @ResponseBody
    public Map<String, Object> create(@RequestParam("groupNumber") String groupNumber) {
        Map<String, Object> response = new HashMap<>();
        int newGroupNumber;
        try {
            newGroupNumber = Integer.parseInt(groupNumber);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Մուտքագրման դաշտում անհրաժեշտ է մուտքագրել միայն թվային արժեքներ");
            return response;
        }
        GroupInfo groupInfo = groupRepository.findGroupByGroupNumber(newGroupNumber);
        if (groupInfo != null) {
            response.put("success", false);
            response.put("message", "Տվյալ խումբն արդեն գոյություն ունի");
            return response;
        }
        GroupInfo newGroup = new GroupInfo(newGroupNumber);
        groupRepository.save(newGroup);
        response.put("success", true);
        response.put("message", "Խումբը հաջողությամբ ավելացվեց!");
        return response;
    }

    @PostMapping("/admin/groups/{groupNumber}/delete")
    @ResponseBody
    @Transactional
    public Map<String, Object> deleteGroup(@PathVariable Integer groupNumber) {
        Map<String, Object> response = new HashMap<>();
        GroupInfo groupInfo = groupRepository.findGroupByGroupNumber(groupNumber);
        if (groupInfo == null) {
            response.put("success", false);
            response.put("message", "Խումբը գոյություն չունի");
            return response;
        }
        List<Student> students = studentRepository.findStudentsByGroupInfo(groupInfo);
        if (!students.isEmpty()) {
            response.put("success", false);
            response.put("message", "Խումբը հնարավոր չէ հեռացնել, քանի որ խմբին կան կցված ուսանողներ");
            return response;
        }
        scheduleRepository.deleteSchedulesByGroup(groupInfo);
        groupRepository.delete(groupInfo);
        response.put("success", true);
        response.put("message", "Խումբը հաջողությամբ հեռացվեց");
        return response;

    }


    //ԱՌԱՐԿԱՆԵՐԻ ՄԵԹՈԴՆԵՐ

    @GetMapping("admin/subjects")
    public String subjectPage(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }

        List<Subjects> subjects = subjectsRepository.findAll();
        Map<String, String[]> subjectsMap = new HashMap<>();

        if (subjects != null && !subjects.isEmpty()) {
            for (Subjects subject : subjects) {
                if (subject == null || subject.getName() == null) {
                    continue;
                }

                Double averageGrade = summaryResultRepository.findAveageGradeBySubject(subject);
                String averageGradeStr = averageGrade != null && !Double.isNaN(averageGrade)
                        ? String.format("%.2f", averageGrade)
                        : "Հաշվարկ չկա";

                List<Schedule> schedules = scheduleRepository.findSchedulesBySubject(subject);
                int classHours = schedules != null ? schedules.size() : 0;

                subjectsMap.put(subject.getName(), new String[]{
                        (Object) subject.getId() != null ? subject.getId() + "" : "0",
                        averageGradeStr,
                        String.valueOf(classHours)
                });
            }
        }

        model.addAttribute("subjectInfoMap", subjectsMap);
        model.addAttribute("activePage", "subjects");
        return "adminSubjectPage";
    }

    @PostMapping("/admin/subjects/new")
    @ResponseBody
    public Map<String, Object> newSubject(@RequestParam("subjectName") String subjectName) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "admin-ը մուտք չի գործել");
            return response;
        }
        if (subjectName.isEmpty()) {
            response.put("success", false);
            response.put("message", "Լրացրեք բոլոր դաշտերը");
            return response;
        }
        subjectName = capitalize(subjectName);
        Subjects subjects = subjectsRepository.findSubjectByName(subjectName);
        if (subjects != null) {
            response.put("success", false);
            response.put("message", "Այդ առարկան արդեն գոյություն ունի");
            return response;
        }
        Subjects newSubject = new Subjects(subjectName);
        subjectsRepository.save(newSubject);
        response.put("success", true);
        response.put("message", "Առարկան հաջողությամբ ավելացվեց");
        return response;
    }


    @PostMapping("/admin/subjects/update")
    @ResponseBody
    public Map<String, Object> updateSubject(@RequestParam("subjectId") String subId, @RequestParam("subjectName") String subjectName) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "admin-ը մուտք չի գործել");
            return response;
        }
        int subjectId;
        try {
            subjectId = Integer.parseInt(subId);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "invalid format");
            return response;
        }
        if (subjectName.isEmpty()) {
            response.put("success", false);
            response.put("message", "Խնդրում ենք լրացնել դաշտը");
            return response;
        }
        Subjects subjects = subjectsRepository.findSubjectById(subjectId);
        if (subjects == null) {
            response.put("success", false);
            response.put("message", "Առարկան չի գտնվել");
            return response;
        }
        subjectName = capitalize(subjectName);
        Subjects subject = subjectsRepository.findSubjectByName(subjectName);
        if (subject != null) {
            response.put("success", false);
            response.put("message", "Տվյալ առարկան արդեն գոյություն ունի");
            return response;
        }
        subjects.setName(subjectName);
        subjectsRepository.save(subjects);
        response.put("success", true);
        response.put("message", "action done");
        return response;


    }

    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


    //Խմբերի մեթոդներ


    @GetMapping("/admin/schedule")
    public String schedule(
            @RequestParam(name = "semester", defaultValue = "1") String semester,
            @RequestParam(name = "semesterPart", defaultValue = "first") String semesterPart,
            @RequestParam(value = "groupNumber", defaultValue = "0") String group,
            Model model,
            HttpSession session) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }

        int semesterNum;
        try {
            semesterNum = Integer.parseInt(semester);
            if (semesterNum != 1 && semesterNum != 2) {
                semesterNum = 1;
            }
        } catch (NumberFormatException e) {
            semesterNum = 1;
        }

        String validSemesterPart = semesterPart.equals("second") ? "second" : "first";

        int groupNumber;
        try {
            groupNumber = Integer.parseInt(group);
        } catch (NumberFormatException e) {
            groupNumber = 0;
        }

        List<Schedule> schedules = new ArrayList<>();
        GroupInfo selectedGroup = null;
        if (groupNumber > 0) {
            selectedGroup = groupRepository.findGroupByGroupNumber(groupNumber);
            if (selectedGroup != null) {
                schedules = scheduleRepository.findSchedulesByGroupAndSemesterAndSemesterPartOrAll(selectedGroup, semesterNum, semesterPart);

            }
        }

        List<GroupInfo> groups = groupRepository.findAll();
        List<Subjects> subjects = subjectsRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        model.addAttribute("groups", groups);
        model.addAttribute("schedules", schedules);
        model.addAttribute("subjects", subjects);
        model.addAttribute("teachers", teachers);
        model.addAttribute("selectedSemester", semesterNum);
        model.addAttribute("selectedSemesterPart", validSemesterPart);
        model.addAttribute("selectedGroup", groupNumber);
        model.addAttribute("activePage", "schedule");

        return "adminSchedule";
    }


    @PostMapping("/admin/schedule/new")
    @ResponseBody
    public Map<String, Object> newSchedule(@RequestParam("classHour") String classHour, @RequestParam("weekday") String weekday,
                                           @RequestParam("classroom") String classroom, @RequestParam("groupNumber") String groupNumber,
                                           @RequestParam("subjectId") String subjectId, @RequestParam("teacherId") String teacherId, @RequestParam("semester") String semester,
                                           @RequestParam("semesterPart") String semesterPart) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need authorization");
            return response;
        }
        int cHour, weekDays, groupsNumber, subId, teachId, sem;
        try {
            cHour = Integer.parseInt(classHour);
            weekDays = Integer.parseInt(weekday);
            groupsNumber = Integer.parseInt(groupNumber);
            subId = Integer.parseInt(subjectId);
            teachId = Integer.parseInt(teacherId);
            sem = Integer.parseInt(semester);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Մուտքագրման սխալ ֆորմատ");
            return response;
        }
        GroupInfo selectedGroup = groupRepository.findGroupByGroupNumber(groupsNumber);
        Teacher teacher = teacherRepository.findTeacherById(teachId);

        Subjects subjects = subjectsRepository.findSubjectById(subId);
        if (semesterPart.isEmpty() || selectedGroup == null || teacher == null || subjects == null) {
            response.put("success", false);
            response.put("message", "Լրացրեք անհրաժեշտ բոլոր դաշտերը");
            return response;
        }

        Schedule existingSchedule = scheduleRepository.findScheduleBySemesterAndSemesterPartAndWeekdayAndClassHourAndGroup(sem, semesterPart, weekDays, cHour, selectedGroup);
        if (existingSchedule == null) {
            if (semesterPart.equals("all")) {
                Schedule existing1 = scheduleRepository.findScheduleBySemesterAndSemesterPartAndWeekdayAndClassHourAndGroup(sem, "first", weekDays, cHour, selectedGroup);
                Schedule existing2 = scheduleRepository.findScheduleBySemesterAndSemesterPartAndWeekdayAndClassHourAndGroup(sem, "second", weekDays, cHour, selectedGroup);
                if (existing1 != null || existing2 != null) {
                    response.put("success", false);
                    response.put("message", "Դասաժամն արդեն զբաղված է, փորձեք տվյալների խմբագրում");
                    return response;
                }

            } else {
                Schedule existing = scheduleRepository.findScheduleBySemesterAndSemesterPartAndWeekdayAndClassHourAndGroup(sem, "all", weekDays, cHour, selectedGroup);
                if (existing != null) {
                    response.put("success", false);
                    response.put("message", "Դասաժամն արդեն զբաղված է, փորձեք տվյալների խմբագրում");
                    return response;
                }
            }
        } else {
            response.put("success", false);
            response.put("message", "Դասաժամն արդեն զբաղված է, փորձեք տվյալների խմբագրում");
            return response;
        }

        Schedule teacherSchedule = scheduleRepository.findTeacherDayClass(teacher, sem, weekDays, cHour, semesterPart);
        if (teacherSchedule != null) {
            response.put("success", false);
            response.put("message", teacher.getSurname() + " -ը " + classHour + "-րդ ժամին զբաղված է, փորձեք այլ ուսուցչի");
            return response;
        }

        boolean hasSubjects = false;
        List<Subjects> teacherSubjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for (Subjects subjects1 : teacherSubjects) {
            if (subjects1.equals(subjects)) {
                hasSubjects = true;
                break;
            }
        }
        if (!hasSubjects) {
            response.put("success", false);
            response.put("message", "Դասախոսն ու առարկան անհամատեղելի են, փորձեք այլ համակցություն");
            return response;
        }
        Schedule labUsage = scheduleRepository.findLaboratoryUsage(sem, semesterPart, weekDays, cHour, classroom);
        if (labUsage != null) {
            response.put("success", false);
            response.put("message", classroom + " լսարանը " + classHour + "-րդ ժամին զբաղված է, փորձեք այլ լսարան");
            return response;
        }
        Schedule schedule = new Schedule(weekDays, cHour, subjects, selectedGroup, teacher, classroom, sem, semesterPart);
        scheduleRepository.save(schedule);
        response.put("success", true);
        response.put("message", "Դասաժամը հաջողությամբ ավելացվեց!");
        return response;


    }


    @PostMapping("/admin/schedule/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteSchedule(@PathVariable("id") int id) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "Ադմինը մուտք չի գործել");
            return response;
        }
        Schedule schedule = scheduleRepository.findScheduleById(id);
        List<Rating> allRatings = ratingRepository.findAll();
        List<Rating> foundRatings = scheduleService.getRatingsBySchedule(schedule, allRatings);
        if (!foundRatings.isEmpty()) {
            response.put("success", false);
            response.put("message", "Դասաժամն ունի ստեղծված գնահատականներ՝ ջնջումն արգելված է");
            return response;
        }
        scheduleRepository.delete(schedule);
        response.put("success", true);
        response.put("message", "Դասաժամը հաջողությամբ հեռացվեց!");
        return response;


    }

    @PostMapping("/admin/schedule/update")
    @ResponseBody
    public Map<String, Object> updateSchedule(@RequestParam("scheduleId") String scheduleId, @RequestParam("subjectId") String subId,
                                              @RequestParam("teacherId") String teachId,
                                              @RequestParam("classroom") String classroom) {

        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "Ադմինը մուտք չի գործել");
            return response;
        }
        int id = 0, subjectId = 0, TeacherId = 0;
        try {
            id = Integer.parseInt(scheduleId);
            subjectId = Integer.parseInt(subId);
            TeacherId = Integer.parseInt(teachId);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Մուտքային դաշտերի չթույլատրված ֆորմատ");
        }
        Schedule schedule = scheduleRepository.findScheduleById(id);
        Subjects subjects = subjectsRepository.findSubjectById(subjectId);
        Teacher teacher = teacherRepository.findTeacherById(TeacherId);
        if (schedule == null || subjects == null || teacher == null) {
            response.put("success", false);
            response.put("message", "Խնդրում ենք լրացնել բոլոր դաշտերը");
            return response;
        }
        if (!schedule.getTeacher().equals(teacher) || !schedule.getSubject().equals(subjects)) {
            boolean hasSubjects = false;
            List<Subjects> teacherSubjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
            for (Subjects subjects1 : teacherSubjects) {
                if (subjects1.equals(subjects)) {
                    hasSubjects = true;
                    break;
                }
            }
            if (!hasSubjects) {
                response.put("success", false);
                response.put("message", "Դասախոսն ու առարկան անհամատեղելի են, փորձեք այլ համակցություն");
                return response;
            }
        }
        if (!teacher.equals(schedule.getTeacher())) {
            Schedule teacherSchedule = scheduleRepository.findTeacherDayClass(teacher, schedule.getSemester(),
                    schedule.getWeekday(), schedule.getClassHour(), schedule.getSemesterPart());
            if (teacherSchedule != null) {
                response.put("success", false);
                response.put("message", teacher.getSurname() + " -ը " + schedule.getClassHour() + "-րդ ժամին զբաղված է," +
                        " փորձեք այլ ուսուցչի");
                return response;
            }

        }
        if (!classroom.equals(schedule.getClassroom())) {
            Schedule labUsage = scheduleRepository.findLaboratoryUsage(schedule.getSemester(), schedule.getSemesterPart(),
                    schedule.getWeekday(), schedule.getClassHour(), classroom);
            if (labUsage != null) {
                response.put("success", false);
                response.put("message", classroom + " լսարանը " + schedule.getClassHour() + "-րդ ժամին զբաղված է, " +
                        "փորձեք այլ լսարան");
                return response;
            }
        }

        if (!teacher.equals(schedule.getTeacher()) || !(subjects.equals(schedule.getSubject()))) {
            List<Rating> allRatings = ratingRepository.findAll();
            List<Rating> foundRatings = scheduleService.getRatingsBySchedule(schedule, allRatings);
            if (!foundRatings.isEmpty()) {
                response.put("success", false);
                response.put("message", "Դասաժամն ունի ստեղծված գնահատականներ՝ խմբագրումն արգելված է");
                return response;
            }
        }
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        schedule.setSubject(subjects);
        scheduleRepository.save(schedule);
        response.put("success", true);
        response.put("message", "Դասաժամը հաջողությամբ խմբագրվեց!");
        return response;
    }

    @GetMapping("/admin/library")
    public String library(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        List<Library> libraryList = libraryRepository.findLibrariesByIsConfirmed(1);
        List<Subjects> subjectList = subjectsRepository.findAll();
        model.addAttribute("libraryList", libraryList);
        model.addAttribute("subjectList", subjectList);
        model.addAttribute("activePage", "library");
        return "adminLibrary";
    }


    @GetMapping("/admin/videos")
    public String videolessons(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        List<VideoLesson> videoLessons = videoLessonRepository.findAll();
        List<Subjects> subjectList = subjectsRepository.findAll();

        model.addAttribute("videoLessons", videoLessons);
        model.addAttribute("subjectList", subjectList);
        model.addAttribute("activePage", "videos");
        return "adminVideoLesson";
    }


    @GetMapping("/admin/admins")
    public String admins(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }
        List<Admin> adminList = adminRepository.findAll();
        model.addAttribute("adminList", adminList);
        model.addAttribute("activePage", "admins");


        return "allAdminsPage";
    }


    @PostMapping("/admin/admins/toggle/{id}")
    @ResponseBody
    public ResponseEntity<?> toggleAdminStatus(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        Admin currentAdmin = (Admin) session.getAttribute("admin");
        if (currentAdmin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body("Ադմինը չի գտնվել");
        }

        // Արգելել ինքն իրեն բլոկավորել
        if (admin.getId() == currentAdmin.getId()) {
            return ResponseEntity.badRequest().body("Դուք չեք կարող բլոկավորել ինքներդ Ձեզ");
        }

        admin.setIsActive(body.get("isActive"));
        adminRepository.save(admin);
        return ResponseEntity.ok("Status updated successfully");
    }

    @PostMapping("/admin/admins/create")
    @ResponseBody
    public Map<String, Object> createNewAdmin(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                              @RequestParam("email") String username) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty()) {
            response.put("success", false);
            response.put("message", "all fields required");
            return response;
        }
        Admin existing = adminRepository.findAdminByUsername(username);
        if (existing != null) {
            response.put("success", false);
            response.put("message", "account with that email already exists");
            return response;
        }
        try {
            String password = passwordGenerationService.generateRandomPassword();
            emailSenderService.sendPasswordToAdmin(username, password, mailSender);
            String hashedPassword = encoder.encode(password);
            Admin admin1 = new Admin(name, surname, username, hashedPassword, 1);
            adminRepository.save(admin1);
            response.put("success", true);
            response.put("message", "Ադմինիստրատորը հաջողությամբ ստեղծվեց");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "there was a problem, re-check your email");
            return response;
        }

    }

    @PostMapping("/admin/admins/update")
    @ResponseBody
    public Map<String, Object> updateAdmin(@RequestParam("") String id, @RequestParam("name") String name, @RequestParam("surname") String surname
            , @RequestParam("username") String username) {
        Map<String, Object> response = new HashMap<>();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        int adminId;
        try {
            adminId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "invalid digital format");
            return response;
        }
        Admin existingAdmin = adminRepository.findAdminById(adminId);
        if (existingAdmin == null) {
            response.put("success", false);
            response.put("message", "admin not found");
            return response;
        }
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty()) {
            response.put("success", false);
            response.put("message", "all fields required");
            return response;
        }
        if (!username.equals(existingAdmin.getUsername())) {
            Admin admin1 = adminRepository.findAdminByUsername(username);
            if (admin1 != null) {
                response.put("success", false);
                response.put("message", "account with that email already exists");
                return response;
            }
            try {
                emailSenderService.sendAboutEmailChange(name, username, mailSender);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "there was a problem, re-check your email");
                return response;
            }
        }
        existingAdmin.setName(name);
        existingAdmin.setSurname(surname);
        existingAdmin.setUsername(username);
        adminRepository.save(existingAdmin);
        if (existingAdmin.equals(admin)) {
            session.setAttribute("admin", existingAdmin);
        }
        response.put("success", true);
        response.put("message", "account updated successfully");
        return response;


    }

    @GetMapping("/admin/events")
    public String eventsPage(Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin";
        }

        List<TeacherNotification> teacherNotifications = teacherNotificationRepository.findAll();
        List<StudentNotification> studentNotifications = studentNotificationRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        List<GroupInfo> groupInfos = groupRepository.findAll();
        List<Subjects> subjects = subjectsRepository.findAll();
        model.addAttribute("activePage", "events");
        model.addAttribute("studentNotifications", studentNotifications);
        model.addAttribute("teacherNotifications", teacherNotifications);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("groupInfos", groupInfos);
        model.addAttribute("subjects", subjects);
        return "adminEvents";
    }

    @PostMapping("/admin/notify/student")
    @ResponseBody
    public Map<String, Object> notifyStudent(@RequestParam("studentId") String studentId, @RequestParam("messageTitle") String messageTitle,
                                             @RequestParam("messageText") String messageText) {
        Admin admin = (Admin) session.getAttribute("admin");
        Map<String, Object> response = new HashMap<>();
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        int stId;
        try {
            stId = Integer.parseInt(studentId);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "invalid studentId");
            return response;
        }

        Student student = studentRepository.findStudentById(stId);
        if (student == null || messageText.isEmpty() || messageTitle.isEmpty()) {
            response.put("success", false);
            response.put("message", "լրացրեք բոլոր դաշտերը");
            return response;
        }
        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
        StudentNotification studentNotification = new StudentNotification(student, null, nowTimestamp, messageTitle, messageText,"admin");
        studentNotificationRepository.save(studentNotification);
        response.put("success", true);
        response.put("message", "action done!");
        return response;

    }

    @PostMapping("/admin/notify/teacher")
    @ResponseBody
    public Map<String, Object> notifyTeacher(@RequestParam("teacherId") String teacherId, @RequestParam("messageTitle") String messageTitle,
                                             @RequestParam("messageText") String messageText, TimeZone timeZone) {
        Admin admin = (Admin) session.getAttribute("admin");
        Map<String, Object> response = new HashMap<>();
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        int teachId;
        try {
            teachId = Integer.parseInt(teacherId);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "invalid studentId");
            return response;
        }

        Teacher teacher = teacherRepository.findTeacherById(teachId);
        if (teacher == null || messageText.isEmpty() || messageTitle.isEmpty()) {
            response.put("success", false);
            response.put("message", "լրացրեք բոլոր դաշտերը");
            return response;
        }
        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
        TeacherNotification teacherNotification = new TeacherNotification(teacher, null, nowTimestamp, messageTitle, messageText,"admin");
        teacherNotificationRepository.save(teacherNotification);
        response.put("success", true);
        response.put("message", "action done!");
        return response;
    }


    @PostMapping("/admin/notify/student-group")
    @ResponseBody
    public Map<String, Object> notifyStudentGroup(@RequestParam("groupId") String groupId, @RequestParam("messageTitle") String messageTitle,
                                                  @RequestParam("messageText") String messageText) {
        Admin admin = (Admin) session.getAttribute("admin");
        Map<String, Object> response = new HashMap<>();
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        if (messageText.isEmpty() || messageTitle.isEmpty()) {
            response.put("success", false);
            response.put("message", "լրացրեք բոլոր դաշտերը");
            return response;
        }

        int grId = 0;
        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
        if (!groupId.equals("all")) {
            try {
                grId = Integer.parseInt(groupId);
            } catch (NumberFormatException e) {
                response.put("success", false);
                response.put("message", "invalid group id");
                return response;
            }
        } else {
            List<GroupInfo> groups = groupRepository.findAll();
            for (GroupInfo group : groups) {
                StudentNotification studentNotification = new StudentNotification(null, group, nowTimestamp, messageTitle, messageText,"admin");
                studentNotificationRepository.save(studentNotification);
            }
            response.put("success", true);
            response.put("message", "action done!");
            return response;
        }
        GroupInfo groupInfo = groupRepository.findGroupById(grId);
        if (groupInfo == null) {
            response.put("success", false);
            response.put("message", "group not found");
            return response;
        }

        List<Student> students = studentRepository.findStudentsByGroupInfo(groupInfo);
        if (students.isEmpty()) {
            response.put("success", false);
            response.put("message", "empty group cant be notified");
        }


        StudentNotification studentNotification = new StudentNotification(null, groupInfo, nowTimestamp, messageTitle, messageText,"admin");
        studentNotificationRepository.save(studentNotification);
        response.put("success", true);
        response.put("message", "action done!");
        return response;

    }


    @PostMapping("/admin/notify/teacher-group")
    @ResponseBody
    public Map<String, Object> notifyTeacherGroup(@RequestParam("subjectId") String subjectId, @RequestParam("messageTitle") String messageTitle,
                                                  @RequestParam("messageText") String messageText) {
        Admin admin = (Admin) session.getAttribute("admin");
        Map<String, Object> response = new HashMap<>();
        if (admin == null) {
            response.put("success", false);
            response.put("message", "need admin login");
            return response;
        }
        if (messageText.isEmpty() || messageTitle.isEmpty()) {
            response.put("success", false);
            response.put("message", "լրացրեք բոլոր դաշտերը");
            return response;
        }

        int subId = 0;
        Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
        if (!subjectId.equals("all")) {
            try {
                subId = Integer.parseInt(subjectId);
            } catch (NumberFormatException e) {
                response.put("success", false);
                response.put("message", "invalid group id");
                return response;
            }
        } else {
            List<Subjects> subjects = subjectsRepository.findAll();

            for (Subjects subject : subjects) {

                TeacherNotification teacherNotification=new TeacherNotification(null,subject,nowTimestamp,messageTitle,messageText,"admin");
                teacherNotificationRepository.save(teacherNotification);

            }
            response.put("success", true);
            response.put("message", "action done!");
            return response;
        }
        Subjects subjects=subjectsRepository.findSubjectById(subId);
        if (subjects == null) {
            response.put("success", false);
            response.put("message", "subject not found");
            return response;
        }

        List<TeacherSubjectGroup> subjectGroups = teacherSubjectGroupRepository.findTeacherSubjectGroupsBySubject(subjects);
        if (subjectGroups.isEmpty()) {
            response.put("success", false);
            response.put("message", "empty group cant be notified");
        }


        TeacherNotification teacherNotification = new TeacherNotification(null, subjects, nowTimestamp, messageTitle, messageText,"admin");
        teacherNotificationRepository.save(teacherNotification);
        response.put("success", true);
        response.put("message", "action done!");
        return response;

    }


    @GetMapping("/admin/logout")
    public String adminLogout() {
        session.invalidate();
        return "redirect:/admin";

    }




}
