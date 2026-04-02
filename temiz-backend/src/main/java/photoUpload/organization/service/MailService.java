package photoUpload.organization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dehriirem@gmail.com"); // application.properties ile aynı olmalı
        message.setTo(to);
        message.setSubject("Şifre Sıfırlama Kodu");
        message.setText("Merhaba,\n\nŞifrenizi sıfırlamak için onay kodunuz: " + code +
                "\n\nBu kod 5 dakika süreyle geçerlidir.");

        mailSender.send(message);
    }
}