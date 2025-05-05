package org.example.educonnectjavaproject.homework;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.exercises.ExerciseRepository;
import org.example.educonnectjavaproject.filegroup.FileGroup;
import org.example.educonnectjavaproject.filegroup.FileGroupRepository;
import org.example.educonnectjavaproject.files.FileUploadProcess;
import org.example.educonnectjavaproject.files.UploadedFile;
import org.example.educonnectjavaproject.files.UploadedFileRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.SubjectsRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeWorkController {


    @Autowired
    private HttpSession session;
    @Autowired
    private HomeWorkRepository homeWorkRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;

    @Autowired
    private FileGroupRepository fileGroupRepository;

    @Autowired
    private UploadedFileRepository fileRepository;
    @Autowired
    private StudentNotificationRepository studentNotificationRepository;
//    @GetMapping("/teacher/new/homework")
//    public String homework(Model model) {
//        model.addAttribute("subject", session.getAttribute("subject"));
//
//        return "addHomeWork";
//    }

//    @PostMapping("addHomework")
//    public String homeWorkPage(@RequestParam("groupNumber") String groupNumber, @RequestParam("title") String title, Model model) {
//
//        int group = Integer.parseInt(groupNumber);
//        String subjectName = (String) session.getAttribute("subject");
//        LocalDate inputDate = LocalDate.now();
//        HomeWork homeWork = new HomeWork(group, title, java.sql.Date.valueOf(inputDate), subjectName);
//        homeWorkRepository.save(homeWork);
//
//        return "redirect:/teacher";
//
//    }

    @GetMapping("/teacher/new-assignment")
    public String newAssignment() {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }
        return "addHomeWork";
    }

    @PostMapping("/teacher/new-assignment/save")
    public String newAssignment(@RequestParam("title") String title, @RequestParam("subjectId") int subjectId,
                                @RequestParam("groupNumber") int groupNumber, @RequestParam("inputDate") Date inputDate,
                                @RequestParam("expirationDate") Date expirationDate, @RequestParam("requiresResponse") int requiresResponse,
                                @RequestParam("files") MultipartFile[] files, @RequestParam("description") String description) {

        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }
        if (title.equals("") || description.equals("")) {
            return "redirect:/teacher/new-assignment";
        }
        FileUploadProcess fileUploadProcess=new FileUploadProcess();
        Subjects subjects=subjectsRepository.findSubjectById(subjectId);
        HomeWork homeWork = new HomeWork(groupNumber,title,inputDate,expirationDate,subjects,requiresResponse,teacher,null,description);
        if (files != null && files.length > 0) {
            FileGroup fileGroup = new FileGroup();
            fileGroupRepository.save(fileGroup);
            homeWork.setFileGroup(fileGroup);
            homeWorkRepository.save(homeWork);
            GroupInfo groupInfo=groupRepository.findGroupByGroupNumber(groupNumber);
            String messageText="Դուք ունեք նոր տնային առաջադրանք ՛"+subjects.getName()+"՛ առարկայից․ կատարեք այն մինչև՝ "+expirationDate;
            StudentNotification studentNotification=new StudentNotification(null,groupInfo,new Timestamp(System.currentTimeMillis()),"\uD83D\uDED1Նոր առաջադրան՜ք❗",messageText,"system");
studentNotificationRepository.save(studentNotification);
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filePath = fileUploadProcess.saveFileToDisk(file);
                    UploadedFile dbFile = new UploadedFile();
                    dbFile.setFileGroup(fileGroup);
                    dbFile.setFileName(filePath);
                    fileRepository.save(dbFile);
                }
            };
        }
        List<HomeWork> currentHomeWorkList = homeWorkRepository.findActiveHomeworksByTeacher(teacher);
        session.setAttribute("currentHomeWorkList", currentHomeWorkList != null ? currentHomeWorkList : new ArrayList<>());
        List<HomeWork> expiredHomeWorkList = homeWorkRepository.findExpiredHomeWorksByTeacher(teacher);
        session.setAttribute("expiredHomeWorkList", expiredHomeWorkList != null ? expiredHomeWorkList : new ArrayList<>());
        List<Exercise> uncheckedExercises = exerciseRepository.findExercisesByTeacherAndIsChecked(teacher, 0);
        session.setAttribute("uncheckedExercises", uncheckedExercises != null ? uncheckedExercises : new ArrayList<>());
        return "redirect:/teacher";

    }

    @GetMapping("task/{id}")
    public String homework(@PathVariable int id) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }
        HomeWork homework = homeWorkRepository.findHomeWorkById(id);
        if (homework == null) {
            return "redirect:/teacher";
        }
        List<Exercise> exercises = exerciseRepository.findExercisesByHomeWork(homework);
        session.setAttribute("detailHomework", homework);
        session.setAttribute("exercisesForHomeWork", exercises);

        GroupInfo groupInfo = groupRepository.findGroupByGroupNumber(homework.getGroupNumber());
        List<Student> students = studentRepository.findStudentsByGroupInfo(groupInfo);
        session.setAttribute("students", students);
        return "homeworkView";
    }
}