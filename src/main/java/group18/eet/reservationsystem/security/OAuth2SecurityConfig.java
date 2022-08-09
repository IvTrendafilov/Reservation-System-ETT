package group18.eet.reservationsystem.security;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import group18.eet.reservationsystem.security.userdetails.EttUserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Profile("production")
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final EttUserDetailsRepository ettUserDetailsRepository;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // only disable these during testing or for non-browser clients
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .logout().logoutSuccessHandler(oidcLogoutSuccessHandler())
                .invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID")
                .and()
                .oauth2Login()
                .failureUrl("/")
                .redirectionEndpoint().baseUri("/api/redirect")
                .and()
                .successHandler(authenticationSuccessHandler())
                .loginPage("/oauth2/authorization/canvas")
        ;

    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("https://ett-res-system.herokuapp.com/");
        return successHandler;
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Object principal = authentication.getPrincipal();
            if (principal instanceof DefaultOidcUser) {
                DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
                EttUser user = ettUserDetailsRepository.findByEmail(oidcUser.getUserInfo().getEmail());
                if (user != null && user.isAdmin()) {
                    response.sendRedirect("/admin");
                    return;
                }
            }
            response.sendRedirect("/");
        };
    }
}
