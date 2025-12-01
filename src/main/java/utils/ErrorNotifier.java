package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ErrorNotifier {

    static String USER;
    static String APP_PASSWORD;
    static String TO;

    // ====== НОВЕ: дозволяє тестам без рефлексії переписувати поля ======
    public static void setCredentialsForTests(String user, String pass, String to) {
        USER = user;
        APP_PASSWORD = pass;
        TO = to;
    }
    // ====================================================================

    static {
        Properties props = new Properties();
        String user = "";
        String password = "";
        String to = "";

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            user = props.getProperty("email.user", "");
            password = props.getProperty("email.password", "");
            to = props.getProperty("email.to", "");
        } catch (IOException e) {
            System.err.println("Не вдалося прочитати config.properties, спробуємо змінні середовища");
            user = System.getenv("EMAIL_USER");
            password = System.getenv("EMAIL_PASSWORD");
            to = System.getenv("EMAIL_TO");
        }

        USER = user;
        APP_PASSWORD = password;
        TO = to;
    }

    public static void sendErrorEmail(Exception e) {
        if (USER == null || USER.isEmpty() ||
                APP_PASSWORD == null || APP_PASSWORD.isEmpty() ||
                TO == null || TO.isEmpty()) {
            System.out.println("Email credentials missing — skipping sendErrorEmail");
            return;
        }

        try {
            Properties mailProps = new Properties();
            mailProps.put("mail.smtp.host", "smtp.gmail.com");
            mailProps.put("mail.smtp.port", "587");
            mailProps.put("mail.smtp.auth", "true");
            mailProps.put("mail.smtp.starttls.enable", "true");
            mailProps.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, APP_PASSWORD);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(USER));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
            msg.setSubject("КРИТИЧНА ПОМИЛКА У ПРОГРАМІ");
            msg.setText("Виникла помилка:\n\n" + e.toString());

            Transport.send(msg);
            System.out.println("Повідомлення про помилку надіслано!");

        } catch (Exception ex) {
            System.out.println("Не вдалося надіслати email:");
            ex.printStackTrace();
        }
    }
}
