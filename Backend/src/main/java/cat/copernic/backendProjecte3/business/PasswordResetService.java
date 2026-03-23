package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.api.common.ApiException;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.PasswordResetToken;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.repository.PasswordResetTokenRepository;
import cat.copernic.backendProjecte3.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servei per gestionar la recuperació de contrasenya. Genera un token temporal
 * i envia el token per correu a l'adreça de l'usuari.
 */
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UsuariRepository usuariRepo;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * Crea el servei de recuperació de contrasenya.
     *
     * @param tokenRepo repositori de tokens temporals
     * @param usuariRepo repositori d'usuaris
     * @param mailSender component encarregat d'enviar correus
     */
    public PasswordResetService(
            PasswordResetTokenRepository tokenRepo,
            UsuariRepository usuariRepo,
            JavaMailSender mailSender
    ) {
        this.tokenRepo = tokenRepo;
        this.usuariRepo = usuariRepo;
        this.mailSender = mailSender;
    }

    /**
     * Genera un token temporal per reiniciar la contrasenya i l'envia per
     * correu.
     *
     * @param email Email de l'usuari (el que s'ha demanat recuperar).
     * @return Token temporal (NO s'ha de retornar a l'API).
     */
    public String createResetToken(String email) {
        usuariRepo.findByEmail(email).orElseThrow(() -> new ApiException(404, "Usuari no trobat"));

        String token = UUID.randomUUID().toString().replace("-", "");
        tokenRepo.save(new PasswordResetToken(token, email, LocalDateTime.now().plusMinutes(30)));

        enviarTokenPerCorreu(email, token);

        return token;
    }

    /**
     * Envia per correu electrònic el token de recuperació generat.
     *
     * @param emailDemanat adreça de correu destinatària
     * @param token token temporal de recuperació
     */
    private void enviarTokenPerCorreu(String emailDemanat, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromEmail != null && !fromEmail.isBlank()) {
            message.setFrom(fromEmail);
        }

        message.setTo(emailDemanat);

        message.setSubject("Recuperació de contrasenya");
        message.setText(
                """
                Has demanat recuperar la contrasenya del teu compte.
                
                TOKEN: """ + token + "\n\n"
                + "Aquest token caduca en 30 minuts."
        );

        mailSender.send(message);
    }

    /**
     * Restableix la contrasenya utilitzant un token temporal vàlid.
     *
     * @param token token de recuperació
     * @param newPassword nova contrasenya en text pla
     */
    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new ApiException(400, "Contrasenya obligatòria");
        }

        PasswordResetToken t = tokenRepo.findById(token)
                .orElseThrow(() -> new ApiException(400, "Token invàlid"));

        if (t.isUsed()) {
            throw new ApiException(400, "Token ja utilitzat");
        }
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(400, "Token caducat");
        }

        Usuari u = usuariRepo.findByEmail(t.getEmail())
                .orElseThrow(() -> new ApiException(404, "Usuari no trobat"));

        u.setPassword(PasswordHasher.encode(newPassword));
        usuariRepo.save(u);

        t.setUsed(true);
        tokenRepo.save(t);
    }
}
