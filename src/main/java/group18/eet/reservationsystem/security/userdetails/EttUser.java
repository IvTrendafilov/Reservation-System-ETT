package group18.eet.reservationsystem.security.userdetails;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@Entity
@NoArgsConstructor
@Setter
@Getter
public class EttUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String email;
    private String name;

    @Type(type = "json")
    private List<String> authorities;

    public EttUser(String email, String name, List<String> authorities) {
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }

    public List<? extends GrantedAuthority> getAuthorities() {
        return this.authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public boolean isAdmin() {
        return this.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ADMIN") || a.equals("GLOBAL_ADMIN"));
    }

    public boolean isGlobalAdmin() {
        return this.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("GLOBAL_ADMIN"));
    }
}
