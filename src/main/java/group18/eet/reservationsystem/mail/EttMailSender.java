package group18.eet.reservationsystem.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EttMailSender {

    private final JavaMailSender javaMailSender;

    /**
     *
     * @param to - email to which we want to send
     * @param subject - the subject of the emai;
     * @param content - the content, which can be an HTML
     * @throws MessagingException
     */
    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setFrom("noreply@ett-reservation-system.com");
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(mimeMessage);
    }
}
