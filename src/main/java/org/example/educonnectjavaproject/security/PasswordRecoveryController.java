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
public class PasswordRecoveryController {

    @Autowired
    private JavaMailSender mailSender;

    private EmailSenderService emailSenderService=new EmailSenderService();

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentNotificationRepository studentNotificationRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/confirm-emaill")
    public ResponseEntity<?> confirmEmail(@RequestParam("email") String email, HttpSession session) {
        if (mailSender == null) {
            System.out.println("mailSender is null!");
            return ResponseEntity.status(500).body("{\"error\": \"Mail sender not initialized\"}");
        }

        Teacher teacher = teacherRepository.findTeacherByEmail(email);
        Student student = studentRepository.findStudentByEmail(email);
        Admin admin=userService.findUserByUsername(email);
        if (teacher == null && student == null&& admin == null) {
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

    @PostMapping("check-code")
    public ResponseEntity<?> checkCode(@RequestParam("code") String code, HttpSession session) {
        if (mailSender == null) {
            System.out.println("mailSender is null!");
            return ResponseEntity.status(500).body("{\"error\": \"Էլ․ հասցեի սպասարկման սերվիսը հասանելի չէ\"}");
        }
        int inputCode = Integer.parseInt(code);
        int realCode = Integer.parseInt(session.getAttribute("verificationCode").toString());
        if (inputCode == realCode) {
            return ResponseEntity.ok("Էլ․ հասցեն հաստաված է, կարող եք փոխել գաղտնաբառը");
        }
        return ResponseEntity.status(400).body("{\"error\": \"Մուտքագրված կոդը սխալ է \"}");


    }

    @PostMapping("new-password")
    public ResponseEntity<?> newPassword(@RequestParam("password") String password, HttpSession session) {

        String email = session.getAttribute("email").toString();
        Student student = studentRepository.findStudentByEmail(email);
        Teacher teacher = teacherRepository.findTeacherByEmail(email);
        Admin admin = userService.findUserByUsername(email);
        String hashedPassword = encoder.encode(password);
        if (student != null) {
            if(encoder.matches(password,student.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"Նոր Գաղտնաբառը պետք է տարբերվի նախորդից \"}");
            }
            student.setPassword(hashedPassword);
            studentRepository.save(student);
            String messageText = "Հարգելի " + student.getName() + ", քիչ առաջ Ձեր գաղտնաբառը փոփոխվել է. եթե դուք չեք կատարել փոփոխությունը, ապա խորհուրդ ենք տալիս փոխել այն ևս մեկ անգամ՝ անվտանգության նկատառումներից ելնելով։";
            StudentNotification studentNotification = new StudentNotification(student, null, new Timestamp(System.currentTimeMillis()), "Գաղտնաբառի փոփոխություն\uD83D\uDD12", messageText, "system");
            studentNotificationRepository.save(studentNotification);
            return ResponseEntity.ok("Գաղտնաբառը հաջողությամբ փոխվեց");
        }
        else if(teacher != null) {
            if(encoder.matches(password,teacher.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"Նոր Գաղտնաբառը պետք է տարբերվի նախորդից \"}");
            }
            teacher.setPassword(hashedPassword);
            teacherRepository.save(teacher);
            String messageText = "Հարգելի " + teacher.getName() + ", քիչ առաջ Ձեր գաղտնաբառը փոփոխվել է. եթե դուք չեք կատարել փոփոխությունը, ապա խորհուրդ ենք տալիս փոխել այն ևս մեկ անգամ՝ անվտանգության նկատառումներից ելնելով։";
            TeacherNotification teacherNotification = new TeacherNotification(teacher, null, new Timestamp(System.currentTimeMillis()), "Գաղտնաբառի փոփոխություն\uD83D\uDD12", messageText, "system");
            teacherNotificationRepository.save(teacherNotification);
            return ResponseEntity.ok("Գաղտնաբառը հաջողությամբ փոխվեց");
        }
        else if(admin != null) {
            if(encoder.matches(password,admin.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"Նոր Գաղտնաբառը պետք է տարբերվի նախորդից \"}");
            }
            admin.setPassword(hashedPassword);
            userService.save(admin);
            return ResponseEntity.ok("Գաղտնաբառը հաջողությամբ փոխվեց");
        }
        return ResponseEntity.status(400).body("{\"error\": \"Առկա է սխալմունք \"}");

    }
}
