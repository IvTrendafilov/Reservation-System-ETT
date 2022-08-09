package group18.eet.reservationsystem.security;

import group18.eet.reservationsystem.security.userdetails.EttUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * This config, serves during development, because we cannot use the OAuth2 locally, since the registered url is only the production one
 */
@Profile("dev")
@Configuration
public class DisableSecurityDevConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().cors().disable()
                .authorizeRequests().antMatchers("/**").permitAll()
        .and()
        .addFilterBefore(populateSecurityContext(), UsernamePasswordAuthenticationFilter.class);
    }

    private GenericFilter populateSecurityContext() {
        return new GenericFilter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                TestingAuthenticationToken t = new TestingAuthenticationToken("test", "test");
                t.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(t);
                chain.doFilter(request, response);
            }
        };
    }
}
