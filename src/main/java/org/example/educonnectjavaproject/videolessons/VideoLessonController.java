package org.example.educonnectjavaproject.videolessons;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.files.FileUploadProcess;
import org.example.educonnectjavaproject.library.Library;
import org.example.educonnectjavaproject.notifications.*;
import org.example.educonnectjavaproject.security.Admin;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.SubjectGroupRepository;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.SubjectsRepository;
import org.example.educonnectjavaproject.subject.TeacherSubjectGroupRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
public class VideoLessonController {

    @Autowired
    private HttpSession session;

    @Autowired
    private VideoLessonRepository videoLessonRepository;

    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private StudentNotificationRepository  studentNotificationRepository;

    @Autowired
    private StudentNotificationReadingRepository studentNotificationReadingRepository;

    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;
    @Autowired
    private SubjectGroupRepository subjectGroupRepository;
    @Autowired
    private TeacherSubjectGroupRepository teacherSubjectGroupRepository;

    @Autowired
    private TeacherNotificationReadingRepository teacherNotificationReadingRepository;

    private FileUploadProcess fileUploadProcess=new FileUploadProcess();
    @GetMapping("/videoLessons")
    public String videoLessons(Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/";
        }
        List<VideoLesson>videoLessons = videoLessonRepository.findAll();
        List<Subjects>subjectList=subjectsRepository.findAll();
        List<StudentNotification>studentNotifications=studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student,student.getGroupInfo(),studentNotificationReadingRepository);
        model.addAttribute("studentNotification", studentNotifications);

        session.setAttribute("videoLessons", videoLessons);
        session.setAttribute("subjectList", subjectList);
        return "videolessons";
    }

    @GetMapping("/teacher/videoLessons")
    public String teacherVideoLessons(Model model) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(teacher == null) {
            return "redirect:/";
        }
        List<VideoLesson>videoLessons = videoLessonRepository.findAll();
        List<Subjects>subjectList=subjectsRepository.findAll();

        session.setAttribute("videoLessons", videoLessons);
        session.setAttribute("subjectList", subjectList);

        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);

        model.addAttribute("teacherNotifications", notRead);
        return "teacherVideoLesson";
    }

    @PostMapping("/addVideo")
    public String addBook(@RequestParam("title") String title, @RequestParam("subject") int subjectId,
                          @RequestParam("grade") int grade,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam("id") String videoId,
                          @RequestParam("description")String description) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Admin admin = (Admin) session.getAttribute("admin");
        if (teacher == null && admin == null) {
            System.out.println("mard chka");
            System.out.println("this is teacher " + teacher);
            System.out.println("this is admin " + admin);
            return "redirect:/";
        }
        Subjects subjects = subjectsRepository.findSubjectById(subjectId);
        if (subjects == null) {
            System.out.println("subjects null");
            return "redirect:/";
        }
        if (videoId != null && !videoId.isEmpty()) {
            int videoID = Integer.parseInt(videoId);
            VideoLesson existingVideoLesson = videoLessonRepository.findVideoLessonById(videoID);
            if (existingVideoLesson != null) {
                if (file != null && !file.isEmpty()) {
                    fileUploadProcess.deleteFileFromDisk(existingVideoLesson.getVideoUrl());
                    String newName = fileUploadProcess.saveFileToDisk(file);
                    existingVideoLesson.setVideoUrl(newName);
                    System.out.println("file name " + newName);
                }
                existingVideoLesson.setTitle(title);
                existingVideoLesson.setSubject(subjects);
                existingVideoLesson.setGrade(grade);
                existingVideoLesson.setDescription(description);
                if (admin != null) {
                    existingVideoLesson.setAdmin(admin);
                }
                videoLessonRepository.save(existingVideoLesson);
                return "redirect:/teacher/videoLessons";
            }
        }
        String fileName = fileUploadProcess.saveFileToDisk(file);
        VideoLesson lesson;
        if (teacher != null) {
            lesson=new VideoLesson(subjects,grade,title,fileName,description,teacher,null,1);
        } else {
            lesson=new VideoLesson(subjects,grade,title,fileName,description,null,admin,1);
        }

        videoLessonRepository.save(lesson);
        if(admin!=null){
            return "redirect:/admin/videos";
        }
        return "redirect:/teacher/videoLessons";
    }

    @PostMapping("/deleteVideo/{videoId}")
    public String deleteVideo(@PathVariable int videoId) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Admin admin = (Admin) session.getAttribute("admin");
        if(admin==null&&teacher==null){
            return "redirect:/";
        }
        VideoLesson lesson = videoLessonRepository.findVideoLessonById(videoId);
        if (lesson == null) {
            return "redirect:/teacher/videoLessons";
        }
        fileUploadProcess.deleteFileFromDisk(lesson.getVideoUrl());
        videoLessonRepository.delete(lesson);
        if(admin!=null){
            return "redirect:/admin/videos";
        }
        return "redirect:/teacher/videoLessons";

    }

}