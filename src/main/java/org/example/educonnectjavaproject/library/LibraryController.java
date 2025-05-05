package org.example.educonnectjavaproject.library;

import jakarta.servlet.http.HttpSession;
import jdk.jfr.Registered;
import org.example.educonnectjavaproject.files.FileUploadProcess;
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
public class LibraryController {

    @Autowired
    HttpSession session;
    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    SubjectsRepository subjectsRepository;

    @Autowired
    private StudentNotificationReadingRepository studentNotificationReadingRepository;
    FileUploadProcess fileUploadProcess = new FileUploadProcess();
    @Autowired
    private StudentNotificationRepository studentNotificationRepository;
    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;
    @Autowired
    private SubjectGroupRepository subjectGroupRepository;
    @Autowired
    private TeacherSubjectGroupRepository teacherSubjectGroupRepository;

    @Autowired
    private TeacherNotificationReadingRepository teacherNotificationReadingRepository;
    @GetMapping("/library")
    public String library(Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/";
        }
        List<StudentNotification> studentNotifications = studentNotificationRepository.findStudentNotificationsByStudentOrGroupAndIsRead(student, student.getGroupInfo(), studentNotificationReadingRepository);
        model.addAttribute("studentNotification", studentNotifications);
        List<Library> libraryList = libraryRepository.findAll();
        List<Subjects> subjectList = subjectsRepository.findAll();
        session.setAttribute("libraryList", libraryList);
        session.setAttribute("subjectList", subjectList);
        return "library";
    }

    @GetMapping("/teacher/library")
    public String teacherLibrary(Model model) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }
        List<Library> libraryList = libraryRepository.findLibrariesByIsConfirmed(1);
        List<Subjects> subjectList = subjectsRepository.findAll();
        session.setAttribute("libraryList", libraryList);
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
        return "teacherLibrary";
    }

    @PostMapping("/addBook")
    public String addBook(@RequestParam("title") String title, @RequestParam("subject") int subjectId,
                          @RequestParam("grade") int grade, @RequestParam("bookAuthor") String author,
                          @RequestParam("bookPublicationYear") int bookPublicationYear, @RequestParam("thumbnailImage") MultipartFile imageFile,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam("id") String libraryId) {
        FileUploadProcess fileUploadProcess = new FileUploadProcess();
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        Admin admin = (Admin) session.getAttribute("admin");
        if (teacher == null && admin == null) {

            return "redirect:/";
        }
        Subjects subjects = subjectsRepository.findSubjectById(subjectId);
        if (subjects == null) {
            System.out.println("subjects null");
            return "redirect:/";
        }
        if (libraryId != null && !libraryId.isEmpty()) {
            int libraryID = Integer.parseInt(libraryId);
            Library existingLibrary = libraryRepository.findLibraryById(libraryID);
            if (existingLibrary != null) {
                if (file != null && !file.isEmpty()) {
                    fileUploadProcess.deleteFileFromDisk(existingLibrary.getFileName());
                    String newName = fileUploadProcess.saveFileToDisk(file);
                    existingLibrary.setFileName(newName);
                    System.out.println("file name " + newName);
                }
                if (imageFile != null && !imageFile.isEmpty()) {
                    fileUploadProcess.deleteFileFromDisk(existingLibrary.getThumbnailImage());
                    String newName = fileUploadProcess.saveFileToDisk(imageFile);
                    existingLibrary.setThumbnailImage(newName);
                    System.out.println("image name " + newName);
                }
                existingLibrary.setTitle(title);
                existingLibrary.setSubject(subjects);
                existingLibrary.setGrade(grade);
                existingLibrary.setBookAuthor(author);
                existingLibrary.setBookPublicationYear(bookPublicationYear);
                if (admin != null) {
                    existingLibrary.setAdmin(admin);
                }
                libraryRepository.save(existingLibrary);
                return "redirect:/teacher/library";
            }
        }

        String fileName = fileUploadProcess.saveFileToDisk(file);
        String thumbnailImage = fileUploadProcess.saveFileToDisk(imageFile);
        Library library;
        if (teacher != null) {
            library = new Library(title, subjects, fileName, grade, bookPublicationYear, author, thumbnailImage, teacher, null, 1);
        } else {
            library = new Library(title, subjects, fileName, grade, bookPublicationYear, author, thumbnailImage, null, admin, 1);
        }
        libraryRepository.save(library);

        if (admin != null) {
            return "redirect:/admin/library";
        }
        return "redirect:/teacher/library";
    }

    @PostMapping("/deleteBook/{libraryId}")
    public String deleteLibrary(@PathVariable int libraryId) {
        Teacher teacher=(Teacher)session.getAttribute("teacher");
        Admin admin=(Admin)session.getAttribute("admin");
        if(admin==null&&teacher==null){
            return "redirect:/";
        }

        Library library = libraryRepository.findLibraryById(libraryId);
        if (library == null) {

            return "redirect:/";
        }
        fileUploadProcess.deleteFileFromDisk(library.getThumbnailImage());
        fileUploadProcess.deleteFileFromDisk(library.getFileName());
        libraryRepository.delete(library);
        if(admin!=null){
            return "redirect:/admin/library";
        }
        return "redirect:/teacher/library";

    }

}