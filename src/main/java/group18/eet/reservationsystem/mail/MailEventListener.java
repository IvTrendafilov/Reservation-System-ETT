package group18.eet.reservationsystem.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

/**
 * An mail event listener, which will execute an async task if it receives an mail event
 */
@Component
@RequiredArgsConstructor
public class MailEventListener {

    private final EttMailSender ettMailSender;

    @Async
    @EventListener
    public void sendMailEventListener(MailEvent event) {
        try {
            ettMailSender.sendMail(
                    event.getTo(),
                    event.getSubject(),
                    event.getContent()
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    public static class MailEvent {
        private String to;
        private String subject;
        private String content;

        public static MailEvent of(String to, String subject, String content) {
            MailEvent event = new MailEvent();
            event.setTo(to);
            event.setSubject(subject);
            event.setContent(content);
            return event;
        }
    }
}
