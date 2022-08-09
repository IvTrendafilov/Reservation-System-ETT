package group18.eet.reservationsystem.security.userdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EttUserDetailsService {

    private final EttUserDetailsRepository ettUserDetailsRepository;

    @Autowired
    public EttUserDetailsService(EttUserDetailsRepository ettUserDetailsRepository) {
        this.ettUserDetailsRepository = ettUserDetailsRepository;
    }

    public EttUser findByEmail(String email) {
        return ettUserDetailsRepository.findByEmail(email);
    }

    public EttUser findById(Long id) {
        return ettUserDetailsRepository.findById(id).orElse(null);
    }

    public void save(EttUser ettUser) {
        ettUserDetailsRepository.save(ettUser);
    }

    public static EttUser getCurrentPrincipal(EttUserDetailsRepository ettUserDetailsRepository) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
            EttUser user = ettUserDetailsRepository.findByEmail(oidcUser.getUserInfo().getEmail());
            if (user == null) {
                user = new EttUser(
                        oidcUser.getUserInfo().getEmail(),
                        oidcUser.getFullName(),
                        List.of("USER")
                );
                ettUserDetailsRepository.save(user);
            }

            return user;
        } else if (principal.equals("test")) {
            return ettUserDetailsRepository.findByEmail("group18@test.com");
        }

        throw new RuntimeException("Non-existent principal!");
    }
}
