package org.example.educonnectjavaproject.exercises;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.filegroup.FileGroup;
import org.example.educonnectjavaproject.filegroup.FileGroupRepository;
import org.example.educonnectjavaproject.files.FileUploadProcess;
import org.example.educonnectjavaproject.files.UploadedFile;
import org.example.educonnectjavaproject.files.UploadedFileRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.notifications.TeacherNotification;
import org.example.educonnectjavaproject.notifications.TeacherNotificationRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExerciseController {

    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private HttpSession session;
    @Autowired
    private FileGroupRepository fileGroupRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private UploadedFileRepository fileRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HomeWorkRepository homeworkRepository;

    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;
    @Autowired
    private StudentNotificationRepository studentNotificationRepository;

    @PostMapping("exercise")
    public String exercise(@RequestParam("stId") String studentId, @RequestParam("homeworkId") String homeworkId, Model model) {
        int homeWorkId = Integer.parseInt(homeworkId);
        int stId = Integer.parseInt(studentId);
        HomeWork homeWork = homeWorkRepository.findById(homeWorkId);
        session.setAttribute("homeWork", homeWork);
        return "studentExercise";
    }
    @PostMapping("/exercise/submit")
    public String saveExercise(
            Model model, @RequestParam("stId") String stId, @RequestParam("homeworkId") String homeworkId, @RequestParam("answer") String answerText,
            @RequestParam("files") MultipartFile[] files
    ) {
        FileUploadProcess fileUploadProcess = new FileUploadProcess();
        Student student = studentRepository.findStudentById(Integer.parseInt(stId));
        HomeWork homeWork = homeWorkRepository.findHomeWorkById(Integer.parseInt(homeworkId));
        Date date = new java.sql.Date(new java.util.Date().getTime());
        Exercise exercise = new Exercise(date, answerText, student, null, homeWork, 0);
        if (files != null && files.length > 0) {
            FileGroup fileGroup = new FileGroup();
            fileGroupRepository.save(fileGroup);
            exercise.setFileGroup(fileGroup);
            exerciseRepository.save(exercise);
            String messageText=student.getName()+" "+student.getSurname()+"ը "+student.getGroupInfo().getGroupNumber()+ " խմբից " +
                    "կատարել է հանձնարարված" + " տնային աշխտանաքը․ " + "այն կարող եք տեսնել հանձնարարության վիճակագրության էջում։";
            TeacherNotification teacherNotification = new TeacherNotification(homeWork.getTeacher(),null,new Timestamp
                    (System.currentTimeMillis()),"Տնային" + " աշխատանքն արված է՜ \uD83D\uDC40 ",messageText,"system");
            teacherNotificationRepository.save(teacherNotification);

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filePath = fileUploadProcess.saveFileToDisk(file);
                    UploadedFile dbFile = new UploadedFile();
                    dbFile.setFileGroup(fileGroup);
                    dbFile.setFileName(filePath);
                    fileRepository.save(dbFile);
                }
            }
        }
        return "redirect:/student";
    }



    @GetMapping("exercise-done/{id}")
    public String homework(@PathVariable int id) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Student student = (Student) session.getAttribute("student");
        Exercise exercise = exerciseRepository.findExerciseById(id);
        if ((teacher == null && student == null) || exercise == null) {
            return "redirect:/";
        }
        session.setAttribute("exerciseDone", exercise);
        return "exerciseDone";
    }

    @PostMapping("/exercise-done/check/{id}")
    public String checkExercise(@PathVariable int id, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Exercise exercise = exerciseRepository.findExerciseById(id);
        exercise.setIsChecked(1);
        exerciseRepository.save(exercise);
        session.setAttribute("exerciseDone", exercise);
        String messageText=teacher.getName()+" "+teacher.getSurname()+
                "ը Ձեր կատարած տնային աշխատանքը նշել է որպես ստուգվա՜ծ։\uD83C\uDF89";
        StudentNotification studentNotification=new StudentNotification(
                exercise.getStudent(),null,new Timestamp(System.currentTimeMillis()),
                "Առաջադրանքը " +
                "ստուգված է✅",messageText,"system");
        studentNotificationRepository.save(studentNotification);
        redirectAttributes.addFlashAttribute("message",
                "Առաջադրանքը նշվել է որպես ստուգված");
        return "redirect:/exercise-done/{id}";
    }

    @PostMapping("/exercise-done/check-click/{id}")
    public ResponseEntity<Map<String, Object>> checkExercises(@PathVariable int id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Exercise exercise = exerciseRepository.findExerciseById(id);
        if (exercise == null) {
            response.put("success", false);
            response.put("message", "Առաջադրանքը չի գտնվել");
            return ResponseEntity.badRequest().body(response);
        }
        exercise.setIsChecked(1);
        exerciseRepository.save(exercise);
        List<HomeWork> currentHomeWorkList = homeWorkRepository.findActiveHomeworksByTeacher(teacher);
        List<HomeWork> expiredHomeWorkList = homeWorkRepository.findExpiredHomeWorksByTeacher(teacher);
        List<Exercise> uncheckedExercises = exerciseRepository.findExercisesByTeacherAndIsChecked(teacher, 0);
        session.setAttribute("uncheckedExercises", uncheckedExercises);
        session.setAttribute("currentHomeWorkList", currentHomeWorkList);
        session.setAttribute("expiredHomeWorkList", expiredHomeWorkList);
        session.setAttribute("exerciseDone", exercise);
        response.put("success", true);
        response.put("message", "Առաջադրանքը նշվել է որպես ստուգված");
        String messageText=teacher.getName()+" "+teacher.getSurname()+"ը Ձեր կատարած տնային" +
                " աշխատանքը նշել է որպես ստուգվա՜ծ։\uD83C\uDF89";
        StudentNotification studentNotification=new StudentNotification(exercise.getStudent(),null,
                new Timestamp(System.currentTimeMillis()),"Առաջադրանքը ստուգված է✅",messageText,
                "system");
        studentNotificationRepository.save(studentNotification);
        return ResponseEntity.ok(response);
    }

}
