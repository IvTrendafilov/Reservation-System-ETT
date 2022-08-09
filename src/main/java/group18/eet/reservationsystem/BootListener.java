package group18.eet.reservationsystem;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("dev")
@Component
public class BootListener {

    private final EttUserDetailsRepository customUserDetailsRepository;

    @Autowired
    public BootListener(EttUserDetailsRepository customUserDetailsRepository) {
        this.customUserDetailsRepository = customUserDetailsRepository;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        EttUser globalAdmin = new EttUser(
                "group18@test.com",
                "group18",
                List.of("ADMIN", "USER", "GLOBAL_ADMIN"));

        if (customUserDetailsRepository.findByEmail("group18@test.com") == null) {
            customUserDetailsRepository.save(globalAdmin);
        }
    }
}
