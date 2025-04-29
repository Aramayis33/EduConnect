package org.example.educonnectjavaproject.security;

import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.notifications.StudentNotification;
import org.example.educonnectjavaproject.notifications.StudentNotificationRepository;
import org.example.educonnectjavaproject.notifications.TeacherNotification;
import org.example.educonnectjavaproject.notifications.TeacherNotificationRepository;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.example.educonnectjavaproject.teacher.TeacherRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.Random;

@Controller
public class AccountActivationController {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private EmailSenderService emailSenderService=new EmailSenderService();
    @Autowired
    private StudentNotificationRepository studentNotificationRepository;
    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;

    @GetMapping("/activate")
    public String activate(){
        return "accountActivation";
    }

    @PostMapping("/email-confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("email") String email, HttpSession session) {
        if (mailSender == null) {
            System.out.println("mailSender is null!");
            return ResponseEntity.status(500).body("{\"error\": \"Mail sender not initialized\"}");
        }
        System.out.println("email: " + email);
        Teacher teacher = teacherRepository.findTeacherByEmailAndIsActivated(email,0);
        System.out.println("inqna");
        System.out.println(teacher);
        Student student = studentRepository.findStudentByEmailAndIsActivated(email,0);
        if (teacher == null && student == null) {
            return ResponseEntity.status(500).body("{\"error\": \"Օգտատերը գտնված չէ\"}");
        }
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("email", email);

        try {
            emailSenderService.sendVerificationEmail(email, verificationCode,mailSender);
            System.out.println("Email sent successfully to: " + email);
            return ResponseEntity.ok().body("{\"message\": \"Հաստատման կոդը ուղարկվել է Ձեր էլ. փոստին\"}");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return ResponseEntity.status(500).body("{\"error\": \"Էլ. փոստ ուղարկելը ձախողվեց: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("new-password-activate")
    public ResponseEntity<?> newPassword(@RequestParam("password") String password, HttpSession session) {

        String email = session.getAttribute("email").toString();
        Student student = studentRepository.findStudentByEmail(email);
        Teacher teacher = teacherRepository.findTeacherByEmail(email);
        String hashedPassword = encoder.encode(password);
        if (student != null) {
            if (encoder.matches(password, student.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"Նոր Գաղտնաբառը պետք է տարբերվի նախորիդ \"}");
            }
            student.setPassword(hashedPassword);
            student.setIsActivated(1);
            studentRepository.save(student);
            String messageText = "Հարգելի " + student.getName() + ", ուրախ ենք տեսնել քեզ EduConnect հարթակում, մաղթում ենք հաջողություններ հետագա կրթական պրոցեսում։\uD83C\uDF93";
            StudentNotification studentNotification = new StudentNotification(student, null, new Timestamp(System.currentTimeMillis()), "Բարի Գալուստ EduConnect!\uD83E\uDD17", messageText, "system");
            studentNotificationRepository.save(studentNotification);
            return ResponseEntity.ok("Գաղտնաբառը հաջողությամբ փոխվեց");
        }
        else if(teacher != null) {
            if(encoder.matches(password,teacher.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"Նոր Գաղտնաբառը պետք է տարբերվի նախորդից \"}");
            }
            teacher.setPassword(hashedPassword);
            teacher.setIsActivated(1);
            teacherRepository.save(teacher);
            String messageText = "Հարգելի " + teacher.getName() + ", ուրախ ենք տեսնել քեզ EduConnect հարթակում, մաղթում ենք հաջողություններ հետագա կրթական պրոցեսում։\uD83C\uDF93";
            TeacherNotification teacherNotification = new TeacherNotification(teacher, null, new Timestamp(System.currentTimeMillis()), "Բարի Գալուստ EduConnect!\uD83E\uDD17", messageText, "system");
            teacherNotificationRepository.save(teacherNotification);
            return ResponseEntity.ok("Գաղտնաբառը հաջողությամբ փոխվեց");

        }
        return ResponseEntity.status(400).body("{\"error\": \"Առկա է սխալմունք \"}");

    }
}

