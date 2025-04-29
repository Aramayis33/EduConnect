package org.example.educonnectjavaproject.emailconfirmation;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.files.FileUploadProcess;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class SettingsController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpSession session;
    @Autowired
    private TeacherRepository teacherRepository;

    FileUploadProcess process = new FileUploadProcess();

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam("email") String email, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Student student = (Student) session.getAttribute("student");
        Teacher teacher = (Teacher) session.getAttribute("teacher");

        if (mailSender == null) {
            System.out.println("mailSender is null!");
            return ResponseEntity.status(500).body("{\"error\": \"Mail sender not initialized\"}");
        }


        if (student != null && student.getEmail().equals(email) || (teacher != null && teacher.getEmail().equals(email))) {
            response.put("error", "Մուտքագրված էլ․ հասցեն չի կարող համընկնել ներկայիս էլ․ հասցեի հետ");
            return ResponseEntity.status(400).body(response);
        }

        Student student1 = studentRepository.findStudentByEmail(email);
        Teacher teacher1 = teacherRepository.findTeacherByEmail(email);
        if (student1 != null || teacher1 != null) {
            response.put("error", "Այս էլ հասցեն արդեն օգտագործված է");
            return ResponseEntity.status(400).body(response);
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("newEmail", email);

        try {
            sendVerificationEmail(email, verificationCode);
            System.out.println("Email sent successfully to: " + email);
            return ResponseEntity.ok().body("{\"message\": \"Հաստատման կոդը ուղարկվել է Ձեր էլ. փոստին\"}");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return ResponseEntity.status(500).body("{\"error\": \"Էլ. փոստ ուղարկելը ձախողվեց: " + e.getMessage() + "\"}");
        }
    }

    private void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EduConnect - Հաստատման կոդ");
        message.setText("Ձեր հաստատման կոդն է: " + code + "\nԽնդրում ենք մուտքագրել այն կայքում՝ էլ. փոստը հաստատելու համար:");
        mailSender.send(message);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("code") String code, HttpSession session) {
        String storedCode = (String) session.getAttribute("verificationCode");
        String newEmail = (String) session.getAttribute("newEmail");

        if (storedCode != null && storedCode.equals(code)) {
            Student student = (Student) session.getAttribute("student");
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            session.removeAttribute("verificationCode");
            session.removeAttribute("newEmail");
            if (student != null) {
                student.setEmail(newEmail);
                session.setAttribute("student", student);
                studentRepository.save(student);

            } else if (teacher != null) {
                teacher.setEmail(newEmail);
                session.setAttribute("teacher", teacher);
                teacherRepository.save(teacher);
            }
            return ResponseEntity.ok().body("{\"message\": \"Էլ. փոստը հաջողությամբ փոխվեց\"}");

        } else {
            return ResponseEntity.status(400).body("{\"error\": \"Հաստատման կոդը սխալ է\"}");
        }
    }

    @GetMapping("/current-email")
    public ResponseEntity<?> getCurrentEmail() {
        Student student = (Student) session.getAttribute("student");
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (student == null && teacher == null) {
            return ResponseEntity.status(401).body("{\"error\": \"Օգտատերը մուտք չի գործել\"}");
        }
        if (student != null) {
            return ResponseEntity.ok().body("{\"email\": \"" + student.getEmail() + "\"}");
        } else {
            return ResponseEntity.ok().body("{\"email\": \"" + teacher.getEmail() + "\"}");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Student student = (Student) session.getAttribute("student");
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            System.out.println("Old Password: " + oldPassword);
            System.out.println("New Password: " + newPassword);
            System.out.println("Student: " + student);

            if (student == null && teacher == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Օգտատերը մուտք չի գործել");
                System.out.println("No student or teacher in session");
                return ResponseEntity.status(401).body(response);
            }

            if (student != null) {

                if (encoder.matches(oldPassword, student.getPassword())) {
                    student.setPassword(encoder.encode(newPassword));
                    studentRepository.save(student);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Գաղտնաբառը հաջողությամբ փոխվեց");
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Ներկա գաղտնաբառը սխալ է");
                    return ResponseEntity.status(400).body(response);
                }
            }
            if (encoder.matches(oldPassword, teacher.getPassword())) {
                teacher.setPassword(encoder.encode(newPassword));
                teacherRepository.save(teacher);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Գաղտնաբառը հաջողությամբ փոխվեց");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Ներկա գաղտնաբառը սխալ է");
                return ResponseEntity.status(400).body(response);
            }

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ինչ-որ սխալ տեղի ունեցավ: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("profileImage") MultipartFile file) {
        try {
            Student student = (Student) session.getAttribute("student");
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            String uploadDir = "src/main/resources/static/images/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            if (student != null) {
                if (!student.getImage().equals("default.jpg")) {
                    process.deleteProfileImage(student.getImage());
                }
                student.setImage(fileName);
                studentRepository.save(student);
                session.setAttribute("student", student);
            } else {
                if (!teacher.getImage().equals("default.jpg")) {
                    process.deleteProfileImage(teacher.getImage());
                }
                teacher.setImage(fileName);
                teacherRepository.save(teacher);
                session.setAttribute("teacher", teacher);
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "Օգտահաշվի նկարը հաջողությամբ փոխվեց");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Նկարի փոփոխումը ձախողվեց");
            return ResponseEntity.status(400).body(response);
        }
    }


    @PostMapping("delete-image")
    public ResponseEntity<?> deleteImage() {
        try {
            Student student = (Student) session.getAttribute("student");
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            if (student != null) {
                process.deleteProfileImage(student.getImage());
                student.setImage("default.jpg");
                studentRepository.save(student);
                session.setAttribute("student", student);
            } else {
                process.deleteProfileImage(teacher.getImage());
                teacher.setImage("default.jpg");
                teacherRepository.save(teacher);
                session.setAttribute("teacher", teacher);
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "Օգտահաշվի նկարը հեռացվեց");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Նկարի ջնջումը ձախողվեց");
            return ResponseEntity.status(400).body(response);
        }
    }


}