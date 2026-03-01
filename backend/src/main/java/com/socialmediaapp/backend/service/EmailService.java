package com.socialmediaapp.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@socialmediaapp.com");
        message.setTo(toEmail);
        message.setSubject("Recuperar contraseña");
        message.setText(
            "Hola,\n\n" +
            "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
            "Haz clic en el siguiente enlace para crear una nueva contraseña:\n" +
            resetLink + "\n\n" +
            "Este enlace expira en 1 hora.\n\n" +
            "Si no solicitaste esto, ignora este correo.\n\n" +
            "Saludos,\nEl equipo de SocialMediaApp"
        );

        mailSender.send(message);
    }
}
