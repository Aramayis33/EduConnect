package org.example.educonnectjavaproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailSenderService {



    public void sendVerificationEmail(String toEmail, String code,JavaMailSender mailSender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EduConnect - Հաստատման կոդ");
        message.setText("Ձեր հաստատման կոդն է: " + code + "\nԽնդրում ենք մուտքագրել այն կայքում՝ էլ. փոստը հաստատելու համար:");
        mailSender.send(message);
    }
    public void sendPasswordToAdmin(String toEmail,String password, JavaMailSender mailSender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EduConnect - գաղտնաբառ");
        message.setText("Ձեր ադմին էջի գաղտնաբառն է: " + password + "\n Օգտագործեք այն ադմինիստրացիոն էջ մուտք գործելու համար, իսկ հետո ըստ ցանկության ստեղծեք նոր գաղտնաբառ:");
        mailSender.send(message);
    }

    public void sendAboutEmailChange(String name,String toEmail,JavaMailSender mailSender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EduConnect - էլ․ հասցեի փոփոխում");
        message.setText("Հարգելի "+ name+" այս էլեկտրոնային հասցեն կցվել է EduConnect -ի ձեր օգտահաշվին, իսկ մնացած տվյալների հետ՝ ներառյալ գաղտնաբառը, ոչ մի փոփոխություն տեղի չի ունեցել " + "\n Օգտագործեք այն ադմինիստրացիոն էջ մուտք գործելու համար, իսկ հետո ըստ ցանկության ստեղծեք նոր գաղտնաբառ:");
   mailSender.send(message);
    }
}
