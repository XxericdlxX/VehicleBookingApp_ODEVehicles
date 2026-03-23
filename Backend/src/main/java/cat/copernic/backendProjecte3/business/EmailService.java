package cat.copernic.backendProjecte3.business;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}